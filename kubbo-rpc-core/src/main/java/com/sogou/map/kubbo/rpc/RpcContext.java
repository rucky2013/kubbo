package com.sogou.map.kubbo.rpc;

import java.util.HashMap;

import com.sogou.map.kubbo.rpc.concurrent.ListenableFuture;
import com.sogou.map.kubbo.rpc.protocol.AbstractAttachable;

/**
 * Thread local context.
 * 异步调用上下文
 * 
 * @author liufuliang
 */
public class RpcContext extends AbstractAttachable<RpcContext>{
    private ListenableFuture<?> future;

    
    protected RpcContext() {
        super(new HashMap<String, String>());
    }

    /**
     * get future.
     * 
     * @param <T>
     * @return future
     */
    @SuppressWarnings("unchecked")
    public <T> ListenableFuture<T> getFuture() {
        return (ListenableFuture<T>) future;
    }

    /**
     * set future.
     * 
     * @param future
     */
    public void setFuture(ListenableFuture<?> future) {
        this.future = future;
    }
    
    
    private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    /**
     * get context.
     * 
     * @return context
     */
    public static RpcContext get() {
        return LOCAL.get();
    }
    
    /**
     * remove context.
     * 
     */
    public static void remove() {
        LOCAL.remove();
    }
}