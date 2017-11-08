package com.sogou.map.kubbo.remote.transport;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.Client;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.Server;

/**
 * transportlayer. (SPI, Singleton, ThreadSafe)
 * @see <a href="https://en.wikipedia.org/wiki/Transport_layer">Transport_layer</a>
 * @author liufuliang
 */
@SPI("netty4")
public interface TransportLayer {

    /**
     * Bind a server.
     * 
     * @param url server url
     * @param handler
     * @return server
     * @throws RemoteException 
     */
    @Adaptive(Constants.TRANSPORTLAYER_KEY)
    Server bind(URL url, ChannelHandler handler) throws RemoteException;

    /**
     * Connect to a server.
     * 
     * @param url server url
     * @param handler
     * @return client
     * @throws RemoteException 
     */
    @Adaptive(Constants.TRANSPORTLAYER_KEY)
    Client connect(URL url, ChannelHandler handler) throws RemoteException;

}