package com.sogou.map.kubbo.remote.transport.handler;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;

/**
 * @author liufuliang
 *
 */
public class ChannelEventRunnable implements Runnable {
    private static final Logger logger             = LoggerFactory.getLogger(ChannelEventRunnable.class);

    private final ChannelHandler handler;
    private final Channel channel;
    private final ChannelState state;
    private final Throwable exception;
    private final Object message;
    
    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state) {
        this(channel, handler, state, null);
    }
    
    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Object message) {
        this(channel, handler, state, message, null);
    }
    
    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Throwable t) {
        this(channel, handler, state, null , t);
    }

    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Object message, Throwable exception) {
        this.channel = channel;
        this.handler = handler;
        this.state = state;
        this.message = message;
        this.exception = exception;
    }
    
    public void run() {
        switch (state) {
            case CONNECTED:
                try{
                    handler.onConnected(channel);
                }catch (Exception e) {
                    logger.warn("ChannelEventRunnable handle " + state + " operation error, channel is " + channel, e);
                }
                break;
            case DISCONNECTED:
                try{
                    handler.onDisconnected(channel);
                }catch (Exception e) {
                    logger.warn("ChannelEventRunnable handle " + state + " operation error, channel is " + channel, e);
                }
                break;
            case SENT:
                try{
                    handler.onSent(channel,message);
                }catch (Exception e) {
                    logger.warn("ChannelEventRunnable handle " + state + " operation error, channel is " + channel
                            + ", message is "+ message,e);
                }
                break;
            case RECEIVED:
                try{
                    handler.onReceived(channel, message);
                }catch (Exception e) {
                    logger.warn("ChannelEventRunnable handle " + state + " operation error, channel is " + channel
                            + ", message is "+ message,e);
                }
                break;
            case CAUGHT:
                try{
                    handler.onExceptonCaught(channel, exception);
                }catch (Exception e) {
                    logger.warn("ChannelEventRunnable handle " + state + " operation error, channel is "+ channel
                            + ", message is: " + message + ", exception is " + exception,e);
                }
                break;
            default:
                logger.warn("unknown state: " + state + ", message is " + message);
        }
    }

    /**
     * ChannelState
     * 
     * @author liufuliang
     */
    public enum ChannelState{
        CONNECTED,
        
        DISCONNECTED,
        
        SENT,
        
        RECEIVED,
        
        CAUGHT
    }

}