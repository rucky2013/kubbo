/**
 * 
 */
package com.sogou.map.kubbo.rpc.concurrent;

/**
 * @author liufuliang
 *
 */
public interface FutureListener<V> {
    /**
     * done.
     * 
     * @param response
     */
    void done(V result);

    /**
     * caught exception.
     * 
     * @param exception
     */
    void caught(Throwable exception);
}
