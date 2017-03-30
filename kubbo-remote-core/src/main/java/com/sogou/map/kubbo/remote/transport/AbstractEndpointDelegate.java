/**
 * 
 */
package com.sogou.map.kubbo.remote.transport;

import java.net.InetSocketAddress;

import com.sogou.map.kubbo.common.AbstractAttributableDelegate;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Endpoint;
import com.sogou.map.kubbo.remote.EndpointDelegate;
import com.sogou.map.kubbo.remote.RemotingException;

/**
 * AbstractEndpointDelegate
 * @author liufuliang
 *
 */
public class AbstractEndpointDelegate extends AbstractAttributableDelegate<Object> implements EndpointDelegate{
    protected transient Endpoint endpoint;
    
    public AbstractEndpointDelegate() {
    }

    public AbstractEndpointDelegate(Endpoint endpoint){
        super(endpoint);
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint == NULL");
        }
        this.endpoint = endpoint;
    }
    
    @Override
    public URL getUrl() {
        return endpoint.getUrl();
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return endpoint.getChannelHandler();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return endpoint.getLocalAddress();
    }

    @Override
    public void send(Object message) throws RemotingException {
        endpoint.send(message);
        
    }

    @Override
    public void send(Object message, boolean blocking) throws RemotingException {
        endpoint.send(message, blocking);		
    }

    @Override
    public void close() {
        endpoint.close();		
    }

    @Override
    public void close(int timeout) {
        endpoint.close(timeout);
        
    }

    @Override
    public boolean isClosed() {
        return endpoint.isClosed();
    }
}
