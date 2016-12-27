package com.sogou.map.kubbo.distributed.replication;

import com.sogou.map.kubbo.distributed.Directory;
import com.sogou.map.kubbo.distributed.Replication;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * 失败转移，当出现失败，重试其它服务器，通常用于读操作，但重试会带来更长延迟。 
 * 
 * <a href="http://en.wikipedia.org/wiki/Failover">Failover</a>
 * 
 * @author liufuliang
 */
public class FailOverReplication implements Replication {

    public final static String NAME = "failover";

    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FailOverReplicationInvoker<T>(directory);
    }

}