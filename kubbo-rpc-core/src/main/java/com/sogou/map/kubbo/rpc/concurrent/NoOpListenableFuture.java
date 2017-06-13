/**
 * 
 */
package com.sogou.map.kubbo.rpc.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author liufuliang
 *
 */
public class NoOpListenableFuture<V> implements ListenableFuture<V>{

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        return true;
    }
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }
    
    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException,
            TimeoutException {
        return get();
    }

    @Override
    public ListenableFuture<V> addListener(FutureListener<V> listener) {
        return this;
    }

}
