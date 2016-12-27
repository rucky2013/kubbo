/**
 * 
 */
package com.sogou.map.kubbo.common.http;


/**
 * @author liufuliang
 *
 */
public interface Watcher<T> {
	  void received(T obj);

	  /**
	   * Run when the watcher finally closes.
	   *
	   * @param cause What caused the watcher to be closed. Null means normal close.
	   */
	  void exceptionCaught(KubboHttpException exception);
}
