package com.sogou.map.kubbo.remote;

/**
 * RemoteExecutionException
 * 
 * @author liufuliang
 */
public class RemoteExecutionException extends ExecutionException {
    
    private static final long serialVersionUID = -2531085236111056860L;
    
    public RemoteExecutionException(Object request, Channel channel, String message, Throwable cause){
        super(request, channel, message, cause);
    }

    public RemoteExecutionException(Object request, Channel channel, String msg){
        super(request, channel, msg);
    }

    public RemoteExecutionException(Object request, Channel channel, Throwable cause){
        super(request, channel, cause);
    }

}