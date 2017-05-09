package com.sogou.map.kubbo.remote.serialization.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.Serialization;

/**
 * TODO for now kryo serialization doesn't deny classes that don't implement the serializable interface
 *
 * @author liufuliang
 */
public class KryoSerialization implements Serialization {
    public static final String NAME = "kryo";

    public byte getContentTypeId() {
        return 2;
    }

    public String getContentType() {
        return "x-application/kryo";
    }

    public ObjectOutput serialize(OutputStream out) throws IOException {
        return new KryoObjectOutput(out);
    }

    public ObjectInput deserialize(InputStream in) throws IOException {
        return new KryoObjectInput(in);
    }
}