package com.sogou.map.kubbo.rpc.protocol.kubbo.codec;

import java.io.IOException;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.Version;
import com.sogou.map.kubbo.common.utils.ReflectUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Codec;
import com.sogou.map.kubbo.remote.buffer.ChannelBuffer;
import com.sogou.map.kubbo.remote.serialization.ObjectInput;
import com.sogou.map.kubbo.remote.serialization.ObjectOutput;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.remote.session.codec.SessionCodec;
import com.sogou.map.kubbo.remote.transport.MessageArray;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcInvocation;
import com.sogou.map.kubbo.rpc.RpcResult;
import com.sogou.map.kubbo.rpc.protocol.kubbo.DecodeableRpcInvocation;
import com.sogou.map.kubbo.rpc.protocol.kubbo.DecodeableRpcResult;

/**
 * Kubbo codec.
 *
 * @author liufuliang
 */
public class KubboCodec extends SessionCodec implements Codec {
    public static final String NAME = "kubbo";

    public static final String KUBBO_VERSION = Version.getVersion(KubboCodec.class, Version.getVersion());

    public static final byte RESPONSE_WITH_EXCEPTION = 0;

    public static final byte RESPONSE_VALUE = 1;

    public static final byte RESPONSE_NULL_VALUE = 2;

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    @Override
    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        int save = buffer.readerIndex();
        MessageArray result = MessageArray.create();
        do {
            Object obj = super.decode(channel, buffer);
            if (obj == DecodeResult.NEED_MORE_INPUT) {
                buffer.readerIndex(save);
                break;
            } else {
                result.addMessage(obj);
                logMessageLength(obj, buffer.readerIndex() - save);
                save = buffer.readerIndex();
            }
        } while (true);

        if (result.isEmpty()) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        if (result.size() == 1) {
            return result.get(0);
        }

        return result;
    }

    private void logMessageLength(Object result, int bytes) {
        if (bytes <= 0) { return; }
        if (result instanceof Request) {
            try {
                ((RpcInvocation) ((Request) result).getData()).setAttachment(Constants.INPUT_KEY, String.valueOf(bytes));
            } catch (Throwable e) {
                /* ignore */
            }
        } else if (result instanceof Response) {
            try {
                ((RpcResult) ((Response) result).getResult()).setAttachment(Constants.OUTPUT_KEY, String.valueOf(bytes));
            } catch (Throwable e) {
                /* ignore */
            }
        }
    }
    
    
    @Override
    protected void encodeRequestData(Channel channel, ObjectOutput out, Object data) throws IOException {
        RpcInvocation inv = (RpcInvocation) data;
        out.writeUTF(inv.getMethodName());
        out.writeUTF(ReflectUtils.getDesc(inv.getParameterTypes()));
        Object[] args = inv.getArguments();
        if (args != null)
            for (int i = 0; i < args.length; i++){
                out.writeObject(args[i]);
            }
        out.writeObject(inv.getAttachments());
    }

    @Override
    protected void encodeResponseData(Channel channel, ObjectOutput out, Object data) throws IOException {
        Result result = (Result) data;

        if(result.hasException()){
            out.writeByte(RESPONSE_WITH_EXCEPTION);
            out.writeObject(result.getException());
        } else{
            Object ret = result.getValue();
            if (ret == null) {
                out.writeByte(RESPONSE_NULL_VALUE);
            } else {
                out.writeByte(RESPONSE_VALUE);
                out.writeObject(ret);
            }
        }
    }
    
    @Override
    protected Object decodeRequestData(Channel channel, ObjectInput in, Request request) throws IOException {
        DecodeableRpcInvocation inv = new DecodeableRpcInvocation(channel, in, request);
        //直接在io线程中解码
        inv.decode();
        return inv;
    }

    @Override
    protected Object decodeResponseData(Channel channel, ObjectInput in, Response response) throws IOException {
        DecodeableRpcResult result = new DecodeableRpcResult(channel, in, response);
        //直接在io线程中解码
        result.decode();
        return result;
    }
}