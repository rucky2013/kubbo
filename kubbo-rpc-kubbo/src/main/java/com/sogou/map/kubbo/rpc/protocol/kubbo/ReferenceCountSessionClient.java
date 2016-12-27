package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.util.concurrent.atomic.AtomicInteger;

import com.sogou.map.kubbo.remote.session.AbstractSessionClientDelegate;
import com.sogou.map.kubbo.remote.session.SessionClient;

/**
 * kubbo protocol support class.
 * 
 * @author liufuliang
 */
final class ReferenceCountSessionClient extends AbstractSessionClientDelegate implements SessionClient {    
        
    private final AtomicInteger refenceCount = new AtomicInteger(0);    
    
    public ReferenceCountSessionClient(SessionClient client) {
        super(client);
        refenceCount.incrementAndGet();
    }

    public void incrementAndGetCount(){
        refenceCount.incrementAndGet();
    }
    
    /* 
     * close方法将不再幂等,调用需要注意.
     */
    @Override
    public void close() {
        close(0);
    }

    @Override
    public void close(int timeout) {
        if (refenceCount.decrementAndGet() <= 0){
            if (timeout == 0){
                client.close();
            } else {
                client.close(timeout);
            }
        }
    }

}