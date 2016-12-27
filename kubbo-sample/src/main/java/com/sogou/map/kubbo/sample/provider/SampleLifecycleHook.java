/**
 * 
 */
package com.sogou.map.kubbo.sample.provider;

import com.sogou.map.kubbo.boot.annotation.Hook;
import com.sogou.map.kubbo.boot.context.ApplicationContext;
import com.sogou.map.kubbo.boot.hook.LifecycleHook;

/**
 * @author liufuliang
 *
 */

@Hook
public class SampleLifecycleHook implements LifecycleHook {

	@Override
	public void destroy(ApplicationContext ctx) {
		System.out.println("<Application LifecycleHook destroy()>");		
	}

	@Override
	public void initialize(ApplicationContext ctx) {
		System.out.println("<Application LifecycleHook initialize()>");
	}
}
