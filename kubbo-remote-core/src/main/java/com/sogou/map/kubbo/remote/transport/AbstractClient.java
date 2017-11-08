package com.sogou.map.kubbo.remote.transport;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.Version;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.threadpool.NamedThreadFactory;
import com.sogou.map.kubbo.common.util.ExecutorUtils;
import com.sogou.map.kubbo.common.util.NetUtils;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Client;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.transport.handler.ChannelHandlers;
import com.sogou.map.kubbo.remote.transport.handler.ExecutorWrappedChannelHandler;

/**
 * AbstractClient
 * 
 * @author liufuliang
 */
public abstract class AbstractClient extends AbstractRole implements Client {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    protected static final String CLIENT_THREAD_POOL_NAME = "kubbo-client-task-handler";

    private final Lock connectLock = new ReentrantLock();

    private static final ScheduledThreadPoolExecutor reconnectExecutorService = new ScheduledThreadPoolExecutor(2,
            new NamedThreadFactory("kubbo-client-connection-active-checker", true));

    private volatile ScheduledFuture<?> reconnectExecutorFuture = null;

    protected volatile ExecutorService executor;

    private final boolean isSendReconnect;

    private ConnectionState connectionState = new ConnectionState();

    public AbstractClient(URL url, ChannelHandler handler) throws RemoteException {
        super(url, handler);
        isSendReconnect = url.getParameter(Constants.SEND_RECONNECT_KEY, false);

        // Open, initialize
        try {
            start();
        } catch (Throwable t) {
            close();
            throw new RemoteException(url.toInetSocketAddress(), null,
                    "Failed to start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress()
                    + " connect to the server " + getRemoteAddress() 
                    + ", cause: " + t.getMessage(), t);
        }

        // Connect
        try {
            connect();
        } catch (RemoteException t) {
            if (url.getParameter(Constants.CHECK_KEY, true)) {
                close();
                throw t;
            } else {
                logger.warn("Failed to start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress()
                        + " connect to the server " + getRemoteAddress()
                        + " (check == false, ignore and retry later!), cause: " + t.getMessage(), t);
            }
        } catch (Throwable t) {
            close();
            throw new RemoteException(url.toInetSocketAddress(), null,
                    "Failed to start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress()
                    + " connect to the server " + getRemoteAddress() 
                    + ", cause: " + t.getMessage(), t);
        }

        // 保留线程池, 用于关闭
        // 如果handler经过了wrap, 将会在独立的线程池内执行,
        // 否则将会在io线程池内执行, 如netty线程池
        if (handler instanceof ExecutorWrappedChannelHandler) {
            executor = ((ExecutorWrappedChannelHandler) handler).getExecutor();
        }
    }

    protected static ChannelHandler wrapChannelHandler(URL url, ChannelHandler handler) {
        url = ExecutorUtils.setThreadName(url, CLIENT_THREAD_POOL_NAME);
        url = url.addParameterIfAbsent(Constants.THREADPOOL_KEY, Constants.DEFAULT_CLIENT_THREADPOOL);
        return ChannelHandlers.wrap(handler, url);
    }

