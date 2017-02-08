/**
 * 
 */
package com.sogou.map.kubbo.remote.transport.netty4;

import java.io.IOException;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Codec;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffer;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffers;
import com.sogou.map.kubbo.remote.buffer.DynamicChannelBuffer;

/**
 * @author liufuliang
 *
 */
public class NettyTransportDecoder extends io.netty.channel.SimpleChannelInboundHandler<io.netty.buffer.ByteBuf> {
    private final Codec        codec;
    
    private final URL            url;
    
    private final int            bufferSize;
    
    private final ChannelHandler handler;

    public NettyTransportDecoder(Codec codec, URL url, ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
        int bufferSize = url.getPositiveParameter(Constants.BUFFER_KEY, Constants.DEFAULT_BUFFER_SIZE);
        this.bufferSize = bufferSize >= Constants.MIN_BUFFER_SIZE && bufferSize <= Constants.MAX_BUFFER_SIZE ? 
                bufferSize : Constants.DEFAULT_BUFFER_SIZE;
    }
    private ChannelBuffer buffer = ChannelBuffers.EMPTY_BUFFER;

    @Override
    public void channelRead0(io.netty.channel.ChannelHandlerContext ctx, io.netty.buffer.ByteBuf input) throws Exception {
        int readable = input.readableBytes();
        if (readable <= 0) {
            return;
        }

        ChannelBuffer message;
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
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

}
