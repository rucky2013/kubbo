package com.sogou.map.kubbo.remote.transport.handler;

import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemotingException;

/**
 * NoopChannelHandler.
 * 
 * @author liufuliang
 */
public class NoOpChannelHandler implements ChannelHandler {

    public void onConnected(Channel channel) throws RemotingException {
    }

    public void onDisconnected(Channel channel) throws RemotingException {
    }

    public void onSent(Channel channel, Object message) throws RemotingException {
    }

    public void onReceived(Channel channel, Object message) throws RemotingException {
    }

    public void onExceptonCaught(Channel channel, Throwable exception) throws RemotingException {
    }

}