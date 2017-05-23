/**
 * 
 */package com.sogou.map.kubbo.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sogou.map.kubbo.boot.sample.SampleService;
import com.sogou.map.kubbo.boot.sample.SampleServiceImpl;



/**
 * @author liufuliang
 *
 */
public class KubboTest {
//	public static void rpc(){
//		SampleService exportservice = new SampleServiceImpl();
//		Kubbo.export(exportservice, SampleService.class, "kubbo://10.130.13.106:30660/sample?transportlayer=netty4&corethreads=1&iothreads=2&serialization=kryo");
//		
//		final SampleService referservice = Kubbo.refer(SampleService.class, "kubbo://10.130.13.106:30660/sample?transportlayer=netty4&iothreads=2&serialization=kryo");
//        for(int i=0; i<100; ++i){
//        	try{
//        		long start = System.nanoTime();
//                Future<String> response = Kubbo.callAsync(new Callable<String>() {
//                    public String call() throws Exception {
//                        return referservice.echo("async call");
//                    }
//                });
//                String result = response.get();
//        		//String result = referservice.echo("sync call");
//                long end = System.nanoTime();
//                System.out.println(result + ", time " + (end - start)/1000 + "micros");
//                System.out.flush();
//            } catch(Throwable t){
//            	System.out.println(t);
//            }
//        	
//        	try { Thread.sleep(2000); } catch (InterruptedException e) {}
//        }
//	}
//
//	public static void main(String[] args){
//		//rpc();
//		try{
//			rpc();
//		} catch(Throwable t){
//        	System.out.println(t);
//        }
//	}
    
    
    @BeforeClass
    public static void init(){
        SampleService exportservice = new SampleServiceImpl();
        Kubbo.export(exportservice, SampleService.class, "kubbo://127.0.0.1:40660");
    }
    
    @AfterClass
    public static void destroy(){
        Kubbo.destroy();
    }
    
    @Test
    public void testSyncCallExplicit(){
        SampleService referservice = Kubbo.refer(SampleService.class, "kubbo://127.0.0.1:40660?timeout=1000");
                
        Assert.assertEquals(referservice.echo("123456"), "123456");
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "1");
        map.put("key2", "2");
        map.put("key3", "3");
        Assert.assertTrue(referservice.keys(map).equals(map.keySet()));
    }
    
    @Test
    public void testSyncCall(){
        SampleService referservice = Kubbo.refer(SampleService.class);
                
        Assert.assertEquals(referservice.echo("123456"), "123456");
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "1");
        map.put("key2", "2");
        map.put("key3", "3");
        Assert.assertTrue(referservice.keys(map).equals(map.keySet()));
    }
    
    @Test
    public void testAsyncCall() throws InterruptedException, ExecutionException{
        final SampleService referservice = Kubbo.refer(SampleService.class, "kubbo://127.0.0.1:40660?timeout=1000");
        
        //echo
        Future<String> future = Kubbo.callAsync(new Callable<String>(){
            @Override
            public String call() throws Exception {
                return referservice.echo("123456");
            }
            
        });
        String result = future.get();
        Assert.assertEquals("123456", result);
        
        //keys
        final Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "1");
        map.put("key2", "2");
        map.put("key3", "3");
        Future<Set<String>> future1  = Kubbo.callAsync(new Callable<Set<String>>(){
            @Override
            public Set<String> call() throws Exception {
                return referservice.keys(map);
            }
            
        });		
        Assert.assertTrue(future1.get().equals(map.keySet()));
    }
}
