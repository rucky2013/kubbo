package com.sogou.map.kubbo.remote.session;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.header.HeaderSessionLayer;

/**
 * SessionLayer. (SPI, Singleton, ThreadSafe)
 * @see <a href="https://en.wikipedia.org/wiki/Session_layer">Session_layer</a>
 * 
 * @author liufuliang
 */
@SPI(HeaderSessionLayer.NAME)
public interface SessionLayer {

    /**
     * bind.
     * 
     * @param url
     * @param handler
     * @return message server
     */
    @Adaptive({Constants.SESSIONLAYER_KEY})
    SessionServer bind(URL url, SessionHandler handler) throws RemotingException;

    /**
     * connect.
     * 
     * @param url
     * @param handler
     * @return message channel
     */
    @Adaptive({Constants.SESSIONLAYER_KEY})
    SessionClient connect(URL url, SessionHandler handler) throws RemotingException;

}