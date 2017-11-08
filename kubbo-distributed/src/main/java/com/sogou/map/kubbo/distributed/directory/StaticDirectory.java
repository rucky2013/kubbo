package com.sogou.map.kubbo.distributed.directory;

import java.util.List;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * StaticDirectory
 * 
 * @author liufuliang
 */
public class StaticDirectory<T> extends AbstractDirectory<T> {
    
    private final List<Invoker<T>> invokers;
    
    public StaticDirectory(List<Invoker<T>> invokers){
        this(null, null, invokers);
    }

    public StaticDirectory(Class<T> type, URL url, List<Invoker<T>> invokers) {
        super(//type
            (type == null && invokers != null && invokers.size() > 0) ?
                invokers.get(0).getInterface() : type, 
            //url
            (url == null && invokers != null && invokers.size() > 0) ?
                invokers.get(0).getUrl() : url);
        if (invokers == null || invokers.size() == 0)
            throw new IllegalArgumentException("invokers == NULL");
        this.invokers = invokers;
    }

    @Override
    public boolean isAvailable() {
        if (isDestroyed()) {
            return false;
        }
        for (Invoker<T> invoker : invokers) {
            if (invoker.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        if(isDestroyed()) {
            return;
        }
        super.destroy();
        for (Invoker<T> invoker : invokers) {
            invoker.destroy();
        }
        invokers.clear();
    }
    
    @Override
    protected List<Invoker<T>> doList(Invocation invocation) throws RpcException {
        return invokers;
    }

}