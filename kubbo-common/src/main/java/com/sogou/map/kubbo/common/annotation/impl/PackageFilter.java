package com.sogou.map.kubbo.common.annotation.impl;

import com.sogou.map.kubbo.common.annotation.Filter;

/**
 * Basic implementation to skip well-known packages and allow only *.class files
 * 
 * @author liufuliang
 */
public class PackageFilter implements Filter {
    public static final String[] IGNORED_PACKAGES = {
        "java", "javax",
        "sun", "com.sun",
        "apple", "com.apple",
        "javassist", "com.impetus.annovention"
    };

    private String[] ignoredPackages;

    public PackageFilter()                         { this.ignoredPackages = IGNORED_PACKAGES; }
    public PackageFilter(String[] ignoredPackages) { this.ignoredPackages = ignoredPackages;  }

    @Override
    public final boolean accepts(String filename) {
        if (filename.endsWith(".class")) {
            if (filename.startsWith("/")) {
                filename = filename.substring(1);
            }
            if (!ignoreScan(filename.replace('/', '.'))) {
                return true;
            }
        }
        return false;
    }

    private boolean ignoreScan(String intf) {
        for (String ignored : ignoredPackages) {
            if (intf.startsWith(ignored + ".")) {
                return true;
            }
        }
        return false;
    }
}
