/**
 * 
 */package com.sogou.map.kubbo.rpc.protocol;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.ProtocolDelegate;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * @author liufuliang
 *
 */
public class AbstractProtocolDelegate implements ProtocolDelegate{
    protected final Protocol protocol;

	public AbstractProtocolDelegate(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("protocol == NULL");
        }
		this.protocol = protocol;
	}

	@Override
	public int getDefaultPort() {
		return protocol.getDefaultPort();
	}

	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		return protocol.export(invoker);
	}

	@Override
	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		return protocol.refer(type, url);
	}

	@Override
	public void destroy() {
		protocol.destroy();
	}

	@Override
	public Protocol getProtocol() {
		return protocol;
	}

}
