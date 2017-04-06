package com.sogou.map.kubbo.bench;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.util.NamedThreadFactory;

/**
 * 
 * @author liufuliang
 *
 */
public class Benchmark {
    private Options options = new Options(1, 1);
    private List<Job> jobs = new ArrayList<Job>();
    private Statistics statistics;

    
    public static Benchmark builder(){
        return new Benchmark();
    }
    
    public Benchmark concurrency(int concurrency){
        options.setConcurrency(concurrency);
        return this;
    }
    
    public Benchmark total(int total){
        options.setTotal(total);
        return this;
    }
    
    public Benchmark job(Job job){
        jobs.add(job);
        return this;
    }
    public Benchmark job(List<Job> jobs){
        this.jobs.addAll(jobs);
        return this;
    }

    public Statistics run(){
        if(jobs.isEmpty()){
            throw new IllegalArgumentException("no job");
        }
        statistics = new Statistics(options);
        ExecutorService threadPool = Executors.newFixedThreadPool(options.getConcurrency(), new NamedThreadFactory("Kubbobench"));
        
        System.out.println("Benchmark start (be patient)");
        // JIT warmup
        int index = 0;
        for(int i=0; i < 20; ++i){
            Job job = jobs.get((index++)%jobs.size());
            job.execute();
            try{ Thread.sleep(100); } catch(Exception e){}
        }
        // job
        long start = System.nanoTime();
        Runnable task = new ExecuteTask();
        for (int i = 0; i < options.getTotal(); i++) {
            threadPool.execute(task);
        }

        // await 
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
        }
        long end = System.nanoTime();
        System.out.println("Benchmark completed.");
        statistics.setTimeTaken((end - start)/1000000);
        System.out.println(statistics.report());
        
        return this.statistics;
    }
    
    
    private class ExecuteTask implements Runnable {
        int displayBase = options.getTotal()/10;
        int index = 0;
        @Override
        public void run() {
            try {
                doRun();
            } catch (Exception e) {
                System.err.print("Failed to invoke a single test. ");
                e.printStackTrace();
            }
        }

        private void doRun() {
            boolean isOK = false;

            long start = System.nanoTime();
            try {
                Job job = jobs.get((index++)%jobs.size());
                isOK = job.execute();
            } catch (Exception e) {
                isOK = false;
            }
            long end = System.nanoTime();
            long time = end - start;
            if (isOK) {
                statistics.complete(time);
            } else {
                statistics.uncomplete(time);
            }

            int num = statistics.getRequestedNum();
            if (displayBase > 0 && num % displayBase == 0) {
                System.out.printf("Completed %d/%d\n", num, options.getTotal());
            }
        }

    }
}
