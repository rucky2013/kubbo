/**
 * 
 */
package com.sogou.map.kubbo.common.compiler.impl;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * @author liufuliang
 *
 */
public class JdkCompilerTest {

    /**
     * @param args
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        System.out.println(System.getProperty("java.home"));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        System.out.println(compiler.toString());
//		String code = "public class Hello{public String sayHello (String name){return \"Hello,\" + name + \"!\";}}";
//		JdkCompiler compiler = new JdkCompiler();
//		Class<?> type = compiler.compile(code, JdkCompilerTest.class.getClassLoader());
//		type.newInstance();

    }

}
