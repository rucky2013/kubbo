package com.sogou.map.kubbo.remote.transport.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.Codec;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffers;
import com.sogou.map.kubbo.remote.buffer.DynamicChannelBuffer;

/**
 * NettyCodecAdapter.
 * 
 * @author liufuliang
 */
final class NettyCodecAdapter {
    private final ChannelHandler encoder = new InternalEncoder();
    
    private final ChannelHandler decoder = new InternalDecoder();

    private final Codec        codec;
    
    private final URL            url;
    
    private final int            bufferSize;
    
    private final com.sogou.map.kubbo.remote.ChannelHandler handler;

    public NettyCodecAdapter(Codec codec, URL url, com.sogou.map.kubbo.remote.ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
        int bufferSize = url.getPositiveParameter(Constants.BUFFER_KEY, Constants.DEFAULT_BUFFER_SIZE);
        this.bufferSize = bufferSize >= Constants.MIN_BUFFER_SIZE && bufferSize <= Constants.MAX_BUFFER_SIZE ? 
        		bufferSize : Constants.DEFAULT_BUFFER_SIZE;
    }

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    @ChannelHandler.Sharable
    private class InternalEncoder extends MessageToMessageEncoder<Object> {

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
    }

    private class InternalDecoder extends SimpleChannelInboundHandler<ByteBuf> {

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
}