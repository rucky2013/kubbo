/**
 * 
 */package com.sogou.map.kubbo.boot;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sogou.map.kubbo.boot.configuration.KubboConfiguration;
import com.sogou.map.kubbo.boot.configuration.PropertiesConfigurator;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.common.utils.StringUtils;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.InvokerProxy;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.RpcContext;
import com.sogou.map.kubbo.rpc.RpcException;

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
        return getProtocol(url).export(
                getAdaptiveInvokerProxy().getInvoker(service, type, url)
                );
    }
    public static Exporter<?> exportGeneric(Object service, Class<?> type, URL url) throws RpcException {
        return getProtocol(url).export(
                getAdaptiveInvokerProxy().getGenericInvoker(service, type, url)
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
        
        if("discovery".equalsIgnoreCase(url.getProtocol())){
            return getAdaptiveInvokerProxy().getProxy(
                    getProtocol("discovery").refer(type, url));
        }
        T service = getAdaptiveInvokerProxy().getProxy(
                getProtocol(url).refer(type, url));
        
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
        
        String address = configuration.getReferenceAddress(type.getName(), name);
        if(StringUtils.isBlank(address)){
            throw new IllegalArgumentException("Not found interface implements for " + type.getName());
        }
        return refer(type, address);
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
    @SuppressWarnings("unchecked")
    public static <T> Future<T> callAsync(Callable<T> callable) throws RpcException {
        try {
            try {
                RpcContext.get().setAttachment(Constants.ASYNC_KEY, Constants.TRUE);
                final T o = callable.call();
                //local调用会直接返回结果.
                if (o != null) {
                    FutureTask<T> f = new FutureTask<T>(new Callable<T>() {
                        public T call() throws Exception {
                            return o;
                        }
                    });
                    f.run();
                    return f;
                } else {
                    
                }
            } catch (Exception e) {
                throw new RpcException(e);
            } finally {
                RpcContext.get().removeAttachment(Constants.ASYNC_KEY);
            }
        } catch (final RpcException e) {
            return new Future<T>() {
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }
                public boolean isCancelled() {
                    return false;
                }
                public boolean isDone() {
                    return true;
                }
                public T get() throws InterruptedException, ExecutionException {
                    throw new ExecutionException(e.getCause());
                }
                public T get(long timeout, TimeUnit unit)
                        throws InterruptedException, ExecutionException,
                        TimeoutException {
                    return get();
                }
            };
        }
        return ((Future<T>)RpcContext.get().getFuture());
    }
    
    /**
     * 异步调用，只发送请求，不接收返回结果.
     * @param runable rpc调用封装
     * @exception RpcException rpc调用异常
     */
    public static void callAsync(Runnable runable) throws RpcException {
        try {
            RpcContext.get().setAttachment(Constants.RETURN_KEY, Constants.FALSE);
            runable.run();
        } catch (Throwable e) {
            throw new RpcException("callAsync runable error. " + e.getMessage(), e);
        } finally {
            RpcContext.get().removeAttachment(Constants.RETURN_KEY);
        }
    }
    
    private static Protocol getProtocol(URL url) {
        String type = url.getParameter(Constants.PROTOCOL_KEY, Constants.DEFAULT_PROTOCOL);
        return getProtocol(type);
    }

    private static Protocol getProtocol(String type) {
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(type);
        protocols.putIfAbsent(type, protocol);
        return protocol;
    }

    
    private static InvokerProxy getAdaptiveInvokerProxy() {
        return ExtensionLoader.getExtensionLoader(InvokerProxy.class).getAdaptiveExtension();
    }
    
    private Kubbo(){}

}
