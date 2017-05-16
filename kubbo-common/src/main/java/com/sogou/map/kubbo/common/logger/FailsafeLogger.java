package com.sogou.map.kubbo.common.logger;

import com.sogou.map.kubbo.common.util.NetUtils;

public class FailsafeLogger implements Logger {
    
    private static final String LOG_HEADER = "[Kubbo] " + "<" + NetUtils.getHostAddress() + "> ";

    private Logger logger;

    public FailsafeLogger(Logger logger) {
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

    public void trace(String msg, Throwable e) {
        try {
            logger.trace(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    public void trace(Throwable e) {
        try {
            logger.trace(e);
        } catch (Throwable t) {
        }
    }

    public void trace(String msg) {
        try {
            logger.trace(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    public void debug(String msg, Throwable e) {
        try {
            logger.debug(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    public void debug(Throwable e) {
        try {
            logger.debug(e);
        } catch (Throwable t) {
        }
    }

    public void debug(String msg) {
        try {
            logger.debug(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    public void info(String msg, Throwable e) {
        try {
            logger.info(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    public void info(String msg) {
        try {
            logger.info(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    public void warn(String msg, Throwable e) {
        try {
            logger.warn(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    public void warn(String msg) {
        try {
            logger.warn(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    public void error(String msg, Throwable e) {
        try {
            logger.error(appendLogHeader(msg), e);
        } catch (Throwable t) {
        }
    }

    public void error(String msg) {
        try {
            logger.error(appendLogHeader(msg));
        } catch (Throwable t) {
        }
    }

    public void error(Throwable e) {
        try {
            logger.error(e);
        } catch (Throwable t) {
        }
    }

    public void info(Throwable e) {
        try {
            logger.info(e);
        } catch (Throwable t) {
        }
    }

    public void warn(Throwable e) {
        try {
            logger.warn(e);
        } catch (Throwable t) {
        }
    }

    public boolean isTraceEnabled() {
        try {
            return logger.isTraceEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean isDebugEnabled() {
        try {
            return logger.isDebugEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean isInfoEnabled() {
        try {
            return logger.isInfoEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean isWarnEnabled() {
        try {
            return logger.isWarnEnabled();
        } catch (Throwable t) {
            return false;
        }
    }
    
    public boolean isErrorEnabled() {
        try {
            return logger.isErrorEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

}