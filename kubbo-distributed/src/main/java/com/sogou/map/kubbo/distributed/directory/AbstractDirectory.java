package com.sogou.map.kubbo.distributed.directory;

import java.util.List;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.distributed.Directory;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * AbstractDirectory
 * @author liufuliang
 */
public abstract class AbstractDirectory<T> implements Directory<T> {

	protected final URL url;
	protected Class<T> type;
    private volatile boolean destroyed = false;
    
    public AbstractDirectory(Class<T> type, URL url) {
        if (url == null)
            throw new IllegalArgumentException("url == NULL");
        if(type == null){
            throw new IllegalArgumentException("type == NULL");
        }
        this.url = url;
        this.type = type;
    }
    
    @Override
	public Class<T> getInterface(){
    	return type;
    }
    
    @Override
    public URL getUrl() {
        return url;
    }
    
    @Override
    public void destroy(){
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
    
    @Override
    public List<Invoker<T>> list(Invocation invocation) throws RpcException {
        if (destroyed){
            throw new RpcException("Directory already destroyed. url: " + getUrl());
        }
        List<Invoker<T>> invokers = doList(invocation);
        return invokers;
    }
    

    protected abstract List<Invoker<T>> doList(Invocation invocation) throws RpcException;

}