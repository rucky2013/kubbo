package com.sogou.map.kubbo.rpc.protocol;

import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Interceptor;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * InterceptorWrappedProtocol
 * 
 * @author liufuliang
 */
public class InterceptorWrappedProtocol extends AbstractProtocolDelegate {

    public InterceptorWrappedProtocol(Protocol protocol){
        super(protocol);
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return protocol.export(wrapInvoker(invoker, Constants.INTERCEPTOR_KEY, Constants.PROVIDER));
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return wrapInvoker(protocol.refer(type, url), Constants.INTERCEPTOR_KEY, Constants.CONSUMER);
    }

    private static <T> Invoker<T> wrapInvoker(final Invoker<T> invoker, String interceptorKey, String group) {
        Invoker<T> last = invoker;
        List<Interceptor> interceptors = getInterceptors(invoker.getUrl(), interceptorKey, group);
        
        if (interceptors.size() > 0) {
            for (int i = interceptors.size() - 1; i >= 0; i --) {
                final Interceptor interceptor = interceptors.get(i);
                final Invoker<T> next = last;
                last = new AbstractInvokerDelegate<T>(invoker) {
                    @Override
                    public Result invoke(Invocation invocation) throws RpcException {
                        return interceptor.invoke(next, invocation);
                    }
                };
            }
        }
        return last;
    }
    
    private static List<Interceptor> getInterceptors(URL url, String interceptorKey, String group){
        return Extensions.getActivateExtension(url, interceptorKey, group, Interceptor.class);
    }
    
}