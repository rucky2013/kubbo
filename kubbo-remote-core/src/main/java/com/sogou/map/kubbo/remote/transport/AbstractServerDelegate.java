package com.sogou.map.kubbo.remote.transport;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Server;
import com.sogou.map.kubbo.remote.ServerDelegate;

/**
 * AbstractServerDelegate
 * 
 * @author liufuliang
 */
public class AbstractServerDelegate extends AbstractEndpointDelegate implements ServerDelegate {
    
	protected transient Server server;

    public AbstractServerDelegate() {
    }

    public AbstractServerDelegate(Server server){
    	super(server);
        this.server = server;
    }

	@Override
	public boolean isBound() {
		return server.isBound();
	}

	@Override
	public Collection<Channel> getChannels() {
		return server.getChannels();
	}

	@Override
	public Channel getChannel(InetSocketAddress remoteAddress) {
		return server.getChannel(remoteAddress);
	}

	@Override
	public void reset(URL url) {
		server.reset(url);
	}

	@Override
	public Server getServer() {
		return server;
	}
 

}