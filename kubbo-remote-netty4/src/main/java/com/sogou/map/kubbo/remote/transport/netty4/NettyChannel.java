package com.sogou.map.kubbo.remote.transport.netty4;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.transport.AbstractChannel;

/**
 * NettyChannel.
 * 
 * @author liufuliang
 */
final class NettyChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannel.class);

    private static final ConcurrentMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<Channel, NettyChannel>();

    private final Channel channel;

    private NettyChannel(Channel channel, URL url, ChannelHandler handler){
        super(url, handler);
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == NULL");
        }
        this.channel = channel;
    }

    static NettyChannel getOrAddChannel(Channel ch, URL url, ChannelHandler handler) {
        if (ch == null) {
            return null;
        }
        NettyChannel channel = channelMap.get(ch);
        if (channel == null) {
            NettyChannel nc = new NettyChannel(ch, url, handler);
            if (ch.isActive()) {
                channel = channelMap.putIfAbsent(ch, nc);
            }
            if (channel == null) {
                channel = nc;
            }
        }
        return channel;
    }

    static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            channelMap.remove(ch);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public void send(Object message, boolean blocking) throws RemoteException {
        super.send(message, blocking);
        boolean success = true;
        int timeout = 0;
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            if (blocking) {
                timeout = getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
                success = future.syncUninterruptibly().await(timeout);
            }
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new RemoteException(this, new StringBuffer(32)
                    .append("Failed to send message [").append(message).append("] ")
                    .append("to ").append(getRemoteAddress()).append(", ")
                    .append("cause: ").append(e.getMessage())
                    .toString(),
                    e);            		
        }
        
        if(!success) {
            throw new RemoteException(this, new StringBuffer(32)
                    .append("Failed to send message [").append(message).append("] ")
                    .append("to ").append(getRemoteAddress()).append(" ")
                    .append("in timeout(").append(timeout).append("ms) limit")
                    .toString());
        }
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            removeChannelIfDisconnected(channel);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Close " + this);
            }
            channel.close().syncUninterruptibly();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NettyChannel other = (NettyChannel) obj;
        return channel.equals(other.channel);
    }

    @Override
    public String toString() {
        return "NettyChannel " + channel;
    }

}