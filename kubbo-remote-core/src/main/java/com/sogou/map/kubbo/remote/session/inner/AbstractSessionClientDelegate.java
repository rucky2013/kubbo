package com.sogou.map.kubbo.remote.session.inner;

import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.session.ResponseFuture;
import com.sogou.map.kubbo.remote.session.SessionClient;
import com.sogou.map.kubbo.remote.session.SessionClientDelegate;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.transport.AbstractClientDelegate;

/**
 * AbstractSessionClientDelegate
 * 
 * @author liufuliang
 */
public class AbstractSessionClientDelegate extends AbstractClientDelegate implements SessionClientDelegate {
    
    protected transient SessionClient client;

    public AbstractSessionClientDelegate() {
    }

    public AbstractSessionClientDelegate(SessionClient client){
        super(client);
        this.client = client;
    }
    
    @Override
    public SessionClient getSessionClient() {
        return client;
    }

    @Override
    public ResponseFuture request(Object request) throws RemoteException {
        return client.request(request);
    }

    @Override
    public ResponseFuture request(Object request, int timeout)
            throws RemoteException {
        return client.request(request, timeout);
    }

    @Override
    public SessionHandler getSessionHandler() {
        return client.getSessionHandler();
    }

}