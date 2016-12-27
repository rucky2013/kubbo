package com.sogou.map.kubbo.remote.session;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.sogou.map.kubbo.remote.Server;

/**
 * SessionServer. (API/SPI, Prototype, ThreadSafe)
 * 
 * @author liufuliang
 */
public interface SessionServer extends Server {

    /**
     * get channels.
     * 
     * @return channels
     */
    Collection<SessionChannel> getSessionChannels();

    /**
     * get channel.
     * 
     * @param remoteAddress
     * @return channel
     */
    SessionChannel getSessionChannel(InetSocketAddress remoteAddress);

}