package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;

/**
 * InvokerProxy.
 * 
 * @author liufuliang
 */
@SPI("jdk")
public interface InvokerProxy {

    /**
     * create proxy.
     * 
     * @param invoker
     * @return proxy
     */
    @Adaptive({Constants.PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker) throws RpcException;

    /**
     * create invoker.
     * 
     * @param <T>
     * @param proxy
     * @param type
     * @param url
     * @return invoker
     */
    @Adaptive({Constants.PROXY_KEY})
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;
    
    /**
     * 
     * @param proxyInstance
     * @param type
     * @param url
     * @return invoker
     * @throws RpcException
     */
    @Adaptive({Constants.PROXY_KEY})
    Invoker<?> getGenericInvoker(Object proxyInstance, Class<?> type, URL url) throws RpcException;

}