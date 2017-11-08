package com.sogou.map.kubbo.remote.transport.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.threadpool.NamedThreadFactory;
import com.sogou.map.kubbo.common.threadpool.ThreadPool;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemoteExecutionException;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.transport.handler.ChannelEventRunnable.ChannelState;

/**
 * ExecutorWrappedChannelHandler
 * @author liufuliang
 *
 */
public class ExecutorWrappedChannelHandler extends AbstractChannelHandlerDelegate {

    protected static final Logger logger = LoggerFactory.getLogger(ExecutorWrappedChannelHandler.class);

    protected static final ExecutorService SHARED_EXECUTOR = Executors.newCachedThreadPool(new NamedThreadFactory("kubbo-shared-pool", true));
    
    protected final ExecutorService executor;
    
    protected final URL url;
    
    public ExecutorWrappedChannelHandler(ChannelHandler handler, URL url) {
        super(handler);
        this.url = url;
        executor = (ExecutorService) Extensions.getAdaptiveExtension(ThreadPool.class).getExecutor(url);
    }

    @Override
    public void onConnected(Channel channel) throws RemoteException {
        ExecutorService cexecutor = getExecutorSafely(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CONNECTED));
        }catch (Throwable t) {
            throw new RemoteExecutionException("onConnected event", channel, getClass() + " error when process onConnected event ." , t);
        }
    }
    
    @Override
    public void onDisconnected(Channel channel) throws RemoteException {
        ExecutorService cexecutor = getExecutorSafely(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.DISCONNECTED));
        }catch (Throwable t) {
            throw new RemoteExecutionException("onDisconnected event", channel, getClass() + " error when process onDisconnected event ." , t);
        }
    }

    @Override
    public void onReceived(Channel channel, Object message) throws RemoteException {
        ExecutorService cexecutor = getExecutorSafely();
        try {
            cexecutor.execute(new ChannelEventRunnable(channel, handler, ChannelState.RECEIVED, message));
        } catch (Throwable t) {
            throw new RemoteExecutionException(message, channel, getClass() + " error when process onReceived event .", t);
        }
    }

    @Override
    public void onExceptonCaught(Channel channel, Throwable exception) throws RemoteException {
        ExecutorService cexecutor = getExecutorSafely(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CAUGHT, exception));
        }catch (Throwable t) {
            throw new RemoteExecutionException("onExceptonCaught event", channel, getClass() + " error when process onExceptonCaught event ." , t);
        }
    }
    
    protected ExecutorService getExecutorSafely() {
        ExecutorService cexecutor = executor;
        if (cexecutor == null || cexecutor.isShutdown()) { 
            cexecutor = SHARED_EXECUTOR;
        }
        return cexecutor;
    }
    
    public ExecutorService getExecutor() {
        return executor;
    }
    
    public URL getUrl() {
        return url;
    }
    
    public void close() {
        try {
            if (executor instanceof ExecutorService) {
                ((ExecutorService)executor).shutdown();
            }
        } catch (Throwable t) {
            logger.warn("fail to destroy thread pool of server: " + t.getMessage(), t);
        }
    }

}