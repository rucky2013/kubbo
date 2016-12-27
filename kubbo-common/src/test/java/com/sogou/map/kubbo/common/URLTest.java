/**
 * 
 */
package com.sogou.map.kubbo.common;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author liufuliang
 *
 */
public class URLTest {
	@Test
	public void testParse(){
		URL url = URL.valueOf("discovery://discovery.mssp.lc/kubbo/sampleservice?protocol=kubbo&transportlayer=netty4");
		
		System.out.println(url.getAddress());
		
		Assert.assertEquals("kubbo/sampleservice", url.getPath());
	}
}
