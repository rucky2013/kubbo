package com.sogou.map.kubbo.remote.transport.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;

/**
 * ChannelHandlerGroup
 * 
 * @author liufuliang
 */
public class ChannelHandlerGroup implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChannelHandlerGroup.class);

    private final Collection<ChannelHandler> channelHandlers = new CopyOnWriteArraySet<ChannelHandler>();
    
    public ChannelHandlerGroup() { }
    
    public ChannelHandlerGroup(ChannelHandler... handlers) {
        this(handlers == null ? null : Arrays.asList(handlers));
    }

    public ChannelHandlerGroup(Collection<ChannelHandler> handlers) {
        if (handlers != null && handlers.size() > 0) {
            this.channelHandlers.addAll(handlers);
        }
    }

    public Collection<ChannelHandler> getChannelHandlers() {
        return channelHandlers;
    }

    public ChannelHandlerGroup addChannelHandler(ChannelHandler handler) {
        this.channelHandlers.add(handler);
        return this;
    }

    public ChannelHandlerGroup removeChannelHandler(ChannelHandler handler) {
        this.channelHandlers.remove(handler);
        return this;
    }

    @Override
    public void onConnected(Channel channel) {
        for (ChannelHandler listener : channelHandlers) {
            try {
                listener.onConnected(channel);
            } catch (Throwable t) {
                logger.error(t.getMessage(), t);
            }
        }
    }

    @Override
    public void onDisconnected(Channel channel) {
        for (ChannelHandler listener : channelHandlers) {
            try {
                listener.onDisconnected(channel);
            } catch (Throwable t) {
                logger.error(t.getMessage(), t);
            }
        }
    }

    @Override
    public void onSent(Channel channel, Object message) {
        for (ChannelHandler listener : channelHandlers) {
            try {
                listener.onSent(channel, message);
            } catch (Throwable t) {
                logger.error(t.getMessage(), t);
            }
        }
    }

    @Override
    public void onReceived(Channel channel, Object message) {
        for (ChannelHandler listener : channelHandlers) {
            try {
                listener.onReceived(channel, message);
            } catch (Throwable t) {
                logger.error(t.getMessage(), t);
            }
        }
    }

    @Override
    public void onExceptonCaught(Channel channel, Throwable exception) {
        for (ChannelHandler listener : channelHandlers) {
            try {
                listener.onExceptonCaught(channel, exception);
            } catch (Throwable t) {
                logger.error(t.getMessage(), t);
            }
        }
    }
    
}