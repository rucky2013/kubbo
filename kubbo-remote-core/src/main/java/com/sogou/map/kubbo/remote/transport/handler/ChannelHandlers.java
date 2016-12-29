package com.sogou.map.kubbo.remote.transport.handler;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.heartbeat.HeartbeatHandler;

/**
 * @author liufuliang
 *
 */
public class ChannelHandlers {
    private static ChannelHandlers INSTANCE = new ChannelHandlers();
    protected ChannelHandlers() {
        
    }
    protected static ChannelHandlers getInstance() {
        return INSTANCE;
    }

    static void setTestingChannelHandlers(ChannelHandlers instance) {
        INSTANCE = instance;
    }
    
    public static ChannelHandler wrap(ChannelHandler handler, URL url){
        return ChannelHandlers.getInstance().wrapInternal(handler, url);
    }

    //TODO heartbeat
    protected ChannelHandler wrapInternal(ChannelHandler handler, URL url) {
        return new MessageArrayHandler(new HeartbeatHandler(new ExecutorWrappedChannelHandler(handler, url)));
    }



}