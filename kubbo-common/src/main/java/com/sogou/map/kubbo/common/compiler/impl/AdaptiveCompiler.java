package com.sogou.map.kubbo.common.compiler.impl;


import com.sogou.map.kubbo.common.compiler.Compiler;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.common.util.StringUtils;

/**
 * AdaptiveCompiler. (SPI, Singleton, ThreadSafe)
 * 
 * @author liufuliang
 */
@Adaptive
public class AdaptiveCompiler implements Compiler {

    private static volatile String DEFAULT_COMPILER;

    public static void setDefaultCompiler(String compiler) {
        DEFAULT_COMPILER = compiler;
    }

    @Override
    public Class<?> compile(String code, ClassLoader classLoader) {
        Compiler compiler;
        ExtensionLoader<Compiler> loader = ExtensionLoader.getExtensionLoader(Compiler.class);
        String name = DEFAULT_COMPILER; // copy reference
        if (!StringUtils.isBlank(name)) {
            compiler = loader.getExtension(name);
        } else {
            compiler = loader.getDefaultExtension();
        }
        return compiler.compile(code, classLoader);
    }

}
