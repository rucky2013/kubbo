package com.sogou.map.kubbo.common.logger.impl;

import java.io.InputStream;
import java.util.logging.LogManager;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerAdapter;

public class JdkLoggerAdapter implements LoggerAdapter {
    
    public JdkLoggerAdapter() {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties");
            if (in != null) {
                LogManager.getLogManager().readConfiguration(in);
            } else {
                System.err.println("No logging.properties in classpath for jdk logging.");
            }
        } catch (Throwable t) {
            System.err.println("Failed to load logging.properties in classpath for jdk logging, cause: " + t.getMessage());
        }
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new JdkLogger(java.util.logging.Logger.getLogger(key == null ? "" : key.getName()));
    }

    @Override
    public Logger getLogger(String key) {
        return new JdkLogger(java.util.logging.Logger.getLogger(key));
    }

}