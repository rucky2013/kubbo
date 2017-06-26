package com.sogou.map.kubbo.rpc.utils;

import java.util.concurrent.atomic.AtomicLong;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.RpcInvocation;

/**
 * RpcUtils
 * 
 * @author liufuliang
 */
public class RpcHelper {
    
    private static final AtomicLong INVOKE_ID = new AtomicLong(0);
    
    public static Long getInvocationId(Invocation inv) {
        String id = inv.getAttachment(Constants.ID_KEY);
        return id == null ? null : new Long(id);
    }
    
    /**
     * 幂等操作:异步操作默认添加invocation id
     * @param url
     * @param inv
     */
    public static void attachInvocationIdIfAsync(URL url, Invocation inv){
        if (shouldAttachInvocationId(url, inv) && getInvocationId(inv) == null && inv instanceof RpcInvocation) {
            ((RpcInvocation)inv).setAttachment(Constants.ID_KEY, String.valueOf(INVOKE_ID.getAndIncrement()));
        }
    }
    
    private static boolean shouldAttachInvocationId(URL url , Invocation invocation) {
        String value = url.getMethodParameter(invocation.getMethodName(), Constants.AUTO_ATTACH_INVOCATIONID_KEY);
        if ( value == null ) {
            //异步操作默认添加invocationid
            return isAsync(url,invocation) ;
        } else if (Constants.TRUE.equalsIgnoreCase(value)) {
            //设置为添加，则一定添加
            return true;
        } else {
            //value为false时，不添加
            return false;
        }
    }
    
    public static String getMethodName(Invocation invocation){
        if(Constants.$INVOKE.equals(invocation.getMethodName()) 
                && invocation.getArguments() != null 
                && invocation.getArguments().length > 0 
                && invocation.getArguments()[0] instanceof String){
            return (String) invocation.getArguments()[0];
        }
        return invocation.getMethodName();
    }

    public static Object[] getArguments(Invocation invocation){
        if(Constants.$INVOKE.equals(invocation.getMethodName()) 
                && invocation.getArguments() != null 
                && invocation.getArguments().length > 2
                && invocation.getArguments()[2] instanceof Object[]){
            return (Object[]) invocation.getArguments()[2];
        }
        return invocation.getArguments();
    }
    
    public static boolean isAsync(URL url, Invocation inv) {
        boolean isAsync ;
        //如果Java代码中设置优先.
        if (Constants.TRUE.equals(inv.getAttachment(Constants.ASYNC_KEY))) {
            isAsync = true;
        } else {
            isAsync = url.getMethodParameter(getMethodName(inv), Constants.ASYNC_KEY, false);
        }
        return isAsync;
    }
    
    public static boolean isOneway(URL url, Invocation inv) {
        boolean isOneway ;
        //如果Java代码中设置优先.
        if (Constants.TRUE.equals(inv.getAttachment(Constants.ONEWAY_KEY))) {
            isOneway = true;
        } else {
            isOneway = url.getMethodParameter(getMethodName(inv), Constants.ONEWAY_KEY, false);
        }
        return isOneway;
    }
    
    public static String serviceKey(URL url){
        return serviceKey(url.getParameter(Constants.GROUP_KEY),
                url.getPath(),
                url.getServiceInterface(),
                url.getParameter(Constants.VERSION_KEY));
    }
            
    // group/path/interface:version
    public static String serviceKey(
            String serviceGroup, 
            String servicePath, 
            String interfaceType, 
            String serviceVersion) {
        StringBuilder buf = new StringBuilder();
        //group
        if (!StringUtils.isBlank(serviceGroup)) {
            buf.append(serviceGroup);
            buf.append("/");
        }
        //path
        buf.append(servicePath);
        //interface
        if(!StringUtils.isBlank(interfaceType)){
            buf.append("/");
            buf.append(interfaceType);
        }
        //version
        if (!StringUtils.isBlank(serviceVersion)) {
            buf.append(":");
            buf.append(serviceVersion);
        }
        return buf.toString();
    } 
    
    
    private RpcHelper(){
        
    }
}
