package com.sogou.map.kubbo.sample;

import com.sogou.map.kubbo.boot.annotation.Hook;
import com.sogou.map.kubbo.boot.context.ApplicationContext;
import com.sogou.map.kubbo.boot.hook.LifecycleHook;

/**
 * ApplicationLifecycleHook
 * @author liufuliang
 */

@Hook
public class ApplicationLifecycleHook implements LifecycleHook {
    /**
     * 程序退出
     */
    @Override
    public void destroy(ApplicationContext ctx) {
        System.out.println("<Application LifecycleHook destroy()>");		
    }

    /**
     * 程序初始化
     */
    @Override
    public void initialize(ApplicationContext ctx) {
        System.out.println("<Application LifecycleHook initialize()>" + ctx.getApplicationHome());
    }

}
