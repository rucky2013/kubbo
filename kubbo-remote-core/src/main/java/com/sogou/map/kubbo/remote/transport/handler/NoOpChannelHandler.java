package com.sogou.map.kubbo.remote.transport.handler;

import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemoteException;

/**
 * NoopChannelHandler.
 * 
 * @author liufuliang
 */
public class NoOpChannelHandler implements ChannelHandler {

    @Override
    public void onConnected(Channel channel) throws RemoteException {
    }

    @Override
    public void onDisconnected(Channel channel) throws RemoteException {
    }

    @Override
    public void onSent(Channel channel, Object message) throws RemoteException {
    }

    @Override
    public void onReceived(Channel channel, Object message) throws RemoteException {
    }

    @Override
    public void onExceptonCaught(Channel channel, Throwable exception) throws RemoteException {
    }

}