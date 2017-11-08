package com.sogou.map.kubbo.remote.transport;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemoteException;

/**
 * AbstractChannel
 * 
 * @author liufuliang
 */
public abstract class AbstractChannel extends AbstractEndpoint implements Channel {	

    public AbstractChannel(URL url, ChannelHandler handler){
        super(url, handler);
    }

    @Override
    public void send(Object message, boolean blocking) throws RemoteException {
        if (isClosed()) {
            throw new RemoteException(this, "Failed to send message "
                            + (message == null ? "" : message.getClass().getName()) + ":" + message
                            + ", cause: Channel closed. channel: " + this);
        }
    }
    
    @Override
    public String toString() {
        return getLocalAddress() + " -> " + getRemoteAddress();
    }
}