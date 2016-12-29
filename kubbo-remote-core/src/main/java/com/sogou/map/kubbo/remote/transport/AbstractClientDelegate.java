package com.sogou.map.kubbo.remote.transport;

import java.net.InetSocketAddress;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.Client;
import com.sogou.map.kubbo.remote.ClientDelegate;
import com.sogou.map.kubbo.remote.RemotingException;

/**
 * AbstractClientDelegate
 * 
 * @author liufuliang
 */
public class AbstractClientDelegate extends AbstractEndpointDelegate implements ClientDelegate {
    
    protected transient Client client;

    public AbstractClientDelegate() {
    }

    public AbstractClientDelegate(Client client){
        super(client);
        this.client = client;
    }
    
    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void reset(URL url) {
        client.reset(url);
    }
    
    @Override
    public void reconnect() throws RemotingException {
        client.reconnect();
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return client.getRemoteAddress();
    }
}