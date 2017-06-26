/**
 * 
 */
package com.sogou.map.kubbo.rpc.concurrent;

/**
 * ListenableFuture extends java Future
 * @author liufuliang
 *
 */
public interface ListenableFuture<V> extends java.util.concurrent.Future<V> {
    ListenableFuture<V> addListener(FutureListener<V> listener);
}
