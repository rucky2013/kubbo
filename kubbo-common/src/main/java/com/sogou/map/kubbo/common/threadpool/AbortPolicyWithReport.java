package com.sogou.map.kubbo.common.threadpool;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;

/**
 * Abort Policy.
 * Log warn info when abort.
 * 
 * @author liufuliang
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {
    protected static final Logger logger = LoggerFactory.getLogger(AbortPolicyWithReport.class);
    
    private final String threadPoolName;
    
    private final URL url;
    
    public AbortPolicyWithReport(String threadPoolName, URL url) {
        this.threadPoolName = threadPoolName;
        this.url = url;
    }
    
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("Thread pool is EXHAUSTED!" +
                " Name: %s, Size: %d (core: %d, max: %d, approximate-active: %d, ever-largest: %d)," + 
                " Completed Task: %d/%d," +
                " Status:(isShutdown:%s, isTerminated:%s, isTerminating:%s), in %s://%s:%d!" ,
                threadPoolName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getCompletedTaskCount(), e.getTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating(),
                url.getProtocol(), url.getIp(), url.getPort());
        logger.warn(msg);
        throw new RejectedExecutionException(msg);
    }

}