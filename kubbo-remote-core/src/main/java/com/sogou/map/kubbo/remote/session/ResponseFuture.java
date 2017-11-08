package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.remote.RemoteException;

/**
 * ResponseFuture.
 * 
* @author liufuliang
 */
public interface ResponseFuture {

    /**
     * get result.
     * 
     * @return result.
     */
    Object get() throws RemoteException;

    /**
     * get result with the specified timeout.
     * 
     * @param timeoutInMillis timeout.
     * @return result.
     */
    Object get(int timeoutInMillis) throws RemoteException;

    /**
     * add listener.
     * 
     * @param listener response listener
     */    
    void addListener(ResponseListener listener);
    
    /**
     * check is done.
     * 
     * @return done or not.
     */
    boolean isDone();
    
    /**
     * concal the future
     */
    void cancel();

}