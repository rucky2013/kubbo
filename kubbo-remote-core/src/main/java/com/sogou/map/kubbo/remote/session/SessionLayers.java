package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.codec.SessionCodec;
import com.sogou.map.kubbo.remote.session.handler.NoOpSessionHandler;

/**
 * SessionLayer facade. (API, Static, ThreadSafe)
 * 
 * @author liufuliang
 */
public class SessionLayers {
    private SessionLayers(){
    }
    
    public static SessionServer bind(String url, SessionHandler handler) throws RemotingException {
        return bind(URL.valueOf(url), handler);
    }

    public static SessionServer bind(URL url, SessionHandler handler) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler == NULL");
        }
        url = url.addParameterIfAbsent(Constants.CODEC_KEY, SessionCodec.NAME);
        return getSessionLayer(url).bind(url, handler);
    }
    
    public static SessionClient connect(String url) throws RemotingException {
        return connect(URL.valueOf(url), new NoOpSessionHandler());
    }
    
    public static SessionClient connect(URL url) throws RemotingException {
        return connect(url, new NoOpSessionHandler());
    }
    
    public static SessionClient connect(String url, SessionHandler handler) throws RemotingException {
        return connect(URL.valueOf(url), handler);
    }

    public static SessionClient connect(URL url, SessionHandler handler) throws RemotingException {
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