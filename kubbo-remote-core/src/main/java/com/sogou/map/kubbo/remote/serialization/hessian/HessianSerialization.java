package com.sogou.map.kubbo.remote.serialization.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.Serialization;

/**
 * @author liufuliang
 */
public class HessianSerialization implements Serialization {
    public static final String NAME = "hessian";

    public byte getContentTypeId() {
        return 3;
    }

    public String getContentType() {
        return "x-application/hessian";
    }

    public ObjectOutput serialize(URL url, OutputStream out) throws IOException {
        return new HessianObjectOutput(out);
    }

    public ObjectInput deserialize(URL url, InputStream is) throws IOException {
        return new HessianObjectInput(is);
    }

}