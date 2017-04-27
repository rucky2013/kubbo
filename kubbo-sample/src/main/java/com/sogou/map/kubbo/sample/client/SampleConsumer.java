package com.sogou.map.kubbo.sample.client;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.sogou.map.kubbo.boot.Kubbo;
import com.sogou.map.kubbo.sample.api.Message;
import com.sogou.map.kubbo.sample.api.SampleService;


/**
 * SampleConsumer
 * 
 * @author liufuliang
 */
public class SampleConsumer {

    public static void main(String[] args) {
        final SampleService service = Kubbo.refer(SampleService.class);
        Random r = new Random(System.currentTimeMillis());
        for(;;){
            try{
                long start = System.currentTimeMillis();
//        		byte[] data = new byte[100 * 1024 * 1024];
//        		service.update(data);//500m
                Future<Message> response = Kubbo.callAsync(new Callable<Message>() {
                    public Message call() throws Exception {
                        return service.echo(new Message("async call"));
                    }
                });
                Message result = response.get();
                long end = System.currentTimeMillis();
                System.out.println(result.getValue() + ", time " + (end - start) + "ms");
            } catch(Throwable t){
                System.out.println(t);
            }
            
            
            try { Thread.sleep(r.nextInt(5000)); } catch (InterruptedException e) {}
            break;
        }
        


    }

}
