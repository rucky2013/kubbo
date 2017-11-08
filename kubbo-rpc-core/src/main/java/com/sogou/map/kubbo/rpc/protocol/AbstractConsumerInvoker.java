package com.sogou.map.kubbo.rpc.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.NetUtils;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcContext;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.RpcInvocation;
import com.sogou.map.kubbo.rpc.RpcResult;
import com.sogou.map.kubbo.rpc.utils.RpcHelper;

/**
 * AbstractInvoker.
 * 
 * @author liufuliang
 */
public abstract class AbstractConsumerInvoker<T> implements Invoker<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final URL url;

    private final Class<T> type;

    private final Map<String, String> attachments;

    private volatile boolean available = true;

    private volatile boolean destroyed = false;
    
    public AbstractConsumerInvoker(Class<T> type, URL url){
        this(type, url, (Map<String, String>) null);
    }
    
    public AbstractConsumerInvoker(Class<T> type, URL url, String[] attachmentKeys) {
        this(type, url, convertAttachment(url, attachmentKeys));
    }

    public AbstractConsumerInvoker(Class<T> type, URL url, Map<String, String> attachments) {
        if (type == null)
            throw new IllegalArgumentException("service type == NULL");
        if (url == null)
            throw new IllegalArgumentException("service url == NULL");
        this.type = type;
        this.url = url;
        this.attachments = attachments == null ? null : Collections.unmodifiableMap(attachments);
    }
    
    private static Map<String, String> convertAttachment(URL url, String[] attachmentKeys) {
        if (attachmentKeys == null || attachmentKeys.length == 0) {
            return null;
        }
        Map<String, String> attachments = new HashMap<String, String>();
        for (String key : attachmentKeys) {
            String value = url.getParameter(key);
            if (!StringUtils.isBlank(value)) {
                attachments.put(key, value);
            }
        }
        return attachments;
    }
    
    @Override
    public Kind kind(){
        return Kind.CONSUMER;
    }
    
    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void destroy() {
        if (isDestroyed()) {
            return;
        }
        destroyed = true;
        setAvailable(false);
    }
    
    protected void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public String toString() {
        return getInterface() + " -> " + (getUrl() == null ? "" : getUrl().toString());
    }

    @Override
    public Result invoke(Invocation inv) throws RpcException {
        if(destroyed) {
            throw new RpcException("Rpc invoker for service " + this + " on consumer " + NetUtils.getHostAddress() 
                                            + " is DESTROYED, can not be invoked any more!");
        }
        RpcInvocation invocation = (RpcInvocation) inv;
        invocation.setInvoker(this);
        
        //invoker attachment
        if (attachments != null && attachments.size() > 0) {
            invocation.addAttachmentsIfAbsent(attachments);
        }
        
        //context attachment
        Map<String, String> context = RpcContext.get().getAttachments();
        if (context != null) {
            invocation.addAttachmentsIfAbsent(context);
        }
        
        //async or sync attachment
        if (getUrl().getMethodParameter(invocation.getMethodName(), Constants.ASYNC_KEY, false)){
            invocation.setAttachment(Constants.ASYNC_KEY, "true");
        }
        
        RpcHelper.attachInvocationIdIfAsync(getUrl(), invocation);
        
        try {
            return doInvoke(invocation);
        } catch (InvocationTargetException e) { // biz exception
            Throwable te = e.getTargetException();
            if (te == null) {
                return new RpcResult(e);
            } 
            if (te instanceof RpcException) {
                ((RpcException) te).setCode(RpcException.CODE_BIZ);
            }
            return new RpcResult(te);
        } catch (RpcException e) {
            if (e.isBiz()) {
                return new RpcResult(e);
            } else {
                throw e;
            }
        } catch (Throwable e) {
            return new RpcResult(e);
        }
    }

    protected abstract Result doInvoke(Invocation invocation) throws Throwable;

}