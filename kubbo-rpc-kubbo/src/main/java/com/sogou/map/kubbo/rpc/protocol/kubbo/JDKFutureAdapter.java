package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.ResponseFuture;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * JDKFutureAdapter
 * 
 * @author liufuliang
 */
public class JDKFutureAdapter<V> implements Future<V> {
    
    private final ResponseFuture future;

    public JDKFutureAdapter(ResponseFuture future){
        this.future = future;
    }

    public ResponseFuture getResponseFuture() {
        return future;
    }

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
        return future.isDone();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get() throws InterruptedException, ExecutionException {
        try {
            return (V) (((Result) future.get()).recreate());
        } catch (RemotingException e) {
            throw new ExecutionException(e.getMessage(), e);
        } catch (Throwable e) {
            throw new RpcException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        int timeoutInMillis = (int) unit.convert(timeout, TimeUnit.MILLISECONDS);
        try {
            return (V) (((Result) future.get(timeoutInMillis)).recreate());
        } catch (com.sogou.map.kubbo.remote.TimeoutException e) {
            throw new TimeoutException(StringUtils.toString(e));
        } catch (RemotingException e) {
            throw new ExecutionException(e.getMessage(), e);
        } catch (Throwable e) {
            throw new RpcException(e);
        }
    }

}