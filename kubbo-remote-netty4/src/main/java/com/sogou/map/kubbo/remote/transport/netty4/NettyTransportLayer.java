package com.sogou.map.kubbo.remote.transport.netty4;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Client;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.Server;
import com.sogou.map.kubbo.remote.transport.TransportLayer;

/**
 * NettyTransportLayer
 * 
 * @author liufuliang
 */
public class NettyTransportLayer implements TransportLayer {

    public static final String NAME = "netty4";
    
    @Override
    public Server bind(URL url, ChannelHandler handler) throws RemoteException {
        return new NettyServer(url, handler);
    }

    @Override
    public Client connect(URL url, ChannelHandler handler) throws RemoteException {
        return new NettyClient(url, handler);
    }

}