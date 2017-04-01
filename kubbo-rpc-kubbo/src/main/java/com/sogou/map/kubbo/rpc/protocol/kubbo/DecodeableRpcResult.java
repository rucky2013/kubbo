package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.io.IOException;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Decodeable;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.rpc.RpcResult;
import com.sogou.map.kubbo.rpc.protocol.kubbo.codec.KubboCodec;


/**
 * @author liufuliang
 */
public class DecodeableRpcResult extends RpcResult implements Decodeable {

    private static final long serialVersionUID = -4446367169516521258L;

    private static final Logger log = LoggerFactory.getLogger(DecodeableRpcResult.class);

    private Channel     channel;

    private ObjectInput input;

    private Response    response;

    private volatile boolean hasDecoded;

    public DecodeableRpcResult(Channel channel, ObjectInput input, Response response) {
        if (channel == null) {
            throw new IllegalArgumentException("channel == NULL");
        }
        if (response == null) {
            throw new IllegalArgumentException("response == NULL");
        }
        if (input == null) {
            throw new IllegalArgumentException("input == NULL");
        }
        this.channel = channel;
        this.response = response;
        this.input = input;
    }

    @Override
    public void decode() {
        if (!hasDecoded && channel != null && input != null) {
            try {
                decode(channel, input);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("Decode rpc result failed: " + e.getMessage(), e);
                }
                response.setStatus(Response.CLIENT_ERROR);
                response.setErrorMessage(StringUtils.toString(e));
            } finally {
                hasDecoded = true;
            }
        }
    }
    
    public Object decode(Channel channel, ObjectInput in) throws IOException {
        byte flag = in.readByte();
        switch (flag) {
            case KubboCodec.RESPONSE_NULL_VALUE:
                break;
            case KubboCodec.RESPONSE_VALUE:
                try {
                    setValue(in.readObject());
//                    Type[] returnType = RpcUtils.getReturnTypes(invocation);
//                    setValue(returnType == null || returnType.length == 0 ? in.readObject() :
//                                 (returnType.length == 1 ? in.readObject((Class<?>) returnType[0])
//                                     : in.readObject((Class<?>) returnType[0], returnType[1])));
                } catch (ClassNotFoundException e) {
                    throw new IOException(StringUtils.toString("Read response data failed.", e));
                }
                break;
            case KubboCodec.RESPONSE_WITH_EXCEPTION:
                try {
                    Object obj = in.readObject();
                    if (!(obj instanceof Throwable))
                        throw new IOException("Response data error, expect Throwable, but get " + obj);
                    setException((Throwable) obj);
                } catch (ClassNotFoundException e) {
                    throw new IOException(StringUtils.toString("Read response data failed.", e));
                }
                break;
            default:
                throw new IOException("Unknown result flag, expect '0' '1' '2', get " + flag);
        }
        return this;
    }



}
