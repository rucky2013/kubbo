/**
 * 
 */
package com.sogou.map.kubbo.boot.hook;

import com.sogou.map.kubbo.boot.context.ApplicationContext;

/**
 * @author liufuliang
 *
 */
public interface LifecycleHook {
	void destroy(ApplicationContext ctx);
	void initialize(ApplicationContext ctx);
}
