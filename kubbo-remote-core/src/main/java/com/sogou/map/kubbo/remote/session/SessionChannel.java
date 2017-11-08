package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.RemoteException;

/**
 * SessionChannel.
 * 
 * @author liufuliang
 */
public interface SessionChannel extends Channel {

    /**
     * send request.
     * 
     * @param request
     * @return response future
     * @throws RemoteException
     */
    ResponseFuture request(Object request) throws RemoteException;

    /**
     * send request.
     * 
     * @param request
     * @param timeout
     * @return response future
     * @throws RemoteException
     */
    ResponseFuture request(Object request, int timeout) throws RemoteException;

    /**
     * get message handler.
     * 
     * @return message handler
     */
    SessionHandler getSessionHandler();

}