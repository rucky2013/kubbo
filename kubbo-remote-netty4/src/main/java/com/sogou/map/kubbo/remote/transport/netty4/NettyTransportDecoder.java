/**
 * 
 */
package com.sogou.map.kubbo.remote.transport.netty4;

import java.io.IOException;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.Codec;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffers;
import com.sogou.map.kubbo.remote.buffer.DynamicChannelBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author liufuliang
 *
 */
public class NettyTransportDecoder extends SimpleChannelInboundHandler<ByteBuf> {
    private final Codec        codec;
    
    private final URL            url;
    
    private final int            bufferSize;
    
    private final com.sogou.map.kubbo.remote.ChannelHandler handler;

    public NettyTransportDecoder(Codec codec, URL url, com.sogou.map.kubbo.remote.ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
        int bufferSize = url.getPositiveParameter(Constants.BUFFER_KEY, Constants.DEFAULT_BUFFER_SIZE);
        this.bufferSize = bufferSize >= Constants.MIN_BUFFER_SIZE && bufferSize <= Constants.MAX_BUFFER_SIZE ? 
                bufferSize : Constants.DEFAULT_BUFFER_SIZE;
    }
    private com.sogou.map.kubbo.remote.buffer.ChannelBuffer buffer = ChannelBuffers.EMPTY_BUFFER;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf input) throws Exception {
        int readable = input.readableBytes();
        if (readable <= 0) {
            return;
        }

        com.sogou.map.kubbo.remote.buffer.ChannelBuffer message;
        if (buffer.readable()) {
            if (buffer instanceof DynamicChannelBuffer) {
                buffer.writeBytes(input.nioBuffer());
                message = buffer;
            } else {
                int size = buffer.readableBytes() + input.readableBytes();
                message = ChannelBuffers.dynamicBuffer(size > bufferSize ? size : bufferSize);
                message.writeBytes(buffer, buffer.readableBytes());
                message.writeBytes(input.nioBuffer());
            }
        } else {
            message = ChannelBuffers.wrappedBuffer(input.nioBuffer());
        }

        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        Object msg;
        int saveReaderIndex;

        // decode object.
        try {
            do {
                saveReaderIndex = message.readerIndex();
                try {
                    msg = codec.decode(channel, message);
                } catch (IOException e) {
                    buffer = ChannelBuffers.EMPTY_BUFFER;
                    throw e;
                }
                if (msg == Codec.DecodeResult.NEED_MORE_INPUT) {
                    message.readerIndex(saveReaderIndex);
                    break;
                } else {
                    if (saveReaderIndex == message.readerIndex()) {
                        buffer = ChannelBuffers.EMPTY_BUFFER;
                        throw new IOException("Decode without read data.");
                    }
                    if (msg != null) {
                        ctx.fireChannelRead(msg);
                    }
                }
            } while (message.readable());
        } finally {
            if (message.readable()) {
                message.discardReadBytes();
                if(message.isDirect()){
                    int size = message.readableBytes();
                    buffer = ChannelBuffers.dynamicBuffer(size > bufferSize ? size : bufferSize);
                    buffer.writeBytes(message, message.readableBytes());
                } else{
                    buffer = message;
                }
            } else {
                buffer = ChannelBuffers.EMPTY_BUFFER;
            }
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

}
