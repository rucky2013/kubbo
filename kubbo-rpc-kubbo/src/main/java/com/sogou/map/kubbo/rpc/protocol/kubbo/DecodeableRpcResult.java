package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.io.IOException;
import java.io.InputStream;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Decodeable;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.Serialization;
import com.sogou.map.kubbo.remote.serialization.Serializations;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.rpc.RpcResult;
import com.sogou.map.kubbo.rpc.protocol.kubbo.codec.KubboCodec;


/**
 * @author liufuliang
 */
public class DecodeableRpcResult extends RpcResult implements Decodeable {
    
    private static final Logger log = LoggerFactory.getLogger(DecodeableRpcResult.class);

    private static final long serialVersionUID = -4446367169516521258L;

    private Channel channel;

    private Serialization serialization;
    
    private InputStream input;

    private Response response;

    private volatile boolean hasDecoded;

    public DecodeableRpcResult(Channel channel, Serialization serialization, InputStream input, Response response) {
        if (channel == null) {
            throw new IllegalArgumentException("channel == NULL");
        }
        if (response == null) {
            throw new IllegalArgumentException("response == NULL");
        }
        if (serialization == null) {
            throw new IllegalArgumentException("serialization == NULL");
        }
        if (input == null) {
            throw new IllegalArgumentException("input == NULL");
        }
        this.channel = channel;
        this.response = response;
        this.serialization = serialization;
        this.input = input;
    }

    @Override
    public void decode() {
        if (!hasDecoded) {
            try {
                decode(channel, serialization, input);
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
    
    public Object decode(Channel channel, Serialization serialization, InputStream input) throws IOException {
        ObjectInput objectInput = serialization.deserialize(input);

        try {
            byte flag = objectInput.readByte();
            switch (flag) {
            case KubboCodec.FLAG_RESPONSE_NULL_VALUE:
                break;
            case KubboCodec.FLAG_RESPONSE_VALUE:
                try {
                    setValue(objectInput.readObject());
                } catch (ClassNotFoundException e) {
                    throw new IOException(StringUtils.toString("Read response data failed.", e));
                }
                break;
            case KubboCodec.FLAG_RESPONSE_EXCEPTION:
                try {
                    Object obj = objectInput.readObject();
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

        } finally {
            Serializations.releaseSafely(objectInput);
        }
        return this;
    }
    
}
