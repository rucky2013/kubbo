package com.sogou.map.kubbo.remote;


/**
 * ChannelHandler.
 * 
 * @author liufuliang
 */
public interface ChannelHandler {

    /**
     * on channel connected.
     * 
     * @param channel channel.
     */
    void onConnected(Channel channel) throws RemoteException;

    /**
     * on channel disconnected.
     * 
     * @param channel channel.
     */
    void onDisconnected(Channel channel) throws RemoteException;

    /**
     * on message sent.
     * 
     * @param channel channel.
     * @param message message.
     */
    void onSent(Channel channel, Object message) throws RemoteException;

    /**
     * on message received.
     * 
     * @param channel channel.
     * @param message message.
     */
    void onReceived(Channel channel, Object message) throws RemoteException;

    /**
     * on exception caught.
     * 
     * @param channel channel.
     * @param exception exception.
     */
    void onExceptonCaught(Channel channel, Throwable exception) throws RemoteException;

}