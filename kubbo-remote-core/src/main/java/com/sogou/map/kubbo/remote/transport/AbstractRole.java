package com.sogou.map.kubbo.remote.transport;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Codec;
import com.sogou.map.kubbo.remote.Resetable;
import com.sogou.map.kubbo.remote.session.codec.SessionCodec;

/**
 * AbstractEndpoint
 * 
 * @author liufuliang
 */
public abstract class AbstractRole extends AbstractEndpoint implements Resetable{
    private static final Logger logger = LoggerFactory.getLogger(AbstractRole.class);

    private Codec              	codec;

    private int                   connectTimeout;
    
    public AbstractRole(URL url, ChannelHandler handler) {
        super(url, handler);
        this.codec = getChannelCodec(url);
        this.connectTimeout = url.getPositiveParameter(Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);
    }

    protected Codec getCodec() {
        return codec;
    }

    protected int getConnectTimeout() {
        return connectTimeout;
    }

    protected static Codec getChannelCodec(URL url) {
        String codecName = url.getParameter(Constants.CODEC_KEY, SessionCodec.NAME);
        Codec codec = ExtensionLoader.getExtensionLoader(Codec.class).getExtension(codecName);
        if (codec == null) {
            throw new IllegalArgumentException("codec == NULL");
        }
        return codec;
    }
    
    @Override
    public void reset(URL url) {
        if(url == null){
            return;
        }
        if (isClosed()) {
            throw new IllegalStateException("Failed to reset parameters "
                                        + url + ", cause: Channel closed. channel: " + getLocalAddress());
        }
        
        //connect timeout
        try {
            if (url.hasParameter(Constants.CONNECT_TIMEOUT_KEY)) {
                int t = url.getParameter(Constants.CONNECT_TIMEOUT_KEY, 0);
                if (t > 0) {
                    this.connectTimeout = t;
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        
        //codec
        try {
            if (url.hasParameter(Constants.CODEC_KEY)) {
                this.codec = getChannelCodec(url);
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }
    

}