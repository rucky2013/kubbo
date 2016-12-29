package com.sogou.map.kubbo.remote.session;

/**
 * SessionClientDelegate. (API/SPI, Prototype, ThreadSafe)
 * 
 * @author liufuliang
 */
public interface SessionClientDelegate extends SessionClient {
    SessionClient getSessionClient();
}