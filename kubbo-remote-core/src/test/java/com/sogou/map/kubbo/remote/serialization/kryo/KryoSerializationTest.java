/**
 * 
 */
package com.sogou.map.kubbo.remote.serialization.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.serialization.Serialization;

/**
 * @author liufuliang
 *
 */
public class KryoSerializationTest {
    
    public static class Message implements Serializable {
        private static final long serialVersionUID = 195394259076471993L;
        int value;
        public Message(){}
        public int getValue() {
            return value;
        }
        public void setValue(int value) {
            this.value = value;
        }
    }
    
    @Test
    public void testSerializeAndDeserialize() throws IOException, ClassNotFoundException{
        Serialization serialization = new KryoSerialization();
        
        // serialize
        Message message = new Message();
        message.setValue(10);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        
        ObjectOutput ouput = serialization.serialize(ostream);
        ouput.writeObject(message);
        ouput.flushBuffer();
        ostream.flush();
        
        // deserialize
        InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
        ObjectInput input = serialization.deserialize(istream);
        Message m = input.readObject(Message.class);
        
        Assert.assertEquals(message.getValue(), m.getValue());
    }
}
