package com.sogou.map.kubbo.remote;

/**
 * Remoting Client.
 * 
 * @author liufuliang
 */
public interface Client extends Endpoint, Channel, Resetable{

    /**
     * reconnect.
     */
    void reconnect() throws RemoteException;

}