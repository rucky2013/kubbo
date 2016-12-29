package com.sogou.map.kubbo.distributed;

import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.distributed.loadbalance.RoundRobinLoadBalance;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;

/**
 * LoadBalance.
 * @author liufuliang
 */
@SPI(RoundRobinLoadBalance.NAME)
public interface LoadBalance {

    /**
     * select one invoker in list.
     * 
     * @param invokers invokers.
     * @param url refer url
     * @param invocation invocation.
     * @return selected invoker.
     */
    @Adaptive(Constants.LOADBALANCE_KEY)
    <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation);

}