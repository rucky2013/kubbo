package com.sogou.map.kubbo.remote.transport.netty4;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.utils.NetUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;

/**
 * NettyHandler
 * 
 * @author liufuliang
 */
@io.netty.channel.ChannelHandler.Sharable
public class NettyHandler extends ChannelHandlerAdapter implements ChannelOutboundHandler, ChannelInboundHandler {

    private final Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>(); // <ip:port, channel>
    
    private final URL url;
    
    private final ChannelHandler handler;

    public NettyHandler(URL url, ChannelHandler handler){
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler == NULL");
        }
        this.url = url;
        this.handler = handler;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress socketAddress, ChannelPromise channelPromise) throws Exception {
        ctx.bind(socketAddress, channelPromise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }
    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.writeAndFlush(msg, promise);

        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.onSent(channel, msg);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }
    
    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise channelPromise) throws java.lang.Exception {
        ctx.disconnect(channelPromise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise channelPromise) throws Exception {
        ctx.close(channelPromise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise channelPromise) throws Exception {
        ctx.deregister(channelPromise);
    }

    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            if (channel != null) {
                channels.put(NetUtils.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), channel);
            }
            handler.onConnected(channel);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            channels.remove(NetUtils.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()));
            handler.onDisconnected(channel);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.onReceived(channel, msg);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.onExceptonCaught(channel, cause);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o) throws Exception {
        ctx.fireUserEventTriggered(o);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}