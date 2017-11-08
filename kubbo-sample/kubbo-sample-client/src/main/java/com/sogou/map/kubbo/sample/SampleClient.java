package com.sogou.map.kubbo.sample;

import com.sogou.map.kubbo.boot.Kubbo;



/**
 * SampleConsumer
 * 
 * @author liufuliang
 */
public class SampleClient {

    public static void main(String[] args) {
        // refer only once
        SampleService service = Kubbo.refer(SampleService.class);
        
        //call more
        for(int i=0; i<10; ++i) {
            Message result = service.echo(new Message("hello"));
            System.out.println(result.getValue());
        }
    }

}
