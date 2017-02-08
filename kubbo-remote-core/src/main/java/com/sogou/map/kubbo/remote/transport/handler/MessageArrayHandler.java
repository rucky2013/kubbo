package com.sogou.map.kubbo.remote.transport.handler;

import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.transport.MessageArray;

/**
 * @author liufuliang
 * @see MessageArray
 */
public class MessageArrayHandler extends AbstractChannelHandlerDelegate {

    public MessageArrayHandler(ChannelHandler handler) {
        super(handler);
    }

    
    @Override
    public void onReceived(Channel channel, Object message) throws RemotingException {
        if (message instanceof MessageArray) {
            MessageArray array = (MessageArray)message;
            for(Object obj : array) {
                handler.onReceived(channel, obj);
            }
        } else {
            handler.onReceived(channel, message);
        }
    }
}
