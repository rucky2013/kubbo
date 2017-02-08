package com.sogou.map.kubbo.remote.session.header;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.StringUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ExecutionException;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.SessionChannel;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.remote.transport.handler.AbstractChannelHandlerDelegate;

/**
 * HeaderSessionHandler
 * 
 * @author liufuliang
 */
public class HeaderSessionHandler extends AbstractChannelHandlerDelegate {

    protected static final Logger logger              = LoggerFactory.getLogger(HeaderSessionHandler.class);

    private final SessionHandler handler;

    public HeaderSessionHandler(SessionHandler handler){
        super(handler);
        if (handler == null) {
            throw new IllegalArgumentException("handler == NULL");
        }
        this.handler = handler;
    }

    @Override
    public void onConnected(Channel channel) throws RemotingException {
        SessionChannel sessionChannel = HeaderSessionChannel.getOrAddChannel(channel);
        try {
            handler.onConnected(sessionChannel);
        } finally {
            HeaderSessionChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void onDisconnected(Channel channel) throws RemotingException {
        SessionChannel sessionChannel = HeaderSessionChannel.getOrAddChannel(channel);
        try {
            handler.onDisconnected(sessionChannel);
        } finally {
            HeaderSessionChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void onSent(Channel channel, Object message) throws RemotingException {
        Throwable exception = null;
        try {
            SessionChannel sessionChannel = HeaderSessionChannel.getOrAddChannel(channel);
            try {
                handler.onSent(sessionChannel, message);
            } finally {
                HeaderSessionChannel.removeChannelIfDisconnected(channel);
            }
        } catch (Throwable t) {
            exception = t;
        }
        
        // notify future sent
        if (message instanceof Request) {
            Request request = (Request) message;
            InternalResponseFuture.sent(channel, request);
        }
        
        // throw
        if (exception != null) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            } else if (exception instanceof RemotingException) {
                throw (RemotingException) exception;
            } else {
                throw new RemotingException(channel.getLocalAddress(), channel.getRemoteAddress(),
                                            exception.getMessage(), exception);
            }
        }
    }

    @Override
    public void onReceived(Channel channel, Object message) throws RemotingException {
        SessionChannel sessionChannel = HeaderSessionChannel.getOrAddChannel(channel);
        try {
            if (message instanceof Request) {
                // handle request.
                Request request = (Request) message;
                if (request.isEvent()) {
                    handleEvent(channel, request);
                } else if (request.isTwoWay()) {
                    Response response = handleRequest(sessionChannel, request);
                    channel.send(response);
                } else {
                    handler.onReceived(sessionChannel, request.getData());
                }
            } else if (message instanceof Response) {
                // handle response.
                handleResponse(channel, (Response) message);
            } else if (message instanceof String) {
                handler.onReceived(sessionChannel, message);
            } else {
                // handle none
                handler.onReceived(sessionChannel, message);
            }
        } finally {
            HeaderSessionChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void onExceptonCaught(Channel channel, Throwable exception) throws RemotingException {
        if (exception instanceof ExecutionException) {
            ExecutionException e = (ExecutionException) exception;
            Object msg = e.getRequest();
            if (msg instanceof Request) {
                Request req = (Request) msg;
                if (req.isTwoWay() && ! req.isHeartbeat()) {
                    Response res = new Response(req.getId(), req.getVersion());
                    res.setStatus(Response.SERVER_ERROR);
                    res.setErrorMessage(StringUtils.toString(e));
                    channel.send(res);
                    return;
                }
            }
        }
        SessionChannel sessionChannel = HeaderSessionChannel.getOrAddChannel(channel);
        try {
            handler.onExceptonCaught(sessionChannel, exception);
        } finally {
            HeaderSessionChannel.removeChannelIfDisconnected(channel);
        }
    }
    
    protected void handleEvent(Channel channel, Request req) throws RemotingException {
        if (req.getData() != null && req.getData().equals(Request.READONLY_EVENT)) {
            channel.setAttribute(Constants.CHANNEL_ATTRIBUTE_READONLY_KEY, Boolean.TRUE);
        }
    }

    protected Response handleRequest(SessionChannel channel, Request req) throws RemotingException {
        Response res = new Response(req.getId(), req.getVersion());
        // bad requests
        if (req.isBroken()) {
            Object data = req.getData();

            String msg;
            if (data == null) msg = null;
            else if (data instanceof Throwable) msg = StringUtils.toString((Throwable) data);
            else msg = data.toString();
            res.setErrorMessage("Fail to decode request due to: " + msg);
            res.setStatus(Response.BAD_REQUEST);

            return res;
        }
        // find handler by message class.
        Object msg = req.getData();
        try {
            // handle data.
            Object result = handler.reply(channel, msg);
            res.setStatus(Response.OK);
            res.setResult(result);
        } catch (Throwable e) {
            res.setStatus(Response.SERVICE_ERROR);
            res.setErrorMessage(StringUtils.toString(e));
        }
        return res;
    }

    protected void handleResponse(Channel channel, Response response) throws RemotingException {
        if (response != null && !response.isHeartbeat()) {
            InternalResponseFuture.received(channel, response);
        }
    }
}