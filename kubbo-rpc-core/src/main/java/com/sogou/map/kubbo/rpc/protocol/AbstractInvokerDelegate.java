/**
 * 
 */package com.sogou.map.kubbo.rpc.protocol;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.InvokerDelegate;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * @author liufuliang
 *
 */
public class AbstractInvokerDelegate<T> implements InvokerDelegate<T> {
    protected Invoker<T> invoker;

    public AbstractInvokerDelegate(Invoker<T> invoker) {
        if (invoker == null) {
            throw new IllegalArgumentException("invoker == NULL");
        }
        this.invoker = invoker;
    }

    @Override
    public Reside reside() {
        return invoker.reside();
    }
    
    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public URL getUrl() {
        return invoker.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return invoker.isAvailable();
    }

    @Override
    public void destroy() {
        invoker.destroy();
        
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }
    
    @Override
    public String toString() {
        return invoker.toString();
    }

}
