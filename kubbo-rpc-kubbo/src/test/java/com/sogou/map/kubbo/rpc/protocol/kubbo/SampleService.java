package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.util.Map;
import java.util.Set;



/**
 * <code>TestService</code>
 */

public interface SampleService
{
	void sayHello(String name);
	
	Set<String> keys(Map<String, String> map);

	String echo(String text);
	
	void update(byte[] buf);
	
}