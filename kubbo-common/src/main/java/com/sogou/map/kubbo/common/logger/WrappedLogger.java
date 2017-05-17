package com.sogou.map.kubbo.common.logger;

import com.sogou.map.kubbo.common.util.NetUtils;

public class WrappedLogger implements Logger {
    
    static final String LOG_HEADER = "[Kubbo] " + "<" + NetUtils.getHostAddress() + "> ";

    private Logger logger;

    public WrappedLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private String appendLogHeader(String msg) {
        return LOG_HEADER + msg;
    }

    @Override
    public void trace(String msg, Throwable e) {
        try {
            logger.trace(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void trace(Throwable e) {
        try {
            logger.trace(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void trace(String msg) {
        try {
            logger.trace(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    @Override
    public void debug(String msg, Throwable e) {
        try {
            logger.debug(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void debug(Throwable e) {
        try {
            logger.debug(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void debug(String msg) {
        try {
            logger.debug(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    @Override
    public void info(String msg, Throwable e) {
        try {
            logger.info(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void info(String msg) {
        try {
            logger.info(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    @Override
    public void warn(String msg, Throwable e) {
        try {
            logger.warn(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void warn(String msg) {
        try {
            logger.warn(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    @Override
    public void error(String msg, Throwable e) {
        try {
            logger.error(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void error(String msg) {
        try {
            logger.error(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    @Override
    public void error(Throwable e) {
        try {
            logger.error(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void info(Throwable e) {
        try {
            logger.info(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void warn(Throwable e) {
        try {
            logger.warn(e);
        } catch (Throwable t) {
        }
    }

    @Override
    public boolean isTraceEnabled() {
        try {
            return logger.isTraceEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDebugEnabled() {
        try {
            return logger.isDebugEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isInfoEnabled() {
        try {
            return logger.isInfoEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isWarnEnabled() {
        try {
            return logger.isWarnEnabled();
        } catch (Throwable t) {
            return false;
        }
    }
    
    @Override
    public boolean isErrorEnabled() {
        try {
            return logger.isErrorEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

}