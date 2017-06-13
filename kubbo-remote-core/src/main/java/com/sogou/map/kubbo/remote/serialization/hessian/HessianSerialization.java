package com.sogou.map.kubbo.remote.serialization.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.Serialization;

/**
 * @author liufuliang
 */
public class HessianSerialization implements Serialization {

    public static final String NAME = "hessian";

    @Override
    public byte getContentTypeId() {
        return 3;
    }

    @Override
    public String getContentType() {
        return "x-application/hessian";
    }

    @Override
    public ObjectOutput serialize(OutputStream out) throws IOException {
        return new HessianObjectOutput(out);
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException {
        return new HessianObjectInput(is);
    }

}