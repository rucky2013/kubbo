package com.sogou.map.kubbo.remote.serialization.java;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.sogou.map.kubbo.remote.serialization.ObjectOutput;

/**
 * @author liufuliang
 */
public class JavaObjectOutput implements ObjectOutput {

    private final ObjectOutputStream outputStream;

    public JavaObjectOutput(OutputStream os) throws IOException {
        this(new ObjectOutputStream(os));
    }

    protected JavaObjectOutput(ObjectOutputStream output) {
        if (output == null) {
            throw new IllegalArgumentException("output == NULL");
        }
        this.outputStream = output;
    }

    protected ObjectOutputStream getObjectOutputStream() {
        return outputStream;
    }

    public void writeObject(Object obj) throws IOException{
        if( obj == null ){
            outputStream.writeByte(0);
        }
        else{
            outputStream.writeByte(1);
            outputStream.writeObject(obj);
        }
    }

    public void writeBool(boolean v) throws IOException {
        outputStream.writeBoolean(v);
    }

    public void writeByte(byte v) throws IOException {
        outputStream.writeByte(v);
    }

    public void writeShort(short v) throws IOException {
        outputStream.writeShort(v);
    }

    public void writeInt(int v) throws IOException {
        outputStream.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        outputStream.writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        outputStream.writeFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        outputStream.writeDouble(v);
    }
    
    public void writeUTF(String v) throws IOException{
        if( v == null ){
            getObjectOutputStream().writeInt(-1);
        }
        else{
            outputStream.writeInt(v.length());
            outputStream.writeUTF(v);
        }
    }


    public void writeBytes(byte[] v) throws IOException {
        if (v == null) {
            outputStream.writeInt(-1);
        } else {
            writeBytes(v, 0, v.length);
        }
    }

    public void writeBytes(byte[] v, int off, int len) throws IOException {
        if (v == null) {
            outputStream.writeInt(-1);
        } else {
            outputStream.writeInt(len);
            outputStream.write(v, off, len);
        }
    }

    public void flushBuffer() throws IOException {
        outputStream.flush();
    }
}
