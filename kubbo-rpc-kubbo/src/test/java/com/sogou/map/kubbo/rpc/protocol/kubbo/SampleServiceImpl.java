package com.sogou.map.kubbo.rpc.protocol.kubbo;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DemoServiceImpl
 */

public class SampleServiceImpl implements SampleService{

	@Override
	public void sayHello(String name) {
		System.out.println("hello "+name);
	}

	@Override
	public String echo(String text){
		return text;
	}

	@Override
	public Set<String> keys(Map<String, String> map) {
		return map == null ? null : new HashSet<String>(map.keySet());
	}

	@Override
	public void update(byte[] buf) {
		System.out.println("update");
		return;
		
	}
}