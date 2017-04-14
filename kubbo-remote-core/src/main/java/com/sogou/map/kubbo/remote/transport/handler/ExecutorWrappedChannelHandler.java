package com.sogou.map.kubbo.remote.transport.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.threadpool.ThreadPool;
import com.sogou.map.kubbo.common.util.NamedThreadFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.ExecutionException;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.transport.handler.ChannelEventRunnable.ChannelState;


public class ExecutorWrappedChannelHandler extends AbstractChannelHandlerDelegate {
    protected static final Logger logger = LoggerFactory.getLogger(ExecutorWrappedChannelHandler.class);

    protected static final ExecutorService SHARED_EXECUTOR = Executors.newCachedThreadPool(new NamedThreadFactory("KubboSharedHandler", true));
    
    protected final ExecutorService executor;
    
    protected final URL url;
    
    public ExecutorWrappedChannelHandler(ChannelHandler handler, URL url) {
        super(handler);
        this.url = url;
        executor = (ExecutorService) Extensions.getAdaptiveExtension(ThreadPool.class).getExecutor(url);
    }

    @Override
    public void onConnected(Channel channel) throws RemotingException {
        ExecutorService cexecutor = getExecutorSafely(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CONNECTED));
        }catch (Throwable t) {
            throw new ExecutionException("connect event", channel, getClass()+" error when process connected event ." , t);
        }
    }
    
    @Override
    public void onDisconnected(Channel channel) throws RemotingException {
        ExecutorService cexecutor = getExecutorSafely(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.DISCONNECTED));
        }catch (Throwable t) {
            throw new ExecutionException("disconnect event", channel, getClass()+" error when process disconnected event ." , t);
        }
    }

    @Override
    public void onReceived(Channel channel, Object message) throws RemotingException {
        ExecutorService cexecutor = getExecutorSafely();
        try {
            cexecutor.execute(new ChannelEventRunnable(channel, handler, ChannelState.RECEIVED, message));
        } catch (Throwable t) {
            throw new ExecutionException(message, channel, getClass() + " error when process received event .", t);
        }
    }

    @Override
    public void onExceptonCaught(Channel channel, Throwable exception) throws RemotingException {
        ExecutorService cexecutor = getExecutorSafely(); 
        try{
            cexecutor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CAUGHT, exception));
        }catch (Throwable t) {
            throw new ExecutionException("caught event", channel, getClass()+" error when process caught event ." , t);
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