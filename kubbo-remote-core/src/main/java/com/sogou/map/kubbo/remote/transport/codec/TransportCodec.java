package com.sogou.map.kubbo.remote.transport.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sogou.map.kubbo.common.utils.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffer;
import com.sogou.map.kubbo.remote.buffer.ChannelBufferInputStream;
import com.sogou.map.kubbo.remote.buffer.ChannelBufferOutputStream;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.Releasable;
import com.sogou.map.kubbo.remote.serialization.Serializations;
import com.sogou.map.kubbo.remote.transport.AbstractCodec;

/**
 * TransportCodec
 * 
 * @author liufuliang
 */
public class TransportCodec extends AbstractCodec {
	@Override
    public void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException {
        OutputStream output = new ChannelBufferOutputStream(buffer);
        ObjectOutput objectOutput = Serializations.getSerialization(channel.getUrl()).serialize(channel.getUrl(), output);
        encodeData(channel, objectOutput, message);
        objectOutput.flushBuffer();
        if (objectOutput instanceof Releasable) {
            ((Releasable) objectOutput).release();
        }
    }
    @Override
    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        InputStream input = new ChannelBufferInputStream(buffer);
        ObjectInput objectInput = Serializations.getSerialization(channel.getUrl()).deserialize(channel.getUrl(), input);
        Object data = decodeData(channel, objectInput);
        if (objectInput instanceof Releasable) {
            ((Releasable) objectInput).release();
        }
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