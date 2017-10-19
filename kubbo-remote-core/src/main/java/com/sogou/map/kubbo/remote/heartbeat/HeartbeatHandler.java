package com.sogou.map.kubbo.remote.heartbeat;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.remote.transport.handler.AbstractChannelHandlerDelegate;

/**
 * @author liufuliang
 */
public class HeartbeatHandler extends AbstractChannelHandlerDelegate {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    public static String KEY_READ_TIMESTAMP = "READ_TIMESTAMP";

    public static String KEY_WRITE_TIMESTAMP = "WRITE_TIMESTAMP";

    public HeartbeatHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void onConnected(Channel channel) throws RemotingException {
        readAndWriteActive(channel);
        handler.onConnected(channel);
    }

    @Override
    public void onDisconnected(Channel channel) throws RemotingException {
        readAndWriteInactive(channel);
        handler.onDisconnected(channel);
    }

    @Override
    public void onSent(Channel channel, Object message) throws RemotingException {
        writeActive(channel);
        handler.onSent(channel, message);
    }
    
    @Override
    public void onReceived(Channel channel, Object message) throws RemotingException {
        readActive(channel);
        if (isHeartbeatRequest(message)) {
            Request req = (Request) message;
            if (req.isTwoWay()) {
                Response res = new Response(req.getId(), req.getVersion());
                res.setEvent(Response.EVENT_HEARTBEAT);
                channel.send(res);
                if (logger.isDebugEnabled()) {
                    int heartbeat = channel.getUrl().getParameter(Constants.HEARTBEAT_KEY, 0);
                    logger.debug(new StringBuilder(128)
                            .append("Received heartbeat from remote channel ")
                            .append(channel.getRemoteAddress())
                            .append(", cause: The channel has no data-transmission exceeds a heartbeat period")
                            .append((heartbeat > 0 ? ": " + heartbeat + "ms" : ""))
                            .toString());
                }
            }
            return;
        }
        if (isHeartbeatResponse(message)) {
            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder(50)
                        .append("Receive heartbeat response in thread ")
                        .append(Thread.currentThread().getName())
                        .toString());
            }
            return;
        }
        handler.onReceived(channel, message);
    }
    private void readAndWriteActive(Channel channel) {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
    }
    
    private void readActive(Channel channel) {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
    }

    private void writeActive(Channel channel) {
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
    }

    private void readAndWriteInactive(Channel channel) {
        channel.removeAttribute(KEY_READ_TIMESTAMP);
        channel.removeAttribute(KEY_WRITE_TIMESTAMP);
    }

    private boolean isHeartbeatRequest(Object message) {
        return (message instanceof Request) && ((Request) message).isHeartbeat();
    }

    private boolean isHeartbeatResponse(Object message) {
        return (message instanceof Response) && ((Response)message).isHeartbeat();
    }
}
