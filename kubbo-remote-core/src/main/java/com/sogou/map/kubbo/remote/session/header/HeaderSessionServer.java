package com.sogou.map.kubbo.remote.session.header;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.NamedThreadFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.Server;
import com.sogou.map.kubbo.remote.heartbeat.HeartBeatTask;
import com.sogou.map.kubbo.remote.session.SessionChannel;
import com.sogou.map.kubbo.remote.session.SessionServer;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.remote.transport.AbstractServerDelegate;


/**
 * HeaderSessionServer
 * 
 * @author liufuliang
 */
public class HeaderSessionServer extends AbstractServerDelegate implements SessionServer {
    
    protected final Logger        logger = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1, new NamedThreadFactory( "KubboHeartbeat", true));

    // 心跳定时器
    private ScheduledFuture<?> heatbeatTimer;

    // 心跳超时，毫秒。缺省0，不会执行心跳。
    private int                            heartbeat;

    private int                            heartbeatTimeout;
    
    private volatile boolean closed = false;

    public HeaderSessionServer(Server server) {
        super(server);
        this.heartbeat = server.getUrl().getParameter(Constants.HEARTBEAT_KEY, 0);
        this.heartbeatTimeout = server.getUrl().getParameter(Constants.HEARTBEAT_TIMEOUT_KEY, heartbeat * 3);
        if (heartbeatTimeout < heartbeat * 2) {
            throw new IllegalStateException("heartbeatTimeout < heartbeatInterval * 2");
        }
        startHeatbeatTimer();
    }

    
    private boolean isRunning() {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            if (InternalResponseFuture.hasFuture(channel)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() {
        doClose();
        super.close();
    }

    @Override
    public void close(final int timeout) {
        if (timeout > 0) {
            final long max = (long) timeout;
            final long start = System.currentTimeMillis();
            if (getUrl().getParameter(Constants.CHANNEL_SEND_READONLYEVENT_KEY, false)){
                notifyChannelReadOnly();
            }
            while (HeaderSessionServer.this.isRunning() 
                    && System.currentTimeMillis() - start < max) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        doClose();
        super.close(timeout);
    }
    
    private void notifyChannelReadOnly(){
        Request request = new Request();
        request.setEvent(Request.READONLY_EVENT);
        request.setTwoWay(false);
        request.setVersion(Constants.DEFAULT_VERSION);
        
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            try {
                if (channel.isConnected())channel.send(request, getUrl().getParameter(Constants.CHANNEL_SEND_READONLYEVENT_BLOCKING_KEY, true));
            } catch (RemotingException e) {
                logger.warn("send connot write messge error.", e);
            }
        }
    }
    
    private void doClose() {
        if (closed) {
            return;
        }
        closed = true;
        stopHeartbeatTimer();
        try {
            scheduled.shutdown();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    public Collection<SessionChannel> getSessionChannels() {
        Collection<SessionChannel> sessionChannels  = new ArrayList<SessionChannel>();
        Collection<Channel> channels = getServer().getChannels();
        if (channels != null && channels.size() > 0) {
            for (Channel channel : channels) {
                sessionChannels.add(HeaderSessionChannel.getOrAddChannel(channel));
            }
        }
        return sessionChannels;
    }

    public SessionChannel getSessionChannel(InetSocketAddress remoteAddress) {
        Channel channel = getServer().getChannel(remoteAddress);
        return HeaderSessionChannel.getOrAddChannel(channel);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<Channel> getChannels() {
        return (Collection)getSessionChannels();
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return getSessionChannel(remoteAddress);
    }

    @Override
    public void reset(URL url) {
        super.reset(url);
        try {
            if (url.hasParameter(Constants.HEARTBEAT_KEY)
                    || url.hasParameter(Constants.HEARTBEAT_TIMEOUT_KEY)) {
                int h = url.getParameter(Constants.HEARTBEAT_KEY, heartbeat);
                int t = url.getParameter(Constants.HEARTBEAT_TIMEOUT_KEY, h * 3);
                if (t < h * 2) {
                    throw new IllegalStateException("heartbeatTimeout < heartbeatInterval * 2");
                }
                if (h != heartbeat || t != heartbeatTimeout) {
                    heartbeat = h;
                    heartbeatTimeout = t;
                    startHeatbeatTimer();
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }
    
    @Override
    public void send(Object message) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send message " + message + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        super.send(message);
    }

    @Override
    public void send(Object message, boolean blocking) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send message " + message + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        super.send(message, blocking);
    }

    private void startHeatbeatTimer() {
        stopHeartbeatTimer();
        if (heartbeat > 0) {
            heatbeatTimer = scheduled.scheduleWithFixedDelay(
                    new HeartBeatTask( new HeartBeatTask.ChannelProvider() {
                        public Collection<Channel> getChannels() {
                            return Collections.unmodifiableCollection(
                                    HeaderSessionServer.this.getChannels() );
                        }
                    }, heartbeat, heartbeatTimeout),
                    heartbeat, heartbeat,TimeUnit.MILLISECONDS);
        }
    }

    private void stopHeartbeatTimer() {
        try {
            ScheduledFuture<?> timer = heatbeatTimer;
            if (timer != null && ! timer.isCancelled()) {
                timer.cancel(true);
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        } finally {
            heatbeatTimer =null;
        }
    }

}