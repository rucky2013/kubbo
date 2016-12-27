package com.sogou.map.kubbo.common.threadpool;

import java.util.concurrent.Executor;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.common.threadpool.impl.FixedThreadPool;

/**
 * ThreadPool
 * 
 * @author liufuliang
 */
@SPI(FixedThreadPool.NAME)
public interface ThreadPool {
    
    /**
     * 线程池
     * 
     * @param url 线程参数
     * @return 线程池
     */
    @Adaptive({Constants.THREADPOOL_KEY})
    Executor getExecutor(URL url);

}