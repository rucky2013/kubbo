package com.sogou.map.kubbo.distributed.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.common.threadpool.impl.CachedThreadPool;
import com.sogou.map.kubbo.distributed.directory.AbstractDirectory;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * AbstractDiscoveryDirectory
 * TODO 
 * 目前是一个接口对应一个发现目录, 当接口比较多时, 需要多个接口公用一个发现, 
 * 即将DiscoveryUpdateWatcher抽象出来, DiscoveryDirectory监听DiscoveryUpdateWatcher即可。
 * @author liufuliang
 */
public abstract class AbstractDiscoveryDirectory<T> extends AbstractDirectory<T> {
    protected volatile List<Invoker<T>> invokers;
    
    protected Executor workLoop = CachedThreadPool.getExecutor(2, 2, 3, TimeUnit.MINUTES, 
            new LinkedBlockingQueue<Runnable>(), "Kubbo-discovery", true);
    
    protected AbstractDiscoveryDirectory(Class<T> type, URL url) {
        super(type, url);
        invokers = new ArrayList<Invoker<T>>();
    }

    @Override
    public boolean isAvailable() {
        if (isDestroyed()) {
            return false;
        }
        for(Invoker<T> invoker : new ArrayList<Invoker<T>>(invokers)){
            if(invoker.isAvailable()){
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<Invoker<T>> doList(Invocation invocation) throws RpcException {
        return invokers;
    }


    /*
     * 以watch的方式阻塞获取, 首次获取非阻塞
     */
    protected abstract List<URL> fetch();
    
    /**
     * watch loop
     * @return
     */
    public void watchUpdate(){
        workLoop.execute(new Runnable(){
            @Override
            public void run() {
                while(!isDestroyed()){
                    List<URL> urls = fetch();
                    update(urls);
                }
                
            }
        });
    }
    
    public void synchronize(){
        List<URL> urls = fetch();
        doUpdate(urls);
    }
    /**
     * 
     * @param urls
     */
    public void doUpdate(List<URL> urls){
//		if(urls.isEmpty()){
//			return;
//		}
        List<Invoker<T>> copyinvokers = invokers;
        //notify addresses
        Set<String> notifyAddresses = new HashSet<String>();
        for(URL url : urls){
            notifyAddresses.add(url.getAddress());
        }
        //address -> invoker
        Map<String, Invoker<T>> addressAndInvokers = new HashMap<String, Invoker<T>>();
        for(Invoker<T> invoker : copyinvokers){
            addressAndInvokers.put(invoker.getUrl().getAddress(), invoker);
        }
        //keep & del
        List<Invoker<T>> newinvokers = new ArrayList<Invoker<T>>();
        List<Invoker<T>> delinvokers = new ArrayList<Invoker<T>>();
        for(Map.Entry<String, Invoker<T>> entry :  addressAndInvokers.entrySet()){
            if(notifyAddresses.contains(entry.getKey())){
                newinvokers.add(entry.getValue());
            } else{
                delinvokers.add(entry.getValue());
            }
        }
        //add
        for(String address : notifyAddresses){
            if(!addressAndInvokers.containsKey(address)){
                URL invokeUrl = url.setAddress(address);
                Protocol protocol = getProtocol(invokeUrl);
                Invoker<T> invoker = protocol.refer(type, invokeUrl);
                newinvokers.add(invoker);
            }
        }	
        invokers = newinvokers;
        //clean up
        for(Invoker<T> invoker : delinvokers){
            invoker.destroy();
        }
    }
    
    /**
     * async
     * @param urls
     */
    public void update(final List<URL> urls) {
        workLoop.execute(new Runnable(){
            @Override
            public void run() {
                doUpdate(urls);				
            }
        });
    }
    
    private static Protocol getProtocol(URL url) {
        String type = url.getParameter(Constants.PROTOCOL_KEY, Constants.DEFAULT_PROTOCOL);
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(type);
        return protocol;
    }


}