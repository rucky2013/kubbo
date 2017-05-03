package com.sogou.map.kubbo.rpc.proxy.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.lang.Defaults;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.proxy.AbstractInvokerProxy;
import com.sogou.map.kubbo.rpc.proxy.AbstractServiceInvoker;
import com.sogou.map.kubbo.rpc.proxy.InvokerInvocationHandler;

/**
 * JdkInvokerProxy

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
                return invokeMatchedMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Invoker<?> getGenericInvoker(Object proxy, Class<?> typeClazz, URL url) throws RpcException {
        
        return new AbstractServiceInvoker(proxy, typeClazz, url) {
            @Override
            protected Object doInvoke(Object proxy, String methodName, Class[] parameterTypes, Object[] arguments) throws Throwable{
                return invokeMatchedMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }

    private Object invokeMatchedMethod(Object proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Exception{
        try{
            Method method = proxy.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(proxy, arguments);
        } catch(NoSuchMethodException e){
            Method[] proxyMethods = proxy.getClass().getMethods();
            for(Method proxyMethod : proxyMethods){
                Class<?>[] types = proxyMethod.getParameterTypes();
                if(isTypesMatchPrefix(parameterTypes, types)){
                    Object[] invokeArguments = Arrays.copyOf(arguments, types.length);
                    for(int i = arguments.length; i < invokeArguments.length; ++i){
                        invokeArguments[i] = Defaults.defaultValue(types[i]);
                    }
                    return proxyMethod.invoke(proxy, invokeArguments);
                }
            }
            throw e;
        }
    }
    
    private boolean isTypesMatchPrefix(Class<?>[] toBeMatchedTypes, Class<?>[] types){
        if(toBeMatchedTypes.length >= types.length){
            return false;
        }
        for(int i=0; i < toBeMatchedTypes.length; ++i){
            Class<?> toBeMatchedType = toBeMatchedTypes[i];
            Class<?> type = types[i];
            if(!toBeMatchedType.getName().equals(type.getName())){
                return false;
            }
        }
        return true;
    }

}