/**
 * 
 */package com.sogou.map.kubbo.remote.session.handler;

import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.session.SessionChannel;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.transport.handler.NoOpChannelHandler;

/**
 * @author liufuliang
 *
 */
public class NoOpSessionHandler extends NoOpChannelHandler implements SessionHandler{

    @Override
    public Object reply(SessionChannel channel, Object request)
            throws RemoteException {
        return null;
    }

}
