/**
 * 
 */
package com.sogou.map.kubbo.remote.session;

/**
 * @author liufuliang
 *
 */
public interface ResponseListener {
    /**
     * done.
     * 
     * @param response
     */
    void done(Response response);

    /**
     * caught exception.
     * 
     * @param exception
     */
    void caught(Throwable exception);
}
