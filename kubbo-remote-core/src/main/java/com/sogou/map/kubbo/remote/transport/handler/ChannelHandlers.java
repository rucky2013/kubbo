package com.sogou.map.kubbo.remote.transport.handler;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.heartbeat.HeartbeatHandler;

/**
 * @author liufuliang
 *
 */
public class ChannelHandlers {

    public static ChannelHandler wrap(ChannelHandler handler, URL url){
        return new MessageArrayHandler(new HeartbeatHandler(new ExecutorWrappedChannelHandler(handler, url)));
    }
    
    protected ChannelHandlers() {
        
    }

}