package com.sogou.map.kubbo.remote.session;

/**
 * ResponseCallback
 * 
 * @author liufuliang
 */
public interface ResponseCallback {

    /**
     * done.
     * 
     * @param response
     */
    void done(Object response);

    /**
     * caught exception.
     * 
     * @param exception
     */
    void caught(Throwable exception);

}