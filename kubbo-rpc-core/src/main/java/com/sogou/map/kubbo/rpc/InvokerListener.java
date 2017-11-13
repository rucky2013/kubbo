package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.extension.SPI;

/**
 * InvokerListener.
 * 
 * @author liufuliang
 */
@SPI
public interface InvokerListener {

    /**
     * The invoker referred
     * 
     * @see com.sogou.map.kubbo.rpc.Protocol#refer(Class, URL)
     * @param invoker
     * @throws RpcException
     */
    void referred(Invoker<?> invoker) throws RpcException;

    /**
     * The invoker destroyed.
     * 
     * @see com.sogou.map.kubbo.rpc.Invoker#destroy()
     * @param invoker
     */
    void destroyed(Invoker<?> invoker);

}