package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.extension.SPI;

/**
 * Filter.
 * 
 * @author liufuliang
 */
@SPI
public interface Filter {
    
    /**
     * invoke filter.
     * 
     * @param invoker invoker
     * @param invocation invocation.
     * @return invoke result.
     * @throws RpcException
     */
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

}