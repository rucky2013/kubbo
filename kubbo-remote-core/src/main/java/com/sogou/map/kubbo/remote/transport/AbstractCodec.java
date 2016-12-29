package com.sogou.map.kubbo.remote.transport;

import java.io.IOException;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Codec;

/**
 * AbstractCodec
 * 
 * @author liufuliang
 */
public abstract class AbstractCodec implements Codec {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCodec.class);
    
    protected static void checkPayload(Channel channel, long size) throws IOException {
        int payload = Constants.DEFAULT_PAYLOAD;
        if (channel != null && channel.getUrl() != null) {
            payload = channel.getUrl().getParameter(Constants.PAYLOAD_KEY, Constants.DEFAULT_PAYLOAD);
        }
        if (payload > 0 && size > payload) {
            IOException e = new IOException("Data length too large: " + size + ", max payload: " + payload + ", channel: " + channel);
            logger.error(e);
            throw e;
        }
    }
}