    protected void connect() throws RemoteException {
        connectLock.lock();
        try {
            if (isConnected()) {
                return;
            }
            doConnect();
            startConnectionStateCheckTask();
            if (!isConnected()) {
                throw new RemoteException(this,
                        "Failed to connect to server " + getRemoteAddress() 
                            + " from " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress() 
                            + ", cause: Connection wait timeout: " + getConnectTimeout() + "ms.");
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Successfully connect to server " + getRemoteAddress() 
                                    + " from " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress() 
                                    + ", channel is " + this.getChannel() 
                                    + ", kubbo version " + Version.getVersion());
                }
            }
            connectionState.reset();
        } catch (RemoteException e) {
            throw e;
        } catch (Throwable e) {
            throw new RemoteException(this, 
                    "Failed to connect to server " + getRemoteAddress() 
                        + " from " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress() 
                        + ", cause: " + e.getMessage(), e);
        } finally {
            connectLock.unlock();
        }
    }

    public void disconnect() {
        connectLock.lock();
        try {
            stopConnectionStateCheckTask();
            try {
                Channel channel = getChannel();
                if (channel != null) {
                    channel.close();
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
            try {
                doDisConnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        } finally {
            connectLock.unlock();
        }
    }

    /**
     * init reconnect thread
     */
    private synchronized void startConnectionStateCheckTask() {
        // reconnect=false to close reconnect
        int reconnectPeriod = getReconnectPeriod(getUrl());
        if (reconnectPeriod > 0 && (reconnectExecutorFuture == null || reconnectExecutorFuture.isCancelled())) {
            Runnable task = new Runnable() {
                public void run() {
                    try {
                        if (!isConnected()) {
                            connect();
                        } else {
                            connectionState.active();
                        }
                    } catch (Throwable t) {
                        String errorMsg = "Connection inactive. url: " + getUrl();
                        connectionState.inActive(errorMsg, t);
                    }
                }
            };
            reconnectExecutorFuture = reconnectExecutorService.scheduleWithFixedDelay(task, reconnectPeriod,
                    reconnectPeriod, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * @param url
     * @return 0-false
     */
    private static int getReconnectPeriod(URL url) {
        int reconnectPeriod;
        String param = url.getParameter(Constants.RECONNECT_KEY);
        if (StringUtils.isBlank(param) || "true".equalsIgnoreCase(param)) {
            reconnectPeriod = Constants.DEFAULT_RECONNECT_PERIOD;
        } else if ("false".equalsIgnoreCase(param)) {
            reconnectPeriod = 0;
        } else {
            try {
                reconnectPeriod = Integer.parseInt(param);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "reconnect param must be nonnegative integer or false/true. input is:" + param);
            }
            if (reconnectPeriod < 0) {
                throw new IllegalArgumentException(
                        "reconnect param must be nonnegative integer or false/true. input is:" + param);
            }
        }
        return reconnectPeriod;
    }

    private synchronized void stopConnectionStateCheckTask() {
        try {
            if (reconnectExecutorFuture != null && !reconnectExecutorFuture.isDone()) {
                reconnectExecutorFuture.cancel(true);
                reconnectExecutorService.purge();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public InetSocketAddress getConnectAddress() {
        return new InetSocketAddress(getUrl().getHost(), getUrl().getPort());
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        Channel channel = getChannel();
        if (channel == null)
            return getUrl().toInetSocketAddress();
        return channel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        Channel channel = getChannel();
        if (channel == null)
            return InetSocketAddress.createUnresolved(NetUtils.getHostAddress(), 0);
        return channel.getLocalAddress();
    }

    @Override
    public boolean isConnected() {
        Channel channel = getChannel();
        if (channel == null)
            return false;
        return channel.isConnected();
    }

    @Override
    public Object getAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null)
            return null;
        return channel.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        Channel channel = getChannel();
        if (channel == null)
            return;
        channel.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null)
            return;
        channel.removeAttribute(key);
    }

    @Override
    public boolean hasAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null)
            return false;
        return channel.hasAttribute(key);
    }

    @Override
    public void send(Object message, boolean blocking) throws RemoteException {
        if (isSendReconnect && !isConnected()) {
            connect();
        }
        Channel channel = getChannel();
        if (channel == null || !channel.isConnected()) {
            throw new RemoteException(this, "message can not send, because channel is closed . url:" + getUrl());
        }
        channel.send(message, blocking);
    }

    @Override
    public void reconnect() throws RemoteException {
        disconnect();
        connect();
    }

    @Override
    public void close() {
        // close thread pool
        try {
            if (executor != null) {
                ExecutorUtils.shutdownNow(executor, 100);
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        // release
        try {
            super.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        // disconnect
        try {
            disconnect();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        // extra close
        try {
            stop();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public void close(int timeout) {
        ExecutorUtils.shutdownGracefully(executor, timeout);
        close();
    }

    @Override
    public String toString() {
        return getClass().getName() + " [" + getLocalAddress() + " -> " + getRemoteAddress() + "]";
    }

    /**
     * start client.
     * 
     * @throws Throwable
     */
    protected abstract void start() throws Throwable;

    /**
     * stop client.
     * 
     * @throws Throwable
     */
    protected abstract void stop() throws Throwable;

    /**
     * Connect to server.
     * 
     * @throws Throwable
     */
    protected abstract void doConnect() throws Throwable;

    /**
     * disConnect to server.
     * 
     * @throws Throwable
     */
    protected abstract void doDisConnect() throws Throwable;

    /**
     * Get the channel.
     * 
     * @return channel
     */
    protected abstract Channel getChannel();

    class ConnectionState {
        private static final int CONNECTION_INACTIVE_WARNING_INTERVAL = 30;

        private final AtomicInteger reconnectCount = new AtomicInteger(0);

        // the last successed connected time
        private long lastConnectedTime = System.currentTimeMillis();

        public void active() {
            this.lastConnectedTime = System.currentTimeMillis();
        }

        public void inActive(String message, Throwable t) {
            if (reconnectCount.getAndIncrement() % CONNECTION_INACTIVE_WARNING_INTERVAL == 0) {
                logger.warn(message, t);
            }
        }

        public void reset() {
            reconnectCount.set(0);
        }

        public long getLastConnectedTime() {
            return lastConnectedTime;
        }

        public void setLastConnectedTime(long lastConnectedTime) {
            this.lastConnectedTime = lastConnectedTime;
        }

    }

}
