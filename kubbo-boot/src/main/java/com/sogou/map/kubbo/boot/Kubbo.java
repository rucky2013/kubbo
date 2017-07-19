/**
 * 
 */package com.sogou.map.kubbo.boot;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.sogou.map.kubbo.boot.configuration.KubboConfiguration;
import com.sogou.map.kubbo.boot.configuration.PropertiesConfigurator;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.common.util.SystemPropertyUtils;
import com.sogou.map.kubbo.distributed.Distributions;
import com.sogou.map.kubbo.metrics.KubboMetrics;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.InvokerProxy;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.Protocols;
import com.sogou.map.kubbo.rpc.RpcContext;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.concurrent.ExceptionWrappedListenableFuture;
import com.sogou.map.kubbo.trace.KubboTrace;
import com.sogou.map.kubbo.trace.Trace;

/**
 * <h3>Kubbo分层架构</h3>
 * kubbo的分层参考了osi7层模型, 并增加了分布式层
 * <pre>
 *   应用层
 *  -------------------
 *   分布式层
 *  -------------------
 *   表示层(协议层)
 *  -------------------
 *   会话层
 *  -------------------
 *   传输层
 *  -------------------
 *   网络层
 *  -------------------
 * </pre>
 * 
 * @author liufuliang
 */
public class Kubbo {
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try { Kubbo.destroy(); } catch (Throwable e) {}
            }
        });
    }
    
    //所有引用过的protocol
    private static ConcurrentMap<String, Protocol> protocols = new ConcurrentHashMap<String, Protocol>();
    //所有引用过的远程接口
    private static ConcurrentMap<String, Object> services = new ConcurrentHashMap<String, Object>();
        
    public static <T> Exporter<T> export(T service, Class<T> type, String url) throws RpcException {
        return export(service, type, URL.valueOf(url));
    }
    
    public static <T> Exporter<T> export(T service, Class<T> type, URL url) throws RpcException {
        URL exportURL = attachApplicationName(url)
                .addParameter(Constants.INTERFACE_KEY, type.getName());
                
        return getProtocol(url).export(
                getAdaptiveInvokerProxy().getInvoker(service, type, exportURL)
                );
    }
    public static Exporter<?> exportGeneric(Object service, Class<?> type, URL url) throws RpcException {
        URL exportURL = attachApplicationName(url)
                .addParameter(Constants.INTERFACE_KEY, type.getName());

        return getProtocol(url).export(
                getAdaptiveInvokerProxy().getGenericInvoker(service, type, exportURL)
                );
    }
    
    public static <T> T refer(Class<T> type, String url) throws RpcException {
        return refer(type, URL.valueOf(url));
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T refer(Class<T> type, URL url) throws RpcException {
        String referKey =  type.getName() + " -> " +  url.toFullString();
        if(services.containsKey(referKey)){
            return (T) services.get(referKey);
        }
        
        URL referURL = attachApplicationName(url)
                        .addParameter(Constants.INTERFACE_KEY, type.getName());
        
        if("discovery".equalsIgnoreCase(url.getProtocol())){
            return getAdaptiveInvokerProxy().getProxy(Distributions.refer(type, referURL));
        }
        T service = getAdaptiveInvokerProxy().getProxy(
                getProtocol(url).refer(type, referURL));
        
        services.putIfAbsent(referKey, service);
        return service;
    }
    
    /**
     * 
     * @param type RPC接口
     * @return 返回RPC接口的本地对象
     */
    public static <T> T refer(Class<T> type){
        return refer(null, type);
    }
    
    /**
     * 
     * @param name RPC接口的别名
     * @param type RPC接口
     * @return 返回RPC接口的本地对象
     */
    public static <T> T refer(String name, Class<T> type){
        KubboConfiguration configuration = KubboConfiguration.getInstance();
        if(! configuration.isConfigured()){
            PropertiesConfigurator.configure();
            if(! configuration.isConfigured()){
                throw new IllegalArgumentException("Kubbo not configured yet!");
            }
        }
        
        // 获取地址
        String address = configuration.getReferenceAddress(type.getName(), name);
        if(StringUtils.isBlank(address)){
            throw new IllegalArgumentException("Reference not found in configuration, interface: " + type.getName());
        }
        
        // 不同接口的不同实现
        URL url = URL.valueOf(address).addParameter(Constants.IMPLEMENTION_KEY, name);
        
        return refer(type, url);
    }
    
    /**
     * 销毁资源
     */
    public static void destroy(){
        synchronized (Kubbo.class) {
            services.clear();
            for(String key : protocols.keySet()){
                Protocol protocol = protocols.get(key);
                protocol.destroy();
                protocols.remove(key);
            }
        }
    }
    
    
    /**
     * 异步调用 ，需要返回值
     * @param callable rpc调用封装
     * @return 通过future.get()获取返回结果.
     * @exception RpcException rpc调用异常
     */
    public static <T> java.util.concurrent.Future<T> callAsync(Callable<T> callable) throws RpcException {
        try {
            try {
                RpcContext.get().setAttachment(Constants.ASYNC_KEY, Constants.TRUE);
                callable.call();
            } catch (Exception e) {
                throw new RpcException(e);
            } finally {
                RpcContext.get().removeAttachment(Constants.ASYNC_KEY);
            }
        } catch (final RpcException e) {
            return new ExceptionWrappedListenableFuture<T>(e);
        }
        return RpcContext.get().getFuture();
    }
    
    /**
     * 异步调用，只发送请求，不接收返回结果.
     * @param runable rpc调用封装
     * @exception RpcException rpc调用异常
     */
    public static void callAsync(Runnable runable) throws RpcException {
        try {
            RpcContext.get().setAttachment(Constants.ONEWAY_KEY, Constants.TRUE);
            runable.run();
        } catch (Throwable e) {
            throw new RpcException("callAsync runable error. " + e.getMessage(), e);
        } finally {
            RpcContext.get().removeAttachment(Constants.ONEWAY_KEY);
        }
    }
    
    /**
     * 监控
     * @param method 监控项名称, 可以使用方法名或者http路径 
     * @param time 本次耗时
     */
    public static void metrics(String method, long time){
        KubboMetrics.elapse(method, time);
    }
    
    /**
     * trace跟踪
     * @param operationName　本次调用的名称
     * @return 返回Trace对象,　用于关闭
     */
    public static Trace trace(String operationName){
        return KubboTrace.trace(operationName);
    }
    
    /**
     * 获取当前traceId, 用这个traceId可以把业务日志与trace系统相关联
     * @return traceId
     */
    public static String traceId(){
        return KubboTrace.traceId();
    }
    
    private static URL attachApplicationName(URL url){
        String application = SystemPropertyUtils.get(Constants.GLOBAL_APPLICATION_NAME, Constants.DEFAULT_APPLICATION_NAME);
        return url.addParameterIfAbsent(Constants.APPLICATION_KEY, application);
    }
    
    private static Protocol getProtocol(URL url) {
        String type = Protocols.getExtensionType(url);
        return getProtocol(type);
    }

    private static Protocol getProtocol(String type) {
        Protocol protocol = Protocols.getExtension(type);
        protocols.putIfAbsent(type, protocol);
        return protocol;
    }
    
    private static InvokerProxy getAdaptiveInvokerProxy() {
        return Extensions.getAdaptiveExtension(InvokerProxy.class);
    }
    
    private Kubbo(){}

}
