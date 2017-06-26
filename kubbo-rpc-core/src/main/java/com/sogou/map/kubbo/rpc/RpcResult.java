package com.sogou.map.kubbo.rpc;

import java.io.Serializable;
import java.util.HashMap;
import com.sogou.map.kubbo.rpc.protocol.AbstractAttachable;

/**
 * RPC Result.
 * 
 * @serial Don't change the class name and properties.
 * @author liufuliang
 */
public class RpcResult extends AbstractAttachable<RpcResult> implements Result, Serializable {

    private static final long serialVersionUID = -6925924956850004727L;
    
    public static final RpcResult NULL = new RpcResult();
    public static final RpcResult ASYNC = new RpcResult();
    public static final RpcResult ONEWAY = new RpcResult();

    private Object result;
    
    private Throwable exception;

    public RpcResult(){
        super(new HashMap<String, String>());
    }
    
    public RpcResult(Object result){
        this();
        this.result = result;
    }

    public RpcResult(Throwable exception){
        this();
        this.exception = exception;
    }

    @Override
    public Object recreate() throws Throwable {
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    @Override
    public Object getValue() {
        return result;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    public void setValue(Object value) {
        this.result = value;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }

    @Override
    public String toString() {
        return "RpcResult [result=" + result + ", exception=" + exception + "]";
    }
}