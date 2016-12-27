package com.sogou.map.kubbo.remote.transport;

import java.net.InetSocketAddress;

import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelDelegate;

/**
 * AbstractChannelDelegate
 * 
 * @author liufuliang
 */
public class AbstractChannelDelegate extends AbstractEndpointDelegate implements ChannelDelegate {
    
    protected transient Channel channel;
    
    public AbstractChannelDelegate() {
    }

    public AbstractChannelDelegate(Channel channel) {
        super(channel);
        this.channel = channel;
    }
    
    @Override
    public Channel getChannel() {
        return channel;
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }
}