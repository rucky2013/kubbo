package com.sogou.map.kubbo.rpc;

/**
 * Exporter.
 * @author liufuliang
 */
public interface Exporter<T> {
    
    /**
     * get invoker.
     * 
     * @return invoker
     */
    Invoker<T> getInvoker();
    
    /**
     * unexport.
     * 
     */
    void unexport();

}