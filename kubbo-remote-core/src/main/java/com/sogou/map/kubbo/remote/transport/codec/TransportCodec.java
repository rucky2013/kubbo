package com.sogou.map.kubbo.remote.transport.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffer;
import com.sogou.map.kubbo.remote.buffer.ChannelBufferInputStream;
import com.sogou.map.kubbo.remote.buffer.ChannelBufferOutputStream;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.Serialization;
import com.sogou.map.kubbo.remote.serialization.Serializations;
import com.sogou.map.kubbo.remote.transport.AbstractCodec;

/**
 * TransportCodec
 * 
 * @author liufuliang
 */
public class TransportCodec extends AbstractCodec {
    
    public static final String NAME = "transport";

    @Override
    public void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException {
        OutputStream output = new ChannelBufferOutputStream(buffer);
        Serialization s = Serializations.getSerialization(channel.getUrl());
        encodeData(channel, s, output, message);
    }
    
    @Override
    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        InputStream input = new ChannelBufferInputStream(buffer);
        Serialization s = Serializations.getSerialization(channel.getUrl());
        return decodeData(channel, s, input);
    }

    protected void encodeData(Channel channel, Serialization serialization, OutputStream output, Object message) throws IOException {
        ObjectOutput objectOutput = serialization.serialize(output);
        encodeData(objectOutput, message);
        objectOutput.flushBuffer();
        Serializations.releaseSafely(objectOutput);
    }
    
    protected Object decodeData(Channel channel, Serialization serialization, InputStream input) throws IOException {
        ObjectInput objectInput = serialization.deserialize(input);
        Object data = decodeData(objectInput);
        Serializations.releaseSafely(objectInput);
        return data;
    }
    
    protected void encodeData(Channel channel, ObjectOutput output, Object message) throws IOException {
        encodeData(output, message);
    }
    
    protected Object decodeData(Channel channel, ObjectInput input) throws IOException {
        return decodeData(input);
    }
    

    protected void encodeData(ObjectOutput output, Object message) throws IOException {
        output.writeObject(message);
    }
    
    protected Object decodeData(ObjectInput input) throws IOException {
        try {
            return input.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("ClassNotFoundException: " + StringUtils.toString(e));
        }
    }
}