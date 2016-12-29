
package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.io.IOException;
import java.util.Map;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.ReflectUtils;
import com.sogou.map.kubbo.common.utils.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Decodeable;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.rpc.RpcInvocation;
import com.sogou.map.kubbo.rpc.protocol.kubbo.codec.KubboCodec;

/**
 * @author liufuliang
 */
public class DecodeableRpcInvocation extends RpcInvocation implements Decodeable {

    private static final long serialVersionUID = 6029662240065402716L;

    private static final Logger log = LoggerFactory.getLogger(DecodeableRpcInvocation.class);

    private Channel     channel;

    private ObjectInput input;

    private Request     request;

    private volatile boolean hasDecoded;

    public DecodeableRpcInvocation(Channel channel, ObjectInput input, Request request) {
        if (channel == null) {
            throw new IllegalArgumentException("channel == NULL");
        }
        if (request == null) {
            throw new IllegalArgumentException("request == NULL");
        }
        if (input == null) {
            throw new IllegalArgumentException("input == NULL");
        }
        this.channel = channel;
        this.request = request;
        this.input = input;
    }

    @Override
    public void decode(){
        if (!hasDecoded && channel != null && input != null) {
            try {
                decode(channel, input);
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

    public Object decode(Channel channel, ObjectInput in) throws IOException {
//        setAttachment(Constants.KUBBO_VERSION_KEY, in.readUTF());
//        setAttachment(Constants.PATH_KEY, in.readUTF());
//        setAttachment(Constants.VERSION_KEY, in.readUTF());

        setMethodName(in.readUTF());
        try {
            Object[] args;
            Class<?>[] pts;
            String desc = in.readUTF();
            if (desc.length() == 0) {
                pts = KubboCodec.EMPTY_CLASS_ARRAY;
                args = KubboCodec.EMPTY_OBJECT_ARRAY;
            } else {
                pts = ReflectUtils.desc2classArray(desc);
                args = new Object[pts.length];
                for (int i = 0; i < args.length; i++) {
                    try {
                        args[i] = in.readObject(pts[i]);
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
            Map<String, String> attachment = (Map<String, String>) in.readObject(Map.class);
            setAttachments(attachment);

        } catch (ClassNotFoundException e) {
            throw new IOException(StringUtils.toString("Read invocation data failed.", e));
        }
        return this;
    }

}
