/**
 * 
 */
package com.sogou.map.kubbo.distributed.protocol;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.distributed.Directory;
import com.sogou.map.kubbo.distributed.Discovery;
import com.sogou.map.kubbo.distributed.Replication;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.protocol.AbstractProtocol;

/**
 * @author liufuliang
 *
 */
public class DiscoveryProtocol extends AbstractProtocol {
    public static final String NAME = "discovery";
    
    public static final int DEFAULT_PORT = 40660;
	
    protected Protocol protocol;

    
	@Override
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}

	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		if(protocol == null){
			protocol = getAdaptiveProtocol();
		}
		return protocol.export(invoker);
	}

	@Override
	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {		
		//discovery
		Discovery discovery = getAdaptiveDiscovery();
		Directory<T> directory = discovery.subscribe(type, url);
		// replication
		Replication replica = getAdaptiveReplication();
		return replica.join(directory);
	}
	
	@Override
	public void destroy() {
		if(protocol != null){
			protocol.destroy();
		}
		super.destroy();
	}
	
	private Replication getAdaptiveReplication() {
        return ExtensionLoader.getExtensionLoader(Replication.class).getAdaptiveExtension();
    }
	
	private Protocol getAdaptiveProtocol() {
		return ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
	}
	
	private Discovery getAdaptiveDiscovery() {
		return ExtensionLoader.getExtensionLoader(Discovery.class).getAdaptiveExtension();
	}
}
