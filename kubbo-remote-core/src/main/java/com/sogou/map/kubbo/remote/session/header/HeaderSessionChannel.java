package com.sogou.map.kubbo.remote.session.header;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.SessionChannel;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.remote.session.ResponseFuture;
import com.sogou.map.kubbo.remote.transport.AbstractChannelDelegate;


/**
 * HeaderSessionChannel
 * 
 * @author liufuliang
 */
final class HeaderSessionChannel extends AbstractChannelDelegate implements SessionChannel {

    private static final Logger logger      = LoggerFactory.getLogger(HeaderSessionChannel.class);

    private static final String CHANNEL_KEY = HeaderSessionChannel.class.getName() + ".CHANNEL";

    private volatile boolean    closed      = false;

    HeaderSessionChannel(Channel channel){
    	super(channel);
    }

    static HeaderSessionChannel getOrAddChannel(Channel ch) {
        if (ch == null) {
            return null;
        }
        HeaderSessionChannel ret = (HeaderSessionChannel) ch.getAttribute(CHANNEL_KEY);
        if (ret == null) {
            ret = new HeaderSessionChannel(ch);
            if (ch.isConnected()) {
                ch.setAttribute(CHANNEL_KEY, ret);
            }
        }
        return ret;
    }
    
    static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && ! ch.isConnected()) {
            ch.removeAttribute(CHANNEL_KEY);
        }
    }
    
    @Override
    public void send(Object message) throws RemotingException {
        send(message, getUrl().getParameter(Constants.SEND_BLOCKING_KEY, Constants.DEFAULT_SEND_BLOCKING));
    }
    
    @Override
    public void send(Object message, boolean blocking) throws RemotingException {
        if (closed) {
            throw new RemotingException(
            		this.getLocalAddress(), 
            		null, 
            		"Failed to send message " + message + ", cause: The channel " + this + " is closed!");
        }
        if (message instanceof Request
                || message instanceof Response
                || message instanceof String) {
            channel.send(message, blocking);
        } else {
            Request request = new Request();
            request.setVersion(Constants.DEFAULT_VERSION);
            request.setTwoWay(false);
            request.setData(message);
            channel.send(request, blocking);
        }
    }

    @Override
    public ResponseFuture request(Object request) throws RemotingException {
        return request(request, channel.getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT));
    }

    @Override
    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send request " + request + ", cause: The channel " + this + " is closed!");
        }
        // create request.
        Request req = new Request();
        req.setVersion(Constants.DEFAULT_VERSION);
        req.setTwoWay(true);
        req.setData(request);
        InternalResponseFuture future = new InternalResponseFuture(channel, req, timeout);
        try{
            channel.send(req);
        }catch (RemotingException e) {
            future.cancel();
            throw e;
        }
        return future;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    // graceful close
    @Override
    public void close(int timeout) {
        if (closed) {
            return;
        }
        closed = true;
        if (timeout > 0) {
            long start = System.currentTimeMillis();
            while (InternalResponseFuture.hasFuture(HeaderSessionChannel.this) 
                    && System.currentTimeMillis() - start < timeout) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        close();
    }

    @Override
    public SessionHandler getSessionHandler() {
        return (SessionHandler) getChannelHandler();
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        HeaderSessionChannel other = (HeaderSessionChannel) obj;
        if (channel == null) {
            if (other.channel != null) return false;
        } else if (!channel.equals(other.channel)) return false;
        return true;
    }

    @Override
    public String toString() {
        return channel.toString();
    }

}