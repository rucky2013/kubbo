package com.sogou.map.kubbo.remote.serialization.hessian;

import java.io.IOException;
import java.io.OutputStream;

import com.caucho.hessian.io.Hessian2Output;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;

/**
 * Hessian2 Object output.
 * 
 * @author liufuliang
 */

public class HessianObjectOutput implements ObjectOutput {
    
    private final Hessian2Output hessianOutput;

    public HessianObjectOutput(OutputStream os) {
        hessianOutput = new Hessian2Output(os);
        hessianOutput.setSerializerFactory(HessianSerializerFactory.SERIALIZER_FACTORY);
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        hessianOutput.writeBoolean(v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        hessianOutput.writeInt(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        hessianOutput.writeInt(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        hessianOutput.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        hessianOutput.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        hessianOutput.writeDouble(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        hessianOutput.writeDouble(v);
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        hessianOutput.writeBytes(b);
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        hessianOutput.writeBytes(b, off, len);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        hessianOutput.writeString(v);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        hessianOutput.writeObject(obj);
    }

    @Override
    public void flushBuffer() throws IOException {
        hessianOutput.flushBuffer();
    }
}