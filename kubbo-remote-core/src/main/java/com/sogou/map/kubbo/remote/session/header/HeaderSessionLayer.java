package com.sogou.map.kubbo.remote.session.header;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.SessionClient;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.session.SessionServer;
import com.sogou.map.kubbo.remote.session.SessionLayer;
import com.sogou.map.kubbo.remote.session.handler.DecodeHandler;
import com.sogou.map.kubbo.remote.transport.TransportLayers;

/**
 * HeaderSessionLayer
 * 
 * @author liufuliang
 */
public class HeaderSessionLayer implements SessionLayer {
    
    public static final String NAME = "header";

    public SessionClient connect(URL url, SessionHandler handler) throws RemotingException {
        return new HeaderSessionClient(TransportLayers.connect(url, new DecodeHandler(new HeaderSessionHandler(handler))));
    }

    public SessionServer bind(URL url, SessionHandler handler) throws RemotingException {
        return new HeaderSessionServer(TransportLayers.bind(url, new DecodeHandler(new HeaderSessionHandler(handler))));
    }

}