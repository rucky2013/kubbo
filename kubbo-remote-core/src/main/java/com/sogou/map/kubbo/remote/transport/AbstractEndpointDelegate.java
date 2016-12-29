/**
 * 
 */
package com.sogou.map.kubbo.remote.transport;

import java.net.InetSocketAddress;

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
public class AbstractEndpointDelegate implements EndpointDelegate{
    protected transient Endpoint endpoint;
    
    public AbstractEndpointDelegate() {
    }

    public AbstractEndpointDelegate(Endpoint endpoint){
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

    @Override
    public boolean hasAttribute(String key) {
        return endpoint.hasAttribute(key);
    }

    @Override
    public Object getAttribute(String key) {
        return endpoint.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        endpoint.setAttribute(key, value);
        
    }

    @Override
    public void removeAttribute(String key) {
        endpoint.removeAttribute(key);		
    }

    @Override
    public void removeAttributes() {
        endpoint.removeAttributes();		
    }

}
