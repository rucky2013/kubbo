package com.sogou.map.kubbo.rpc.proxy;

import java.lang.reflect.InvocationTargetException;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.RpcResult;

/**
 * AbstractProvider
 * 
 * @author liufuliang
 */
public abstract class AbstractProviderInvoker<T> implements Invoker<T> {
    
    private final T service;
    
    private final Class<T> type;
    
    private final URL url;

    public AbstractProviderInvoker(T service, Class<T> type, URL url){
        if (service == null) {
            throw new IllegalArgumentException("service == NULL");
        }
        if (type == null) {
            throw new IllegalArgumentException("type == NULL");
        }
        if (!type.isInstance(service)) {
            throw new IllegalArgumentException(service.getClass().getName() + " not implement interface " + type);
        }
        this.service = service;
        this.type = type;
        this.url = url;
    }
    
    @Override
    public Reside reside(){
        return Reside.PROVIDER;
    }
    
    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        try {
            return new RpcResult(doInvoke(
                    service, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments()));
        } catch (InvocationTargetException e) {
            return new RpcResult(e.getTargetException());
        } catch (Throwable e) {
            throw new RpcException("Failed to invoke proxy method " + invocation.getMethodName() + " to " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }
    
    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;

    @Override
    public String toString() {
        return getInterface() + " -> " + getUrl()==null ? " " : getUrl().toString();
    }

    
}