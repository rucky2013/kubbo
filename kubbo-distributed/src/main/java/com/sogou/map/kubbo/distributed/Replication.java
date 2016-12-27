package com.sogou.map.kubbo.distributed;

import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.distributed.replication.FailOverReplication;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * Replication (SPI Singleton ThreadSafe)
 * 服务集群, 对应kubernetes的rc
 * @author liufuliang
 */
@SPI(FailOverReplication.NAME)
public interface Replication {

    /**
     * Merge the directory invokers to a virtual invoker.
     * 
     * @param <T>
     * @param directory
     * @return cluster invoker
     * @throws RpcException
     */
    @Adaptive
    <T> Invoker<T> join(Directory<T> directory) throws RpcException;

}