/**
 * 
 */
package com.sogou.map.kubbo.remote.transport.netty4;

import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Codec;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffer;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffers;

/**
 * @author liufuliang
 *
 */

@io.netty.channel.ChannelHandler.Sharable
public class NettyTransportEncoder extends io.netty.handler.codec.MessageToMessageEncoder<Object> {
    private final Codec codec;
    
    private final URL url;
        
    private final int bufferSize;
    
    private final ChannelHandler handler;

    public NettyTransportEncoder(Codec codec, URL url, ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
        int size = url.getPositiveParameter(Constants.ENCODE_BUFFER_KEY, Constants.DEFAULT_ENCODE_BUFFER_SIZE);
        this.bufferSize = size >= Constants.MIN_BUFFER_SIZE && size <= Constants.MAX_BUFFER_SIZE ? 
                size : Constants.DEFAULT_ENCODE_BUFFER_SIZE;
    }
    
    @Override
    protected void encode(io.netty.channel.ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(this.bufferSize);
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
               
        try {
            codec.encode(channel, buffer, msg);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }

        out.add(io.netty.buffer.Unpooled.wrappedBuffer(buffer.toByteBuffer()));
    }
    
    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.onExceptonCaught(channel, cause);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }
}
