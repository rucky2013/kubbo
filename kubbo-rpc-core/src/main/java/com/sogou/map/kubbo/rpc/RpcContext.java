package com.sogou.map.kubbo.rpc;

import java.util.HashMap;
import java.util.concurrent.Future;
import com.sogou.map.kubbo.rpc.protocol.AbstractAttachable;

/**
 * Thread local context. (API, ThreadLocal, ThreadSafe)
 * 异步调用上下文
 * 
 * @author liufuliang
 */
public class RpcContext extends AbstractAttachable<RpcContext>{
    private Future<?> future;

    
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
    public <T> Future<T> getFuture() {
        return (Future<T>) future;
    }

    /**
     * set future.
     * 
     * @param future
     */
    public void setFuture(Future<?> future) {
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
    public static RpcContext getContext() {
        return LOCAL.get();
    }
    
    /**
     * remove context.
     * 
     */
    public static void removeContext() {
        LOCAL.remove();
    }
}