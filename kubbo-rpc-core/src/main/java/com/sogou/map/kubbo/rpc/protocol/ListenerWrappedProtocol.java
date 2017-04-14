package com.sogou.map.kubbo.rpc.protocol;

import java.util.Collections;
import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.ExporterListener;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.InvokerListener;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * ListenerProtocol
 * 
 * @author liufuliang
 */
public class ListenerWrappedProtocol extends AbstractProtocolDelegate {

    public ListenerWrappedProtocol(Protocol protocol){
        super(protocol);
    }
    
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return new ListenerWrappedExporter<T>(protocol.export(invoker), getExporterListeners(invoker.getUrl()));
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return new ListenerWrappedInvoker<T>(protocol.refer(type, url), getInvokerListeners(url));
    }
    
    private List<ExporterListener> getExporterListeners(URL url){
        return Collections.unmodifiableList(Extensions.getActivateExtension(url, Constants.EXPORTER_LISTENER_KEY, ExporterListener.class));
    }
    private List<InvokerListener> getInvokerListeners(URL url){
        return Collections.unmodifiableList(Extensions.getActivateExtension(url, Constants.INVOKER_LISTENER_KEY, InvokerListener.class));
    }
}