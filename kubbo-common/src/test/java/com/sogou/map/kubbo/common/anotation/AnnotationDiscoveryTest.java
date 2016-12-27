/**
 * 
 */
package com.sogou.map.kubbo.common.anotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sogou.map.kubbo.common.annotation.AnnotationDiscovery;
import com.sogou.map.kubbo.common.annotation.ClassAnnotationDiscoveryListener;
import com.sogou.map.kubbo.common.annotation.impl.JavassistAnnotationDiscovery;
import com.sogou.map.kubbo.common.extension.SPI;

/**
 * @author liufuliang
 *
 */

public class AnnotationDiscoveryTest {

	@Test
	public void testDiscovery() throws IOException {
		final List<String> clazzs = new ArrayList<String>();
		AnnotationDiscovery discovery = new JavassistAnnotationDiscovery();
		discovery.addListener(new ClassAnnotationDiscoveryListener() {
			@Override
			public String[] annotations() {
				return new String[] { SPI.class.getName() };
			}
			
			@Override
			public void discovered(String clazz, String annotation) {
				clazzs.add(clazz);
			}
		});
		discovery.discover();
		Assert.assertTrue(true);
	}

}
