package com.sogou.map.kubbo.remote;

import java.net.InetSocketAddress;

import com.sogou.map.kubbo.common.Attributable;
import com.sogou.map.kubbo.common.URL;

/**
 * Endpoint. (API/SPI, Prototype, ThreadSafe)
 * @author liufuliang
 */
public interface Endpoint extends Attributable{
    /**
     * get url.
     * 
     * @return url.
     */
    URL getUrl();
    
    /**
     * get channel handler.
     * 
     * @return channel handler
     */
    ChannelHandler getChannelHandler();

    /**
     * get local address.
     * 
     * @return local address.
     */
    InetSocketAddress getLocalAddress();
    
    /**
     * send message.
     * 
     * @param message
     * @throws RemotingException
     */
    void send(Object message) throws RemotingException;

    /**
     * send message.
     * 
     * @param message
     * @param blocking 是否阻塞等待发送完毕
     */
    void send(Object message, boolean blocking) throws RemotingException;

    /**
     * close the channel.
     */
    void close();
    
    /**
     * Graceful close the channel.
     */
    void close(int timeout);
    
    /**
     * is closed.
     * 
     * @return closed
     */
    boolean isClosed();

}