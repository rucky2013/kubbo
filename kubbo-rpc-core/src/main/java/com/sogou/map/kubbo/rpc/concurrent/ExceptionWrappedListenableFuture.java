/**
 * 
 */
package com.sogou.map.kubbo.rpc.concurrent;

import java.util.concurrent.ExecutionException;

/**
 * ExceptionWrappedListenableFuture
 * 
 * @author liufuliang
 *
 */
public class ExceptionWrappedListenableFuture<V> extends NoOpListenableFuture<V>{

    private Exception exception;
    
    public ExceptionWrappedListenableFuture(Exception e){
        this.exception = e;
    }
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        throw new ExecutionException(exception.getCause());
    }

    @Override
    public ListenableFuture<V> addListener(FutureListener<V> listener) {
        listener.caught(exception);
        return this;
    }
}
