package com.sogou.map.kubbo.remote.transport;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Client;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.Server;
import com.sogou.map.kubbo.remote.transport.handler.ChannelHandlerGroup;
import com.sogou.map.kubbo.remote.transport.handler.NoOpChannelHandler;

/**
 * TransportLayer facade. (API, Static, ThreadSafe)
 * 
 * @author liufuliang
 */
public class TransportLayers {

    public static Server bind(String url, ChannelHandler... handler) throws RemotingException {
        return bind(URL.valueOf(url), handler);
    }

    public static Server bind(URL url, ChannelHandler... handlers) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        if (handlers == null || handlers.length == 0) {
            throw new IllegalArgumentException("handlers == NULL");
        }
        ChannelHandler handler;
        if (handlers.length == 1) {
            handler = handlers[0];
        } else {
            handler = new ChannelHandlerGroup(handlers);
        }
        return getTransportLayer().bind(url, handler);
    }

    public static Client connect(String url, ChannelHandler... handler) throws RemotingException {
        return connect(URL.valueOf(url), handler);
    }

    public static Client connect(URL url, ChannelHandler... handlers) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        ChannelHandler handler;
        if (handlers == null || handlers.length == 0) {
            handler = new NoOpChannelHandler();
        } else if (handlers.length == 1) {
            handler = handlers[0];
        } else {
            handler = new ChannelHandlerGroup(handlers);
        }
        return getTransportLayer().connect(url, handler);
    }

    public static TransportLayer getTransportLayer() {
        return Extensions.getAdaptiveExtension(TransportLayer.class);
    }

    private TransportLayers(){
    }

}