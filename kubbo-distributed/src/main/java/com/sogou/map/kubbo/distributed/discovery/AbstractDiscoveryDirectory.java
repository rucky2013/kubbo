package com.sogou.map.kubbo.distributed.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.threadpool.impl.CachedThreadPool;
import com.sogou.map.kubbo.distributed.directory.AbstractDirectory;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.Protocols;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * AbstractDiscoveryDirectory
 * TODO 
 * 目前是一个接口对应一个发现目录, 当接口比较多时, 需要多个接口公用一个发现, 
 * 即将DiscoveryUpdateWatcher抽象出来, DiscoveryDirectory监听DiscoveryUpdateWatcher即可。
 * @author liufuliang
 */
public abstract class AbstractDiscoveryDirectory<T> extends AbstractDirectory<T> {

    private static final String DISCOVERY_POOL_NAME = "kubbo-discovery";
    
    protected volatile List<Invoker<T>> invokers;
    
    protected Executor workLoop = CachedThreadPool.getExecutor(2, 2, 3, TimeUnit.MINUTES, 
            new LinkedBlockingQueue<Runnable>(), DISCOVERY_POOL_NAME, true);
    
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
     */
    public void doUpdate(List<URL> urls){
//		if(urls.isEmpty()){
//			return;
//		}
        List<Invoker<T>> copyinvokers = invokers;
        //notify identity -> URL
        Map<String, URL> notifyAddresses = new HashMap<String, URL>();
        for(URL url : urls){
            notifyAddresses.put(url.toIdentityString(), url);
        }
        //current identity -> invoker
        Map<String, Invoker<T>> identityAndInvokers = new HashMap<String, Invoker<T>>();
        for(Invoker<T> invoker : copyinvokers){
            identityAndInvokers.put(invoker.getUrl().toIdentityString(), invoker);
        }
        //keep & del
        List<Invoker<T>> lastestInvokers = new ArrayList<Invoker<T>>();
        List<Invoker<T>> delInvokers = new ArrayList<Invoker<T>>();
        for(Map.Entry<String, Invoker<T>> entry :  identityAndInvokers.entrySet()){
            String identity = entry.getKey();
            Invoker<T> invoker = entry.getValue();
            if(notifyAddresses.containsKey(identity)){
                lastestInvokers.add(invoker);
            } else{
                delInvokers.add(invoker);
            }
        }
        //add
        for(Map.Entry<String, URL> entry :  notifyAddresses.entrySet()){
            String identity = entry.getKey();
            URL notifyAddress = entry.getValue();
            if(!identityAndInvokers.containsKey(identity)){
                URL invokeUrl = notifyAddress.addParametersIfAbsent(url.getParameters());
                Protocol protocol = Protocols.getExtension(invokeUrl);
                Invoker<T> invoker = protocol.refer(type, invokeUrl);
                lastestInvokers.add(invoker);
            }
        }
        invokers = lastestInvokers;
        //clean up
        for(Invoker<T> invoker : delInvokers){
            invoker.destroy();
        }
    }
    
    /**
     * async
     */
    public void update(final List<URL> urls) {
        workLoop.execute(new Runnable(){
            @Override
            public void run() {
                doUpdate(urls);				
            }
        });
    }
}