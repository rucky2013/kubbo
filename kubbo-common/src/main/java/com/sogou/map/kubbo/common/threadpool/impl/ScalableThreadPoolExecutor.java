package com.sogou.map.kubbo.common.threadpool.impl;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ScalableThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * The number of tasks submitted but not yet finished. This includes tasks
     * in the queue and tasks that have been handed to a worker thread but the
     * latter did not start executing the task yet.
     * This number is always greater or equal to {@link #getActiveCount()}.
     */
    private final AtomicInteger submittedCount = new AtomicInteger(0);

    public ScalableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ScalableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public ScalableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public ScalableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }
    
    public static ThreadPoolExecutor createExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, 
            int queues, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        TaskQueue workQueue = new TaskQueue(queues);
        ScalableThreadPoolExecutor executor = new ScalableThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, 
                workQueue, threadFactory, handler);
        workQueue.setParent(executor);
        return executor;
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        submittedCount.decrementAndGet();
    }

    public int getSubmittedCount() {
        return submittedCount.get();
    }

    @Override
    public void execute(Runnable command) {
        execute(command, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the <tt>Executor</tt> implementation.
     * If no threads are available, it will be added to the work queue.
     * If the work queue is full, the system will wait for the specified
     * time and it throw a RejectedExecutionException if the queue is still
     * full after that.
     *
     * @param command the runnable task
     * @param timeout A timeout for the completion of the task
     * @param unit The timeout time unit
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution - the queue is full
     * @throws NullPointerException if command or unit is null
     */
    public void execute(Runnable command, long timeout, TimeUnit unit) {
        submittedCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            if (super.getQueue() instanceof TaskQueue) {
                final TaskQueue queue = (TaskQueue)super.getQueue();
                try {
                    if (!queue.force(command, timeout, unit)) {
                        submittedCount.decrementAndGet();
                        throw new RejectedExecutionException("Queue capacity is full.");
                    }
                } catch (InterruptedException x) {
                    submittedCount.decrementAndGet();
                    throw new RejectedExecutionException(x);
                }
            } else {
                submittedCount.decrementAndGet();
                throw rx;
            }

        }
    }

    public static class TaskQueue extends LinkedBlockingQueue<Runnable> {

        private static final long serialVersionUID = 1L;

        private volatile ScalableThreadPoolExecutor parent = null;

        public TaskQueue() {
            super();
        }

        public TaskQueue(int capacity) {
            super(capacity);
        }

        public TaskQueue(Collection<? extends Runnable> c) {
            super(c);
        }

        public void setParent(ScalableThreadPoolExecutor tp) {
            parent = tp;
        }

        public boolean force(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
            if ( parent == null || parent.isShutdown() ) throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
            return super.offer(o, timeout, unit); //forces the item onto the queue, to be used if the task is rejected
        }

      @Override
      public boolean offer(Runnable o) {
        //we can't do any checks
          if (parent == null) return super.offer(o);
          //we are maxed out on threads, simply queue the object
          if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
              return super.offer(o);
          }
          //we have idle threads, just add it to the queue
          if (parent.getSubmittedCount() < (parent.getPoolSize())) {
              return super.offer(o);
          }
          //if we have less threads than maximum force creation of a new thread
          if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
              return false;
          }
          
          //if we reached here, we need to add it to the queue
          return super.offer(o);
      }
    }
}
