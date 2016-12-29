package com.sogou.map.kubbo.remote;

/**
 * Remoting Client. (API/SPI, Prototype, ThreadSafe)
 * 
 * @author liufuliang
 */
public interface ClientDelegate extends Client{

    /**
     * reconnect.
     */
    Client getClient();

}