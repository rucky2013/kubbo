package com.sogou.map.kubbo.remote.serialization.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import com.caucho.hessian.io.Hessian2Input;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;

/**
 * Hessian2 Object input.
 * 
 * @author liufuliang
 */

public class HessianObjectInput implements ObjectInput {
    private final Hessian2Input hessianInput;

    public HessianObjectInput(InputStream is) {
        hessianInput = new Hessian2Input(is);
        hessianInput.setSerializerFactory(HessianSerializerFactory.SERIALIZER_FACTORY);
    }

    public boolean readBool() throws IOException {
        return hessianInput.readBoolean();
    }

    public byte readByte() throws IOException {
        return (byte) hessianInput.readInt();
    }

    public short readShort() throws IOException {
        return (short) hessianInput.readInt();
    }

    public int readInt() throws IOException {
        return hessianInput.readInt();
    }

    public long readLong() throws IOException {
        return hessianInput.readLong();
    }

    public float readFloat() throws IOException {
        return (float) hessianInput.readDouble();
    }

    public double readDouble() throws IOException {
        return hessianInput.readDouble();
    }

    public byte[] readBytes() throws IOException {
        return hessianInput.readBytes();
    }

    public String readUTF() throws IOException {
        return hessianInput.readString();
    }

    public Object readObject() throws IOException {
        return hessianInput.readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        return (T) hessianInput.readObject(cls);
    }

    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        return readObject(cls);
    }

}