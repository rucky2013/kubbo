/**
 * 
 */
package com.sogou.map.kubbo.common.annotation;

import java.io.IOException;

import com.sogou.map.kubbo.common.annotation.impl.JavassistAnnotationDiscovery;
import com.sogou.map.kubbo.common.extension.SPI;

/**
 * @author liufuliang
 *
 */
@SPI(JavassistAnnotationDiscovery.NAME)
public interface AnnotationDiscovery {
	void discover() throws IOException;
	void addListener(AnnotationDiscoveryListener listener);
}
