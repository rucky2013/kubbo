package com.sogou.map.kubbo.common.threadpool.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.threadpool.AbortPolicyWithReport;
import com.sogou.map.kubbo.common.threadpool.NamedThreadFactory;
import com.sogou.map.kubbo.common.threadpool.ThreadPool;

/**
 * 此线程池一直增长，直到上限，增长后不收缩。
 * 
 * @author liufuliang
 */
public class LimitedThreadPool implements ThreadPool {

    public static final String NAME = "limited";

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
        int maxthreads = url.getParameter(Constants.MAX_THREADS_KEY, Constants.DEFAULT_MAX_THREADS);
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        return getExecutor(cores, maxthreads,
                queues == 0 ? new SynchronousQueue<Runnable>() : 
                    (queues < 0 ? new LinkedBlockingQueue<Runnable>() 
                            : new LinkedBlockingQueue<Runnable>(queues)),
                        name, new AbortPolicyWithReport(name, url), false);
    }
    
    public static Executor getExecutor(int corePoolSize, int maximumPoolSize, 
            BlockingQueue<Runnable> workQueue, String threadname, RejectedExecutionHandler handler, 
            boolean allowCoreThreadTimeOut){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, Long.MAX_VALUE, TimeUnit.MILLISECONDS, 
                workQueue, new NamedThreadFactory(threadname, true), handler);
        executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return executor;
    }
    
    public static Executor getExecutor(int corePoolSize, int maximumPoolSize,
            BlockingQueue<Runnable> workQueue, String threadname,  boolean allowCoreThreadTimeOut){
        return getExecutor(corePoolSize, maximumPoolSize, 
                workQueue, threadname, new ThreadPoolExecutor.AbortPolicy(), allowCoreThreadTimeOut);
    }

}
