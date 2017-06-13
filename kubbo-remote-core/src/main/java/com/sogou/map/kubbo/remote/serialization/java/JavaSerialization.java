package com.sogou.map.kubbo.remote.serialization.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.Serialization;

public class JavaSerialization implements Serialization  {

    public static final String NAME = "java";

    @Override
    public byte getContentTypeId() {
        return 1;
    }

    @Override
    public String getContentType() {
        return "x-application/java";
    }

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new JavaObjectOutput(output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new JavaObjectInput(input);
    }
}
