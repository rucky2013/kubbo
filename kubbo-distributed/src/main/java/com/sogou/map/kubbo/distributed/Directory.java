package com.sogou.map.kubbo.distributed;

import java.util.List;

import com.sogou.map.kubbo.common.Node;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * Discovery. 
 * 对应一个接口的Invoker列表
 * @author liufuliang
 */
public interface Directory<T> extends Node {
    
    /**
     * get service type.
     * 
     * @return service type.
     */
    Class<T> getInterface();

    /**
     * list invokers.
     * 
     * @return invokers
     */
    List<Invoker<T>> list(Invocation invocation) throws RpcException;
    
}