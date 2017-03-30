package com.sogou.map.kubbo.remote.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.ChannelHandlerDelegate;
import com.sogou.map.kubbo.remote.Endpoint;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.transport.handler.AbstractChannelHandlerDelegate;

/**
 * AbstractEndpoint
 * 
 * @author liufuliang
 */
public abstract class AbstractEndpoint extends AbstractChannelHandlerDelegate implements Endpoint {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEndpoint.class);

    private volatile URL         url;

    private volatile boolean     closed;
    
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    public AbstractEndpoint(URL url, ChannelHandler handler) {
        super(handler);
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        this.url = url;
    }
    
    @Override
    public void send(Object message) throws RemotingException {
        send(message, url.getParameter(Constants.SEND_BLOCKING_KEY, Constants.DEFAULT_SEND_BLOCKING));
    }
    
    @Override
    public void close() {
        closed = true;
        //attribute
        try {
            removeAttributes();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
    
    @Override
    public void close(int timeout) {
        close();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    protected void setUrl(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        this.url = url;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        } else {
            return handler;
        }
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void onConnected(Channel ch) throws RemotingException {
        if (closed) {
            return;
        }
        super.onConnected(ch);
    }


    @Override
    public void onSent(Channel ch, Object msg) throws RemotingException {
        if (closed) {
            return;
        }
        super.onSent(ch, msg);
    }

    @Override
    public void onReceived(Channel ch, Object msg) throws RemotingException {
        if (closed) {
            return;
        }
        super.onReceived(ch, msg);
    }
    
    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    @Override
    public void removeAttributes(){
        attributes.clear();
    }
    
    @Override
    public Object getAttribute(String key, Object defaultValue) {
        if (attributes == null) {
            return defaultValue;
        }
        Object value = attributes.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}