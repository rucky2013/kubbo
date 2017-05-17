package com.sogou.map.kubbo.common.logger.impl;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

import com.sogou.map.kubbo.common.logger.WrappedLogger;
import com.sogou.map.kubbo.common.logger.Logger;

public class Log4j2Logger implements Logger {
        
    private static final String FQCN = WrappedLogger.class.getName();
    
    private static final Marker MARKER = null;
    
    private ExtendedLoggerWrapper logger;

    public Log4j2Logger(org.apache.logging.log4j.Logger logger) {
        this.logger = new ExtendedLoggerWrapper((ExtendedLogger) logger, logger.getName(), logger.getMessageFactory());
    }

    @Override
    public void trace(String msg) {
        logger.logIfEnabled(FQCN, Level.TRACE, MARKER, msg);
    }

    @Override
    public void trace(Throwable e) {
        logger.logIfEnabled(FQCN, Level.TRACE, MARKER, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void trace(String msg, Throwable e) {
        logger.logIfEnabled(FQCN, Level.TRACE, MARKER, msg, e);
    }

    @Override
    public void debug(String msg) {
        logger.logIfEnabled(FQCN, Level.DEBUG, MARKER, msg);
    }

    @Override
    public void debug(Throwable e) {
        logger.logIfEnabled(FQCN, Level.DEBUG, MARKER, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void debug(String msg, Throwable e) {
        logger.logIfEnabled(FQCN, Level.DEBUG, MARKER, msg, e);
    }

    @Override
    public void info(String msg) {
        logger.logIfEnabled(FQCN, Level.INFO, MARKER, msg);
    }

    @Override
    public void info(Throwable e) {
        logger.logIfEnabled(FQCN, Level.INFO, MARKER, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void info(String msg, Throwable e) {
        logger.logIfEnabled(FQCN, Level.INFO, MARKER, msg, e);
    }

    @Override
    public void warn(String msg) {
        logger.logIfEnabled(FQCN, Level.WARN, MARKER, msg);
    }

    @Override
    public void warn(Throwable e) {
        logger.logIfEnabled(FQCN, Level.WARN, MARKER, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.logIfEnabled(FQCN, Level.WARN, MARKER, msg, e);
    }

    @Override
    public void error(String msg) {
        logger.logIfEnabled(FQCN, Level.ERROR, MARKER, msg);
    }

    @Override
    public void error(Throwable e) {
        logger.logIfEnabled(FQCN, Level.ERROR, MARKER, e == null ? null : e.getMessage(), e);
    }

    @Override
    public void error(String msg, Throwable e) {
        logger.logIfEnabled(FQCN, Level.ERROR, MARKER, msg, e);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }
    
    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

}