package com.sogou.map.kubbo.remote.session.inner;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.session.SessionClient;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.session.SessionServer;
import com.sogou.map.kubbo.remote.session.SessionLayer;
import com.sogou.map.kubbo.remote.session.handler.DecodeHandler;
import com.sogou.map.kubbo.remote.transport.TransportLayers;

/**
 * InnerSessionLayer
 * 
 * @author liufuliang
 */
public class InnerSessionLayer implements SessionLayer {
    
    public static final String NAME = "inner";

    @Override
    public SessionClient connect(URL url, SessionHandler handler) throws RemoteException {
        return new InnerSessionClient(TransportLayers.connect(url, new DecodeHandler(new InnerSessionHandler(handler))));
    }

    @Override
    public SessionServer bind(URL url, SessionHandler handler) throws RemoteException {
        return new InnerSessionServer(TransportLayers.bind(url, new DecodeHandler(new InnerSessionHandler(handler))));
    }

}