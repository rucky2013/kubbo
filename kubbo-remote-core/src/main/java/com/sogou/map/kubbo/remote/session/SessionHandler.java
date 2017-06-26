package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemotingException;

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
     * @throws RemotingException
     */
    Object reply(SessionChannel channel, Object request) throws RemotingException;

}