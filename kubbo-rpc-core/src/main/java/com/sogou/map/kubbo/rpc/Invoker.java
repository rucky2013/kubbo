package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.Node;

/**
 * Invoker
 * @author liufuliang
 */
public interface Invoker<T> extends Node {
    /*
     * consumer-side or provider-side
     */
    public enum Reside {
        NONE,
        CONSUMER,
        PROVIDER
    }

    Reside reside();
    
    /**
     * get service interface.
     * 
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     * 
     * @param invocation
     * @return result
     * @throws RpcException
     */
    Result invoke(Invocation invocation) throws RpcException;

}