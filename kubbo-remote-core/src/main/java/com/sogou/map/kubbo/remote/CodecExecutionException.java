package com.sogou.map.kubbo.remote;

/**
 * CodecExecutionException
 * 
 * @author liufuliang
 */
public class CodecExecutionException extends ExecutionException {

    private static final long serialVersionUID = -1443137847476756918L;

    public CodecExecutionException(Object request, Channel channel, String message, Throwable cause){
        super(request, channel, message, cause);
    }

    public CodecExecutionException(Object request, Channel channel, String msg){
        super(request, channel, msg);
    }

    public CodecExecutionException(Object request, Channel channel, Throwable cause){
        super(request, channel, cause);
    }

}