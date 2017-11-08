package com.sogou.map.kubbo.rpc;

/**
 * RpcException
 * 
 * @author liufuliang
 */
public final class RpcException extends RuntimeException {

    private static final long serialVersionUID = 7815426752583648734L;

    public static final int CODE_UNKNOWN = 0;
    
    public static final int CODE_NETWORK = 1;
    
    public static final int CODE_TIMEOUT = 2;
    
    public static final int CODE_BIZ = 3;
    
    public static final int CODE_FORBIDDEN = 4;
    
    public static final int CODE_SERIALIZATION = 5;
        
    private int code = CODE_UNKNOWN;

    public RpcException() {
        super();
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(int code) {
        super();
        this.code = code;
    }

    public RpcException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RpcException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
    
    public boolean isBiz() {
        return code == CODE_BIZ;
    }
    
    public boolean isForbidded() {
        return code == CODE_FORBIDDEN;
    }

    public boolean isTimeout() {
        return code == CODE_TIMEOUT;
    }

    public boolean isNetwork() {
        return code == CODE_NETWORK;
    }

    public boolean isSerialization() {
        return code == CODE_SERIALIZATION;
    }
}