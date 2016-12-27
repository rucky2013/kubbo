package com.sogou.map.kubbo.sample.consumer;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.sogou.map.kubbo.boot.Kubbo;
import com.sogou.map.kubbo.sample.api.SampleService;


/**
 * SampleConsumer
 * 
 * @author liufuliang
 */
public class SampleConsumer {

    public static void main(String[] args) {
    	final SampleService service = Kubbo.refer(SampleService.class);
        for(;;){
        	try{
        		long start = System.currentTimeMillis();
//        		byte[] data = new byte[100 * 1024 * 1024];
//        		service.update(data);//500m
                Future<String> response = Kubbo.callAsync(new Callable<String>() {
                    public String call() throws Exception {
                        return service.echo("async call");
                    }
                });
                String result = response.get();
                long end = System.currentTimeMillis();
                System.out.println(result + ", time " + (end - start) + "ms");
            } catch(Throwable t){
            	System.out.println(t);
            }
        	
        	try { Thread.sleep(5000); } catch (InterruptedException e) {}
        }
        


    }

}
