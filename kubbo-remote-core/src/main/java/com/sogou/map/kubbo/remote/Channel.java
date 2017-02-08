package com.sogou.map.kubbo.remote;

import java.net.InetSocketAddress;

/**
 * Channel.
 *
 * @author liufuliang
 */
public interface Channel extends Endpoint {

    /**
     * get remote address.
     * 
     * @return remote address.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * is connected.
     * 
     * @return connected
     */
    boolean isConnected();

}