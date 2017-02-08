package com.sogou.map.kubbo.remote.transport.handler;

import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.ChannelHandlerDelegate;
import com.sogou.map.kubbo.remote.RemotingException;

/**
 * @author liufuliang
 */
public abstract class AbstractChannelHandlerDelegate implements ChannelHandlerDelegate {

    protected ChannelHandler handler;

    protected AbstractChannelHandlerDelegate(ChannelHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler == NULL");
        }
        this.handler = handler;
    }

    @Override
    public ChannelHandler getHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate)handler).getHandler();
        }
        return handler;
    }

    @Override
    public void onConnected(Channel channel) throws RemotingException {
        handler.onConnected(channel);
    }

    @Override
    public void onDisconnected(Channel channel) throws RemotingException {
        handler.onDisconnected(channel);
    }

    @Override
    public void onSent(Channel channel, Object message) throws RemotingException {
        handler.onSent(channel, message);
    }

    @Override
    public void onReceived(Channel channel, Object message) throws RemotingException {
        handler.onReceived(channel, message);
    }

    @Override
    public void onExceptonCaught(Channel channel, Throwable exception) throws RemotingException {
        handler.onExceptonCaught(channel, exception);
    }
}
