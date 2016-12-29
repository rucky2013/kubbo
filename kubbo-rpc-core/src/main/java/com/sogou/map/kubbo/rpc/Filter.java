package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.extension.SPI;

/**
 * Filter. (SPI, Singleton, ThreadSafe)
 * 
 * @author liufuliang
 */
@SPI
public interface Filter {

    /**
     * do invoke filter.
     * 
     * <code>
     * // before filter
     * Result result = invoker.invoke(invocation);
     * // after filter
     * return result;
     * </code>
     * 
     * @see kubbo.rpc.Invoker#invoke(Invocation)
     * @param invoker service
     * @param invocation invocation.
     * @return invoke result.
     * @throws RpcException
     */
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

}