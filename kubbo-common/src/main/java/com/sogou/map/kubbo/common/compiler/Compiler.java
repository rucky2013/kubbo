package com.sogou.map.kubbo.common.compiler;

import com.sogou.map.kubbo.common.compiler.impl.JavassistCompiler;
import com.sogou.map.kubbo.common.extension.SPI;

/**
 * Compiler. (SPI, Singleton, ThreadSafe)
 * 
 * @author liufuliang
 */
@SPI(JavassistCompiler.NAME)
public interface Compiler {

    /**
     * Compile java source code.
     * 
     * @param code Java source code
     * @param classLoader TODO
     * @return Compiled class
     */
    Class<?> compile(String code, ClassLoader classLoader);

}
