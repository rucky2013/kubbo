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

    public void connected(Channel channel) throws RemotingException {
    }

    public void disconnected(Channel channel) throws RemotingException {
    }

    public void sent(Channel channel, Object message) throws RemotingException {
    }

    public void received(Channel channel, Object message) throws RemotingException {
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
    }

}