/**
 * 
 */package com.sogou.map.kubbo.rpc.protocol.kubbo;

import org.junit.Assert;
import org.junit.Test;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.Protocols;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.InvokerProxy;

/**
 * @author liufuliang
 *
 */
public class KubboProtocolTest {
    private static Protocol protocol = Protocols.getExtension("kubbo");
    private static InvokerProxy proxy = Extensions.getAdaptiveExtension(InvokerProxy.class);

    public static void main(String[] args){
        new KubboProtocolTest().testSampleService();
        protocol.destroy();
    }
    
    @Test
    public void testSampleService(){
        SampleService exportservice = new SampleServiceImpl();
        Exporter<SampleService> exporter = protocol.export(
                proxy.getInvoker(exportservice, SampleService.class, URL.valueOf("kubbo://127.0.0.1:9020/sample?codec=session")));
        
        
        SampleService referservice = proxy.getProxy(
                protocol.refer(SampleService.class, URL.valueOf("kubbo://127.0.0.1:9020/sample?codec=session&timeout=1000000&heartbeat=0")));
        
        byte[] data = new byte[100];
        referservice.update(data);
        
        Assert.assertEquals(referservice.echo("123456"), "123456");
        
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("key1", "1");
//		map.put("key2", "2");
//		map.put("key3", "3");
//		
//		Assert.assertTrue(referservice.keys(map).equals(map.keySet()));
        exporter.unexport();
        
    }
}
