package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.session.codec.SessionCodec;
import com.sogou.map.kubbo.remote.session.handler.NoOpSessionHandler;

/**
 * SessionLayers
 * 
 * @author liufuliang
 */
public class SessionLayers {
    
    private SessionLayers() { }
    
    public static SessionServer bind(String url, SessionHandler handler) throws RemoteException {
        return bind(URL.valueOf(url), handler);
    }

    public static SessionServer bind(URL url, SessionHandler handler) throws RemoteException {
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler == NULL");
        }
        url = url.addParameterIfAbsent(Constants.CODEC_KEY, SessionCodec.NAME);
        return getSessionLayer(url).bind(url, handler);
    }
    
    public static SessionClient connect(String url) throws RemoteException {
        return connect(URL.valueOf(url), new NoOpSessionHandler());
    }
    
    public static SessionClient connect(URL url) throws RemoteException {
        return connect(url, new NoOpSessionHandler());
    }
    
    public static SessionClient connect(String url, SessionHandler handler) throws RemoteException {
        return connect(URL.valueOf(url), handler);
    }

    public static SessionClient connect(URL url, SessionHandler handler) throws RemoteException {
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler == NULL");
        }
        url = url.addParameterIfAbsent(Constants.CODEC_KEY, SessionCodec.NAME);
        return getSessionLayer(url).connect(url, handler);
    }

    public static SessionLayer getSessionLayer(URL url) {
        return Extensions.getExtension(url, Constants.SESSIONLAYER_KEY, SessionLayer.class);
    }
}