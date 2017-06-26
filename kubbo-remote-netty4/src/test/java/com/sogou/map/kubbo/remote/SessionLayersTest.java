/**
 * 
 */package com.sogou.map.kubbo.remote;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.Serializable;

import com.sogou.map.kubbo.remote.session.SessionChannel;
import com.sogou.map.kubbo.remote.session.SessionClient;
import com.sogou.map.kubbo.remote.session.SessionServer;
import com.sogou.map.kubbo.remote.session.SessionLayers;
import com.sogou.map.kubbo.remote.session.ResponseFuture;
import com.sogou.map.kubbo.remote.session.handler.SessionHandlerAdapter;

/**
 * @author liufuliang
 *
 */
public class SessionLayersTest {
    static class Message implements Serializable{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        String value = "message";
        int age = 30;
        long[] extra = {1,2,3,4};
    }
    
    @Test
    public void test() throws RemotingException{
        SessionServer server = SessionLayers.bind("kubbo://localhost:8080?transportlayer=netty4", new SessionHandlerAdapter(){
            @Override
            public Object reply(SessionChannel channel, Object request) throws RemotingException {
                if(request instanceof Message){
                    assertEquals("message", ((Message)request).value);
                }
                return "Hello";
            }

        });

        SessionClient client = SessionLayers.connect("kubbo://localhost:8080?transportlayer=netty4&timeout=1000000");

        ResponseFuture Response = client.request(new Message());
        
        assertEquals("Hello", Response.get());
        
        client.close(3000);
        server.close(3000);
        
    }
    

}
