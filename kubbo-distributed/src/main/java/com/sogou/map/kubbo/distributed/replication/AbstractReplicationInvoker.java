package com.sogou.map.kubbo.distributed.replication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.NetUtils;
import com.sogou.map.kubbo.distributed.Directory;
import com.sogou.map.kubbo.distributed.LoadBalance;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.utils.Rpcs;


/**
 * AbstractClusterInvoker
 * 
 * @author liufuliang
 */
public abstract class AbstractReplicationInvoker<T> implements Invoker<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractReplicationInvoker.class);
    
    protected final Directory<T> directory;

    private volatile boolean destroyed = false;

    public AbstractReplicationInvoker(Directory<T> directory) {
        this(directory, directory.getUrl());
    }
    
    public AbstractReplicationInvoker(Directory<T> directory, URL url) {
        if (directory == null)
            throw new IllegalArgumentException("service directory == NULL");
        
        this.directory = directory ;
    }

    @Override
    public Class<T> getInterface() {
        return directory.getInterface();
    }

    @Override
    public URL getUrl() {
        return directory.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return directory.isAvailable();
    }

    @Override
    public void destroy() {
        directory.destroy();
        destroyed = true;
    }
    
    @Override
    public String toString() {
        return getInterface() + " -> " + getUrl().toString();
    }
    
    @Override
    public Result invoke(final Invocation invocation) throws RpcException {
        //invokers
        List<Invoker<T>> invokers = list(invocation);

        //loadbalance
        LoadBalance loadbalance = (invokers != null && invokers.size() > 0) ? 
                getLoadBalance(invokers.get(0).getUrl(), invocation.getMethodName()) : getDefaultLoadBalance();
        //doInvoke
        return doInvoke(invocation, invokers, loadbalance);
    }

    

    /**
     * 
     * @param skipped 已选过的invoker.注意：输入保证不重复
     * 
     */
    protected Invoker<T> select(
            Invocation invocation,
            List<Invoker<T>> invokers, 
            List<Invoker<T>> skipped, 
            LoadBalance loadbalance) throws RpcException {
        //保护
        if (isInvokersEmpty(invokers))
            return null;
        if(skipped == null){
            skipped = Collections.emptyList();
        }
        
        //快速返回
        if (invokers.size() == 1)
            return invokers.get(0);
        if (invokers.size() == 2 && skipped.size() > 0) {
            return skipped.contains(invokers.get(0)) ? invokers.get(1) : invokers.get(0);
        }
        
        //跳过过滤集
        List<Invoker<T>> theInvokers = new ArrayList<Invoker<T>>(invokers);
        theInvokers.removeAll(skipped);
        
        //负载均衡
        Invoker<T> invoker = loadbalance.select(theInvokers, getUrl(), invocation);
        
        //重试.
        if( !invoker.isAvailable() ){
            Invoker<T> retryInvoker = loadbalance.select(invokers, getUrl(), invocation);
            if(retryInvoker == null){
                int index = invokers.indexOf(invoker);
                try{
                    invoker = index < invokers.size() - 1 ? invokers.get(index + 1) : invoker;
                } catch (Exception e) {
                    logger.warn(e.getMessage()+" may because invokers list dynamic change, ignore.",e);
                }
            }
        }
        return invoker;
    }
    
    /**
     * 尽最大努力重新选择
     * @param invocation
     * @param invokers
     * @param skipped
     * @param loadbalance
     * @return
     * @throws RpcException
     */
    protected Invoker<T> selectRetry(    		
            Invocation invocation,
            List<Invoker<T>> invokers, 
            List<Invoker<T>> skipped, 
            LoadBalance loadbalance) throws RpcException{
        List<Invoker<T>> theInvokers = new ArrayList<Invoker<T>>(invokers);
        //Available, Not Skipped
        for(Invoker<T> ink : invokers){
            if(ink.isAvailable() && !skipped.contains(ink)){
                theInvokers.add(ink);
            }
        }
        //Available, Skipped
        if(theInvokers.isEmpty()){
            for(Invoker<T> ink : skipped){
                if(ink.isAvailable()){
                    theInvokers.add(ink);
                }
            }
        }
        if(theInvokers.isEmpty()){
            return null;
        }
        
        Invoker<T> invoker = loadbalance.select(theInvokers, getUrl(), invocation);
        return invoker;
    }
    
    protected boolean isInvokersEmpty(List<Invoker<T>> invokers){
        return invokers == null || invokers.size() == 0;
    }
    
    protected  List<Invoker<T>> list(Invocation invocation) throws RpcException {
        assertNotDestroyed();
        List<Invoker<T>> invokers = directory.list(invocation);
        assertInvokersNotEmpty(invokers, invocation);
        return invokers;
    }

    private void assertNotDestroyed() {
        if(destroyed){
            throw new RpcException("Rpc Replication invoker for " + getInterface() + " on consumer " + NetUtils.getLocalHost()
                    + " is now destroyed! Can not invoke any more.");
        }
    }
    
    private void assertInvokersNotEmpty(List<Invoker<T>> invokers, Invocation invocation) {
        if (isInvokersEmpty(invokers)) {
            throw new RpcException("Failed to invoke the method "
                    + invocation.getMethodName() + " in the service " + getInterface().getName() 
                    + ". No provider available for the service " + Rpcs.serviceKey(directory.getUrl())
                    + " from discovery " + directory.getUrl().getAddress() 
                    + " on the consumer " + NetUtils.getLocalHost()
                    + ". Please check if the providers have been started and registered.");
        }
    }
    
    private LoadBalance getLoadBalance(URL url, String methodName){
        LoadBalance loadbalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(
                url.getMethodParameter(methodName, Constants.LOADBALANCE_KEY, Constants.DEFAULT_LOADBALANCE));
        return loadbalance;
    }
    
    private LoadBalance getDefaultLoadBalance(){
        LoadBalance loadbalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(Constants.DEFAULT_LOADBALANCE);
        return loadbalance;
    }
    
    protected abstract Result doInvoke(Invocation invocation, List<Invoker<T>> invokers,
                                       LoadBalance loadbalance) throws RpcException;
    

}