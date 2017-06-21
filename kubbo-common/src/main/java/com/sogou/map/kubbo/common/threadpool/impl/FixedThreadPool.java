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
 * 此线程池启动时即创建固定大小的线程数，不做任何伸缩，来源于：<code>Executors.newFixedThreadPool()</code>
 * 
 * @see java.util.concurrent.Executors#newFixedThreadPool(int)
 * @author liufuliang
 */
public class FixedThreadPool implements ThreadPool {

    public static final String NAME = "fixed";

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int threads = url.getParameter(Constants.MAX_THREADS_KEY, Constants.DEFAULT_MAX_THREADS);
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        return getExecutor(threads, 
           		queues == 0 ? new SynchronousQueue<Runnable>() : 
                    (queues < 0 ? new LinkedBlockingQueue<Runnable>() 
                            : new LinkedBlockingQueue<Runnable>(queues)),
                            name, new AbortPolicyWithReport(name, url));
    }
    
    public static Executor getExecutor(int threads, BlockingQueue<Runnable> workQueue, String threadname, RejectedExecutionHandler handler){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS, 
                workQueue, new NamedThreadFactory(threadname, true), handler);
        return executor;
    }
    
    public static Executor getExecutor(int threads, BlockingQueue<Runnable> workQueue, String threadname){
        return getExecutor(threads, workQueue, threadname, new ThreadPoolExecutor.AbortPolicy());
    }

}