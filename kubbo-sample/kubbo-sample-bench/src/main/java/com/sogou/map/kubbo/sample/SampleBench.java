/**
 * 
 */
package com.sogou.map.kubbo.sample;

import com.sogou.map.kubbo.bench.Benchmark;
import com.sogou.map.kubbo.bench.Job;
import com.sogou.map.kubbo.boot.Kubbo;

/**
 * @author liufuliang
 *
 */
public class SampleBench {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int concurrency = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        int total = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
        
        final SampleService service = Kubbo.refer(SampleService.class);
        Benchmark.builder()
            .concurrency(concurrency)
            .total(total)
            .job(new Job(){
                @Override
                public boolean execute() {
                    try{
                        service.echo(new Message("hello"));
                        return true;
                    } catch(Throwable t){
                        return false;
                    }
                }
            })
            .run();
    }

}
