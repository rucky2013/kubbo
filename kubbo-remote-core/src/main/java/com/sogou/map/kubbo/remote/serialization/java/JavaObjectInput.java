package com.sogou.map.kubbo.remote.serialization.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;

import com.sogou.map.kubbo.remote.serialization.ObjectInput;

/**
 * @author liufuliang
 */
public class JavaObjectInput implements ObjectInput {

    private final ObjectInputStream inputStream;

    public JavaObjectInput(InputStream is) throws IOException {
        this(new ObjectInputStream(is));
    }

    protected JavaObjectInput(ObjectInputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("input == NULL");
        }
        inputStream = input;
    }

    protected ObjectInputStream getObjectInputStream() {
        return inputStream;
    }

	public Object readObject() throws IOException, ClassNotFoundException{
		byte b = getObjectInputStream().readByte();
		if( b == 0 )
			return null;
		return getObjectInputStream().readObject();
	}

	@SuppressWarnings("unchecked")
	public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
		return (T) readObject();
	}

	@SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls, Type type) throws IOException,ClassNotFoundException
    {
        return (T) readObject();
    }

    public boolean readBool() throws IOException {
        return inputStream.readBoolean();
    }

    public byte readByte() throws IOException {
        return inputStream.readByte();
    }

    public short readShort() throws IOException {
        return inputStream.readShort();
    }

    public int readInt() throws IOException {
        return inputStream.readInt();
    }

    public long readLong() throws IOException {
        return inputStream.readLong();
    }

    public float readFloat() throws IOException {
        return inputStream.readFloat();
    }

    public double readDouble() throws IOException {
        return inputStream.readDouble();
    }

	public String readUTF() throws IOException{
		int len = getObjectInputStream().readInt();
		if( len < 0 )
			return null;

		return getObjectInputStream().readUTF();
	}

	public byte[] readBytes() throws IOException{
		int len = getObjectInputStream().readInt();
		if( len < 0 )
			return null;
		if( len == 0 )
			return new byte[0];
		byte[] b = new byte[len];
		getObjectInputStream().readFully(b);
		return b;
	}
}
