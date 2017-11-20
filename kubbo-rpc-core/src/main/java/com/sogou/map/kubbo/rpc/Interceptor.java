package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.extension.SPI;

/**
 * Interceptor.
 * 拦截器, 用于实现AOP的Advice
 * 
 * @author liufuliang
 */
@SPI
public interface Interceptor {
    
    /**
     * invoke interceptor.
     * 
     * @param invoker invoker
     * @param invocation invocation.
     * @return invoke result.
     * @throws RpcException
     */
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

}