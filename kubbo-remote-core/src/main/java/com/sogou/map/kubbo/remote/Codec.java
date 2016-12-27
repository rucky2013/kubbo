package com.sogou.map.kubbo.remote;

import java.io.IOException;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffer;

/**
 * @author liufuliang
 */
@SPI
public interface Codec {

    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_SOME_INPUT
    }
    
    @Adaptive({Constants.CODEC_KEY})
    void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException;
    
    @Adaptive({Constants.CODEC_KEY})
    Object decode(Channel channel, ChannelBuffer buffer) throws IOException;

}

