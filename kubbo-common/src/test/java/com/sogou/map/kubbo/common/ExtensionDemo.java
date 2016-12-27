/**
 * 
 */
package com.sogou.map.kubbo.common;

import com.sogou.map.kubbo.common.extension.ExtensionLoader;

/**
 * @author liufuliang
 *
 */
public class ExtensionDemo {

	public static void main(String[] args){
		Demo demo = ExtensionLoader.getExtensionLoader(Demo.class).getExtension("demo1");
		System.out.println(demo.say());
	}
}
