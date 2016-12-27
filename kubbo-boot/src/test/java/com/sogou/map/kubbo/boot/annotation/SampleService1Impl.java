/**
 * 
 */
package com.sogou.map.kubbo.boot.annotation;

import com.sogou.map.kubbo.boot.annotation.Export;

/**
 * @author liufuliang
 *
 */

@Export(SampleService1.class)
public class SampleService1Impl implements SampleService1{

	@Override
	public String echo(String msg) {
		return msg;
	}

}
