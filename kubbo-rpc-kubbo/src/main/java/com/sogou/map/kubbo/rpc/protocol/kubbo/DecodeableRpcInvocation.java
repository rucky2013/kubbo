
package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.sogou.map.kubbo.common.lang.Reflects;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Decodeable;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.Serialization;
import com.sogou.map.kubbo.remote.serialization.Serializations;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.rpc.RpcInvocation;
import com.sogou.map.kubbo.rpc.protocol.kubbo.codec.KubboCodec;

/**
 * @author liufuliang
 */
public class DecodeableRpcInvocation extends RpcInvocation implements Decodeable {

    private static final long serialVersionUID = 6029662240065402716L;

    private static final Logger log = LoggerFactory.getLogger(DecodeableRpcInvocation.class);

    private Channel channel;

    private Serialization serialization;
    
    private InputStream input;

    private Request request;

    private volatile boolean hasDecoded;

    public DecodeableRpcInvocation(Channel channel, Serialization serialization, InputStream input, Request request) {
        if (channel == null) {
            throw new IllegalArgumentException("channel == NULL");
        }
        if (request == null) {
            throw new IllegalArgumentException("request == NULL");
        }
        if (serialization == null) {
            throw new IllegalArgumentException("serialization == NULL");
        }
        if (input == null) {
            throw new IllegalArgumentException("input == NULL");
        }
        
        this.channel = channel;
        this.request = request;
        this.serialization = serialization;
        this.input = input;
    }

    @Override
    public void decode(){
        if (!hasDecoded) {
            try {
                decode(channel, serialization, input);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("Decode rpc invocation failed: " + e.getMessage(), e);
                }
                request.setBroken(true);
                request.setData(e);
            } finally {
                hasDecoded = true;
            }
        } else{
            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder(32).append("Already Decode decodeable message ").append(toString()).toString());
            }
        }
        
    }

    public Object decode(Channel channel, Serialization serialization, InputStream input) throws IOException {
        ObjectInput objectInput = serialization.deserialize(input);
        setMethodName(objectInput.readUTF());
        try {
            Object[] args;
            Class<?>[] pts;
            String desc = objectInput.readUTF();
            if (desc.length() == 0) {
                pts = KubboCodec.EMPTY_CLASS_ARRAY;
                args = KubboCodec.EMPTY_OBJECT_ARRAY;
            } else {
                pts = Reflects.desc2classArray(desc);
                args = new Object[pts.length];
                for (int i = 0; i < args.length; i++) {
                    try {
                        args[i] = objectInput.readObject(pts[i]);
                    } catch (Exception e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Decode argument failed: " + e.getMessage(), e);
                        }
                    }
                }
            }
            setParameterTypes(pts);
            setArguments(args);

            @SuppressWarnings("unchecked")
            Map<String, String> attachment = (Map<String, String>) objectInput.readObject(Map.class);
            setAttachments(attachment);

        } catch (ClassNotFoundException e) {
            throw new IOException(StringUtils.toString("Read invocation data failed.", e));
        } finally {
            Serializations.releaseSafely(objectInput);
        }
        return this;
    }

}
