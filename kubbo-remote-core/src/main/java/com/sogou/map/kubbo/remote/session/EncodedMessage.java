/**
 * 
 */
package com.sogou.map.kubbo.remote.session;

/**
 * @author liufuliang
 *
 */
public class EncodedMessage {
    
    byte[] bytes;
    
    public EncodedMessage(byte[] bytes) {
        super();
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
