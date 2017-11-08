package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.session.inner.InnerSessionLayer;

/**
 * SessionLayer.
 * @see <a href="https://en.wikipedia.org/wiki/Session_layer">Session_layer</a>
 * 
 * @author liufuliang
 */
@SPI(InnerSessionLayer.NAME)
public interface SessionLayer {

    /**
     * bind.
     * 
     * @param url
     * @param handler
     * @return message server
     */
    SessionServer bind(URL url, SessionHandler handler) throws RemoteException;

    /**
     * connect.
     * 
     * @param url
     * @param handler
     * @return message channel
     */
    SessionClient connect(URL url, SessionHandler handler) throws RemoteException;

}