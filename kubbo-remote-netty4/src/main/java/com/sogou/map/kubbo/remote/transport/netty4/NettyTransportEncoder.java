/**
 * 
 */
package com.sogou.map.kubbo.remote.transport.netty4;

import java.util.List;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.Codec;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * @author liufuliang
 *
 */

@ChannelHandler.Sharable
public class NettyTransportEncoder extends MessageToMessageEncoder<Object> {
    private final Codec codec;
    
    private final URL url;
        
    private final com.sogou.map.kubbo.remote.ChannelHandler handler;

    public NettyTransportEncoder(Codec codec, URL url, com.sogou.map.kubbo.remote.ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        com.sogou.map.kubbo.remote.buffer.ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(1024);
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
               
        try {
            codec.encode(channel, buffer, msg);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }

        out.add(Unpooled.wrappedBuffer(buffer.toByteBuffer()));
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
}
