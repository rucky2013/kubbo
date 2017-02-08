package com.sogou.map.kubbo.rpc.protocol;

import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Filter;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * FilterWrappedProtocol
 * 
 * @author liufuliang
 */
public class FilterWrappedProtocol extends AbstractProtocolDelegate {

    public FilterWrappedProtocol(Protocol protocol){
        super(protocol);
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return protocol.export(wrapInvoker(invoker, Constants.FILTER_KEY, Constants.PROVIDER));
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return wrapInvoker(protocol.refer(type, url), Constants.FILTER_KEY, Constants.CONSUMER);
    }

    private static <T> Invoker<T> wrapInvoker(final Invoker<T> invoker, String filterKey, String group) {
        Invoker<T> last = invoker;
        List<Filter> filters = getFilters(invoker.getUrl(), filterKey, group);
        
        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i --) {
                final Filter filter = filters.get(i);
                final Invoker<T> next = last;
                last = new AbstractInvokerDelegate<T>(invoker) {
                    @Override
                    public Result invoke(Invocation invocation) throws RpcException {
                        return filter.invoke(next, invocation);
                    }
                };
            }
        }
        return last;
    }
    
    private static List<Filter> getFilters(URL url, String filterKey, String group){
        return ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(url, filterKey, group);
    }
    
}