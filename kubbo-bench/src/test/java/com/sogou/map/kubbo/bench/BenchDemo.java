/**
 * 
 */
package com.sogou.map.kubbo.bench;

import com.sogou.map.kubbo.boot.Kubbo;
import com.sogou.map.kubbo.sample.api.SampleService;

/**
 * @author liufuliang
 *
 */
public class BenchDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final SampleService service = Kubbo.refer(SampleService.class, "kubbo://10.134.77.209:40660?timeout=2000");
        Benchmark.builder()
            .concurrency(50)
            .total(10000)
            .job(new Job(){
                @Override
                public boolean execute() {
                    try{
                        service.echo("xxxxx");
                        return true;
                    } catch(Throwable t){
                        return false;
                    }
                }
            })
            .run();
    }

}
