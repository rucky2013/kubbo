package com.sogou.map.kubbo.rpc.proxy.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.proxy.AbstractInvokerProxy;
import com.sogou.map.kubbo.rpc.proxy.AbstractServiceInvoker;
import com.sogou.map.kubbo.rpc.proxy.InvokerInvocationHandler;

/**
 * JavaassistRpcProxyFactory

 * @author liufuliang
 */
public class JdkInvokerProxy extends AbstractInvokerProxy {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new AbstractServiceInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName, 
                                      Class<?>[] parameterTypes, 
                                      Object[] arguments) throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Invoker<?> getGenericInvoker(Object proxy, Class<?> typeClazz, URL url) throws RpcException {
        return new AbstractServiceInvoker(proxy, typeClazz, url) {
            @Override
            protected Object doInvoke(Object proxy, String methodName, Class[] parameterTypes, Object[] arguments) throws Throwable{
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
    }

}