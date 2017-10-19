package com.sogou.map.kubbo.common.threadpool.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.threadpool.AbortPolicyWithReport;
import com.sogou.map.kubbo.common.threadpool.NamedThreadFactory;
import com.sogou.map.kubbo.common.threadpool.ThreadPool;

/**
 * 此线程池可伸缩，线程空闲一分钟后回收，新请求重新创建线程,
 * corethreads maxthreads
 * 当线程数小于maxthreads, 优先创建线程, 
 * 当线程数达到maxthreads, 则如队列等待
 * 
 * @author liufuliang
 */
public class ScalableThreadPool implements ThreadPool {

    public static final String NAME = "scalable";

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_SCALABLE_CORE_THREADS);
        int maxthreads = url.getParameter(Constants.MAX_THREADS_KEY, Constants.DEFAULT_SCALABLE_MAX_THREADS);
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_SCALABLE_QUEUES);
        int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);
        
        return getExecutor(cores, maxthreads, alive, TimeUnit.MILLISECONDS, queues,
                name, new AbortPolicyWithReport(name, url));
    }
    
    public static Executor getExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, 
            int queues, String threadname, RejectedExecutionHandler handler){
        ThreadPoolExecutor executor = ScalableThreadPoolExecutor.createExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, 
                queues, new NamedThreadFactory(threadname, true), handler);
        return executor;
    }


}