package com.sogou.map.kubbo.rpc.proxy;

import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.InvokerProxy;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.echo.EchoService;

/**
 * AbstractProxyFactory
 * 
 * @author liufuliang
 */
public abstract class AbstractInvokerProxy implements InvokerProxy {

    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        Class<?>[] interfaces = new Class<?>[] {invoker.getInterface(), EchoService.class};
        return getProxy(invoker, interfaces);
    }
    
    public abstract <T> T getProxy(Invoker<T> invoker, Class<?>[] types);

}