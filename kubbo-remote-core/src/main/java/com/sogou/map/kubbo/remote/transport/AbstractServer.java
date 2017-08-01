package com.sogou.map.kubbo.remote.transport;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.Version;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.ExecutorUtils;
import com.sogou.map.kubbo.common.util.NetUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.Server;
import com.sogou.map.kubbo.remote.transport.handler.ChannelHandlers;
import com.sogou.map.kubbo.remote.transport.handler.ExecutorWrappedChannelHandler;

/**
 * AbstractServer
 * 
 * @author liufuliang
 */
public abstract class AbstractServer extends AbstractRole implements Server {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    private InetSocketAddress localAddress;

    private InetSocketAddress bindAddress;

    private int accepts;

    private int idleTimeout = Constants.DEFAULT_IDLE_TIMEOUT; //600 seconds
    
    protected static final String SERVER_THREAD_POOL_NAME  ="kubbo-server-task-handler";
    
    ExecutorService executor;

    public AbstractServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
        localAddress = getUrl().toInetSocketAddress();
        String host = url.getParameter(Constants.ANYHOST_KEY, false) || NetUtils.isInvalidLocalHost(getUrl().getHost()) ? 
                        NetUtils.ANYHOST : getUrl().getHost();
        bindAddress = new InetSocketAddress(host, getUrl().getPort());
        this.accepts = url.getParameter(Constants.ACCEPTS_KEY, Constants.DEFAULT_ACCEPTS);
        this.idleTimeout = url.getParameter(Constants.IDLE_TIMEOUT_KEY, Constants.DEFAULT_IDLE_TIMEOUT);
        try {
            start();
            if (logger.isInfoEnabled()) {
                logger.info("Start " + getClass().getSimpleName() 
                        + " bind " + getBindAddress() 
                        + ", kubbo version " + Version.getVersion());
            }
        } catch (Throwable t) {
            throw new RemotingException(url.toInetSocketAddress(), null, "Failed to bind " + getClass().getSimpleName() 
                                        + " on " + getLocalAddress() + ", cause: " + t.getMessage(), t);
        }
        if (handler instanceof ExecutorWrappedChannelHandler ){
            executor = ((ExecutorWrappedChannelHandler)handler).getExecutor();
        }
    }

    protected static ChannelHandler wrapChannelHandler(URL url, ChannelHandler handler){
        url = ExecutorUtils.setThreadName(url, SERVER_THREAD_POOL_NAME);
        url = url.addParameterIfAbsent(Constants.THREADPOOL_KEY, Constants.DEFAULT_SERVER_THREADPOOL);
        return ChannelHandlers.wrap(handler, url);
    }
    
    @Override
    public void reset(URL url) {
        if (url == null) {
            return;
        }
        //accepts
        try {
            if (url.hasParameter(Constants.ACCEPTS_KEY)) {
                int a = url.getParameter(Constants.ACCEPTS_KEY, 0);
                if (a > 0) {
                    this.accepts = a;
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        
        //idle timeout
        try {
            if (url.hasParameter(Constants.IDLE_TIMEOUT_KEY)) {
                int t = url.getParameter(Constants.IDLE_TIMEOUT_KEY, 0);
                if (t > 0) {
                    this.idleTimeout = t;
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        
        //thread pool
        try {
            if (url.hasParameter(Constants.MAX_THREADS_KEY) 
                    && executor instanceof ThreadPoolExecutor && !executor.isShutdown()) {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                int threads = url.getParameter(Constants.MAX_THREADS_KEY, 0);
                int max = threadPoolExecutor.getMaximumPoolSize();
                int core = threadPoolExecutor.getCorePoolSize();
                if (threads > 0 && (threads != max || threads != core)) {
                    if (threads < core) {
                        threadPoolExecutor.setCorePoolSize(threads);
                        if (core == max) {
                            threadPoolExecutor.setMaximumPoolSize(threads);
                        }
                    } else {
                        threadPoolExecutor.setMaximumPoolSize(threads);
                        if (core == max) {
                            threadPoolExecutor.setCorePoolSize(threads);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        super.setUrl(getUrl().addParameters(url.getParameters()));
    }

    @Override
    public void send(Object message, boolean blocking) throws RemotingException {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            if (channel.isConnected()) {
                channel.send(message, blocking);
            }
        }
    }
    
    @Override
    public void close() {
        if (logger.isInfoEnabled()) {
            logger.info("Close " + getClass().getSimpleName() + " bind " + getBindAddress());
        }
        ExecutorUtils.shutdownNow(executor ,100);
        try {
            super.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        
        //extra
        try {
            stop();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }
    
    @Override
    public void close(int timeout) {
        ExecutorUtils.shutdownGracefully(executor ,timeout);
        close();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }
    
    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    public int getAccepts() {
        return accepts;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    @Override
    public void onConnected(Channel ch) throws RemotingException {
        Collection<Channel> channels = getChannels();
        if (accepts > 0 && channels.size() > accepts) {
            logger.warn("Close channel " + ch + ", cause: The server " + ch.getLocalAddress() + " connections greater than max accepts " + accepts);
            ch.close();
            return;
        }
        super.onConnected(ch);
    }
    
    @Override
    public void onDisconnected(Channel ch) throws RemotingException {
        Collection<Channel> channels = getChannels();
        if (channels.size() == 0){
            if(logger.isDebugEnabled()){
                logger.warn("All clients has discontected from " + ch.getLocalAddress() + ". You can graceful shutdown now.");
            }
        }
        super.onDisconnected(ch);
    }
    
    protected abstract void start() throws Throwable;
    
    protected abstract void stop() throws Throwable;
}