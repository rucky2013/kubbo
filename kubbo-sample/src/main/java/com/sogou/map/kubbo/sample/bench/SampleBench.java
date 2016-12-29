/**
 * 
 */
package com.sogou.map.kubbo.sample.bench;

import com.sogou.map.kubbo.bench.Benchmark;
import com.sogou.map.kubbo.bench.Job;
import com.sogou.map.kubbo.boot.Kubbo;
import com.sogou.map.kubbo.sample.api.SampleService;

/**
 * @author liufuliang
 *
 */
public class SampleBench {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int concurrency = 10;
        int total = 100;
        if(args.length > 0){
            concurrency = Integer.parseInt(args[0]);
        }
        if(args.length > 1){
            total = Integer.parseInt(args[1]);
        }
        
        final SampleService service = Kubbo.refer(SampleService.class);
        Benchmark.builder()
            .concurrency(concurrency)
            .total(total)
            .job(new Job(){
                @Override
                public boolean execute() {
                    try{
                        service.echo("aaaaaaaaaaaaaaaaa");
                        return true;
                    } catch(Throwable t){
                        return false;
                    }
                }
            })
            .run();
    }

}
