package com.sogou.map.kubbo.common.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PlatformDependent {
    public static final int JAVA_VERSION = javaVersion0();

    static int javaVersion0() {
        try {
            final String javaSpecVersion = AccessController.doPrivileged(new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("java.specification.version");
                }
            });
            return majorVersion(javaSpecVersion);
        } catch (SecurityException e) {
            return 6;
        }
    }
    
    static int majorVersion(final String javaSpecVersion) {
        final String[] components = javaSpecVersion.split("\\.");
        final int[] version = new int[components.length];
        for (int i = 0; i < components.length; i++) {
            version[i] = Integer.parseInt(components[i]);
        }

        if (version[0] == 1) {
            assert version[1] >= 6;
            return version[1];
        } else {
            return version[0];
        }
    }

    private PlatformDependent() {
        // only static method supported
    }
}
