package com.sogou.map.kubbo.rpc;

/**
 * InvokerDelegate
 * @author liufuliang
 */
public interface InvokerDelegate<T> extends Invoker<T> {
    Invoker<T> getInvoker();

}