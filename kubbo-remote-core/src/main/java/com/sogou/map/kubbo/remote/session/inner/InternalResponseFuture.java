package com.sogou.map.kubbo.remote.session.inner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.NamedThreadFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.TimeoutException;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.remote.session.ResponseCallback;
import com.sogou.map.kubbo.remote.session.ResponseFuture;

/**
 * InternalResponseFuture.
 * 
 * @author liufuliang
 */
public class InternalResponseFuture implements ResponseFuture {

    private static final Logger                   logger = LoggerFactory.getLogger(InternalResponseFuture.class);

    private static final Map<Long, Channel>       CHANNELS   = new ConcurrentHashMap<Long, Channel>();

    private static final Map<Long, InternalResponseFuture> FUTURES   = new ConcurrentHashMap<Long, InternalResponseFuture>();

    // invoke id.
    private final long                            id;

    private final Channel                         channel;
    
    private final Request                         request;

    private final int                             timeout;

    private final Lock                            lock = new ReentrantLock();

    private final Condition                       done = lock.newCondition();

    private final long                            start = System.currentTimeMillis();

    private volatile long                         sent;
    
    private volatile Response                     response;

    private volatile ResponseCallback             callback;

    public InternalResponseFuture(Channel channel, Request request, int timeout){
        this.channel = channel;
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout > 0 ? timeout : channel.getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
        // put into waiting map.
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
    }
    
    @Override
    public Object get() throws RemotingException {
        return get(timeout);
    }

    @Override
    public Object get(int timeout) throws RemotingException {
        if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            
            if (!isDone()) {
                throw new TimeoutException(sent > 0, channel, getTimeoutMessage(false));
            }
        }
        return createResult();
    }
    
    @Override
    public void cancel(){
        Response errorResult = new Response(id);
        errorResult.setErrorMessage("request future has been canceled.");
        response = errorResult ;
        FUTURES.remove(id);
        CHANNELS.remove(id);
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public void setCallback(ResponseCallback callback) {
        if (isDone()) {
            invokeCallback(callback);
        } else {
            boolean isdone = false;
            lock.lock();
            try{
                if (!isDone()) {
                    this.callback = callback;
                } else {
                    isdone = true;
                }
            }finally {
                lock.unlock();
            }
            if (isdone){
                invokeCallback(callback);
            }
        }
    }
    private void invokeCallback(ResponseCallback c){
        ResponseCallback callbackCopy = c;
        if (callbackCopy == null){
            throw new NullPointerException("callback cannot be null.");
        }
        c = null;
        Response res = response;
        if (res == null) {
            throw new IllegalStateException("response cannot be null. url: " + channel.getUrl());
        }
        
        if (res.getStatus() == Response.OK) {
            try {
                callbackCopy.done(res.getResult());
            } catch (Exception e) {
                logger.error("callback invoke error. reasult: " + res.getResult() + ", url: " + channel.getUrl(), e);
            }
        } else if (res.getStatus() == Response.CLIENT_TIMEOUT || res.getStatus() == Response.SERVER_TIMEOUT) {
            try {
                TimeoutException te = new TimeoutException(res.getStatus() == Response.SERVER_TIMEOUT, channel, res.getErrorMessage());
                callbackCopy.caught(te);
            } catch (Exception e) {
                logger.error("callback invoke error, url: " + channel.getUrl(), e);
            }
        } else {
            try {
                RuntimeException re = new RuntimeException(res.getErrorMessage());
                callbackCopy.caught(re);
            } catch (Exception e) {
                logger.error("callback invoke error, url: " + channel.getUrl(), e);
            }
        }
    }

    private Object createResult() throws RemotingException {
        Response res = response;
        if (res == null) {
            throw new IllegalStateException("response cannot be NULL");
        }
        if (res.getStatus() == Response.OK) {
            return res.getResult();
        }
        if (res.getStatus() == Response.CLIENT_TIMEOUT || res.getStatus() == Response.SERVER_TIMEOUT) {
            throw new TimeoutException(res.getStatus() == Response.SERVER_TIMEOUT, channel, res.getErrorMessage());
        }
        throw new RemotingException(channel, res.getErrorMessage());
    }

    private long getId() {
        return id;
    }
    
    private Channel getChannel() {
        return channel;
    }
    
    private boolean isSent() {
        return sent > 0;
    }

    public Request getRequest() {
        return request;
    }

    private int getTimeout() {
        return timeout;
    }

    private long getStartTimestamp() {
        return start;
    }

    private String getTimeoutMessage(boolean scan) {
        long nowTimestamp = System.currentTimeMillis();
        
        return new StringBuffer(32)
            .append(sent > 0 ? "Waiting server-side response timeout" : "Sending request timeout in client-side")
            .append(scan ? " by scan timer." : ".")
            .append(" start time: ")
            .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(start))).append(",") 
            .append(" end time: ")
            .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())).append(",") 
            .append((sent > 0 ? " client elapsed: " + (sent - start) + " ms, server elapsed: " + (nowTimestamp - sent)
                    : " elapsed: " + (nowTimestamp - start)) + " ms,")
            .append(" timeout: ")
            .append(timeout).append("ms,")
            .append(" request: ")
            .append(request).append(",")
            .append(" channel: ")
            .append(channel.getLocalAddress()).append(" -> ").append(channel.getRemoteAddress())
            .toString();
    }
    
    private void doSent() {
        sent = System.currentTimeMillis();
    }
    
    private void doReceived(Response res) {
        lock.lock();
        try {
            response = res;
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
        if (callback != null) {
            invokeCallback(callback);
        }
    }
    
    //================================
    public static InternalResponseFuture getFuture(long id) {
        return FUTURES.get(id);
    }

    public static boolean hasFuture(Channel channel) {
        return CHANNELS.containsValue(channel);
    }

    public static void sent(Channel channel, Request request) {
        InternalResponseFuture future = FUTURES.get(request.getId());
        if (future != null) {
            future.doSent();
        }
    }
    public static void received(Channel channel, Response response) {
        try {
            InternalResponseFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                future.doReceived(response);
            } else {
                String msg = new StringBuffer(32)
                    .append("The timeout response finally returned at ")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())).append(", ")
                    .append(" response: ")
                    .append(response)
                    .append(channel == null ? "" : ", channel: " + channel.getLocalAddress() + " -> " + channel.getRemoteAddress())
                    .toString();
                logger.warn(msg);
            }
        } finally {
            CHANNELS.remove(response.getId());
        }
    }

    private static class RemoteInvocationTimeoutScan implements Runnable {
        public void run() {
            while (true) {
                try {
                    for (InternalResponseFuture future : FUTURES.values()) {
                        if (future == null || future.isDone()) {
                            continue;
                        }
                        if (System.currentTimeMillis() - future.getStartTimestamp() > future.getTimeout()) {
                            // create exception response.
                            Response timeoutResponse = new Response(future.getId());
                            // set timeout status.
                            timeoutResponse.setStatus(future.isSent() ? Response.SERVER_TIMEOUT : Response.CLIENT_TIMEOUT);
                            timeoutResponse.setErrorMessage(future.getTimeoutMessage(true));
                            // handle response.
                            InternalResponseFuture.received(future.getChannel(), timeoutResponse);
                        }
                    }
                    Thread.sleep(30);
                } catch (Throwable e) {
                    logger.error("Exception when scan the timeout invocation of remoting.", e);
                }
            }
        }
    }

    static {
        ThreadFactory factory = new NamedThreadFactory("KubboResponseTimer", true);
        factory.newThread(new RemoteInvocationTimeoutScan()).start();
    }

}