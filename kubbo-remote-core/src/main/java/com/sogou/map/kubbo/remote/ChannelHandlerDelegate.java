package com.sogou.map.kubbo.remote;

/**
 * @author liufuliang
 */
public interface ChannelHandlerDelegate extends ChannelHandler {
    public ChannelHandler getHandler();
}