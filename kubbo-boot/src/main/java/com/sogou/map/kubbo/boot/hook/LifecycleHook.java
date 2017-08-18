/**
 * 
 */
package com.sogou.map.kubbo.boot.hook;

import com.sogou.map.kubbo.boot.context.ApplicationContext;

/**
 * @author liufuliang
 * TODO 应该有boolean返回值, 标识成功与否
 */
public interface LifecycleHook {
    
    void destroy(ApplicationContext ctx);
    
    void initialize(ApplicationContext ctx);
}
