package com.sogou.map.kubbo.distributed.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.utils.AtomicPositiveInteger;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.utils.RpcHelper;

/**
 * Round robin load balance.
 *
 * @author liuliang
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "roundrobin";

    private final ConcurrentMap<String, AtomicPositiveInteger> sequences = new ConcurrentHashMap<String, AtomicPositiveInteger>();

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        String key = RpcHelper.serviceKey(invokers.get(0).getUrl()) + "." + invocation.getMethodName();
        int length = invokers.size(); // 总个数
        
        //sequence
        AtomicPositiveInteger sequence = sequences.get(key);
        if (sequence == null) {
            sequences.putIfAbsent(key, new AtomicPositiveInteger());
            sequence = sequences.get(key);
        }
        int currentSequence = sequence.getAndIncrement();
        
        //weight
        int maxWeight = 0; // 最大权重
        int minWeight = Integer.MAX_VALUE; // 最小权重
        List<IntegerWrapper> weights = new ArrayList<IntegerWrapper>(invokers.size());
        int weightSum = 0;
        for (int i = 0; i < length; i++) {
            int weight = getWeight(invokers.get(i), invocation);
            maxWeight = Math.max(maxWeight, weight); // 累计最大权重
            minWeight = Math.min(minWeight, weight); // 累计最小权重
            weights.add(new IntegerWrapper(weight));
            weightSum += weight;
        }

        //select by weight
        if (maxWeight > 0 && minWeight < maxWeight) { // 权重不一样
            int mod = currentSequence % weightSum;
            for (int i = 0; i < maxWeight; i++) {
                for (int w=0; w < weights.size(); ++w) {
                    IntegerWrapper weigth = weights.get(i);
                    if (mod == 0 && weigth.getValue() > 0) {
                        return invokers.get(w);
                    }
                    if (weigth.getValue() > 0) {
                        weigth.decrement();
                        mod--;
                    }
                }
            }
        }
        // 取模轮循
        return invokers.get(currentSequence % length);
    }
    
    private final class IntegerWrapper {
        private int value;

        public IntegerWrapper(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        public void decrement() {
            this.value--;
        }
    }

}