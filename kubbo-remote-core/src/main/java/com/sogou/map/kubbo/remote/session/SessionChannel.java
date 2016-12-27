package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.RemotingException;

/**
 * SessionChannel. (API/SPI, Prototype, ThreadSafe)
 * 
 * @author liufuliang
 */
public interface SessionChannel extends Channel {

    /**
     * send request.
     * 
     * @param request
     * @return response future
     * @throws RemotingException
     */
    ResponseFuture request(Object request) throws RemotingException;

    /**
     * send request.
     * 
     * @param request
     * @param timeout
     * @return response future
     * @throws RemotingException
     */
    ResponseFuture request(Object request, int timeout) throws RemotingException;

    /**
     * get message handler.
     * 
     * @return message handler
     */
    SessionHandler getSessionHandler();

    /**
     * graceful close.
     * 
     * @param timeout
     */
    void close(int timeout);

}