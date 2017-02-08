package com.sogou.map.kubbo.remote;

/**
 * Remoting Client.
 * 
 * <a href="http://en.wikipedia.org/wiki/Client%E2%80%93server_model">Client/Server</a>
 * 
 * @author liufuliang
 */
public interface Client extends Endpoint, Channel, Resetable{

    /**
     * reconnect.
     */
    void reconnect() throws RemotingException;

}