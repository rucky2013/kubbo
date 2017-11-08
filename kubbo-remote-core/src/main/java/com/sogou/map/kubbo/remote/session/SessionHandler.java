package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemoteException;

/**
 * SessionHandler.
 * 
 * @author liufuliang
 */
public interface SessionHandler extends ChannelHandler{

    /**
     * reply.
     * 
     * @param channel
     * @param request
     * @return response
     * @throws RemoteException
     */
    Object reply(SessionChannel channel, Object request) throws RemoteException;

}