package com.sogou.map.kubbo.common.logger;

/**
 * 日志输出器供给器
 *
 * @author liufuliang
 */
public interface LoggerAdapter {
    
    /**
     * 获取日志输出器
     *
     * @param key 分类键
     * @return 日志输出器, 后验条件: 不返回null.
     */
    Logger getLogger(Class<?> key);

    /**
     * 获取日志输出器
     *
     * @param key 分类键
     * @return 日志输出器, 后验条件: 不返回null.
     */
    Logger getLogger(String key);

}