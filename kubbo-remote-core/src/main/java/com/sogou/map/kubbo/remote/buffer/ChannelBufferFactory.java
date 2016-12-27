
package com.sogou.map.kubbo.remote.buffer;

import java.nio.ByteBuffer;

/**
 * @author liufuliang
 */
public interface ChannelBufferFactory {

    ChannelBuffer getBuffer(int capacity);
    
    ChannelBuffer getBuffer(byte[] array, int offset, int length);
    
    ChannelBuffer getBuffer(ByteBuffer nioBuffer);
    
}
