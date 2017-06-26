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
 * 此线程池可伸缩，线程空闲一分钟后回收，新请求重新创建线程，来源于：<code>Executors.newCachedThreadPool()</code>
 * 
 * @see java.util.concurrent.Executors#newCachedThreadPool()
 * @author liufuliang
 */
public class CachedThreadPool implements ThreadPool {

    public static final String NAME = "cached";

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, 0);
        int maxthreads = url.getParameter(Constants.MAX_THREADS_KEY, Integer.MAX_VALUE);
        int queues = url.getParameter(Constants.QUEUES_KEY, 0);
        int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);
        
        return getExecutor(cores, maxthreads, alive, TimeUnit.MILLISECONDS, 
                queues == 0 ? new SynchronousQueue<Runnable>() : 
                    (queues < 0 ? new LinkedBlockingQueue<Runnable>() 
                            : new LinkedBlockingQueue<Runnable>(queues)),
                name, new AbortPolicyWithReport(name, url), false);
    }
    
    public static Executor getExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, 
            BlockingQueue<Runnable> workQueue, String threadname, RejectedExecutionHandler handler, boolean allowCoreThreadTimeOut){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, 
                workQueue, new NamedThreadFactory(threadname, true), handler);
        executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return executor;
    }
    
    public static Executor getExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, 
            BlockingQueue<Runnable> workQueue, String threadname,  boolean allowCoreThreadTimeOut){
        return getExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, 
                workQueue, threadname, new ThreadPoolExecutor.AbortPolicy(), allowCoreThreadTimeOut);
    }

}