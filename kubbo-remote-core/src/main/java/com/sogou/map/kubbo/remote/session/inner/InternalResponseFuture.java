package com.sogou.map.kubbo.remote.session.inner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.sogou.map.kubbo.common.threadpool.NamedThreadFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.RemoteException;
import com.sogou.map.kubbo.remote.TimeoutException;
import com.sogou.map.kubbo.remote.session.Request;
import com.sogou.map.kubbo.remote.session.Response;
import com.sogou.map.kubbo.remote.session.ResponseFuture;
import com.sogou.map.kubbo.remote.session.ResponseListener;

/**
 * InternalResponseFuture.
 * 
 * @author liufuliang
 */
public class InternalResponseFuture implements ResponseFuture {

    private static final Logger logger = LoggerFactory.getLogger(InternalResponseFuture.class);

    private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<Long, Channel>();

    private static final Map<Long, InternalResponseFuture> FUTURES = new ConcurrentHashMap<Long, InternalResponseFuture>();

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // invoke id.
    private final long id;

    private final Channel channel;

    private final Request request;

    private final int timeout;

    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    private final long start = System.currentTimeMillis();

    private volatile long sent;

    private volatile Response response;

    private List<ResponseListener> listeners;

    public InternalResponseFuture(Channel channel, Request request, int timeout) {
        this.channel = channel;
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout > 0 ? timeout
                : channel.getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
        this.listeners = new ArrayList<ResponseListener>(2);
        // put into waiting map.
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
    }

    @Override
    public Object get() throws RemoteException {
        return get(timeout);
    }

    @Override
    public Object get(int timeout) throws RemoteException {
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
                throw new TimeoutException(sent > 0 ? TimeoutException.SERVER_SIDE : TimeoutException.CLIENT_SIDE, 
                        channel, getTimeoutMessage());
            }
        }
        return createResult();
    }

    @Override
    public void cancel() {
        Response errorResult = new Response(id);
        errorResult.setErrorMessage("request future has been canceled.");
        response = errorResult;
        FUTURES.remove(id);
        CHANNELS.remove(id);
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public void addListener(ResponseListener listener) {
        if (isDone()) {
            notifyListener(listener);
        } else {
            boolean isdone = false;
            lock.lock();
            try {
                if (!isDone()) {
                    this.listeners.add(listener);
                } else {
                    isdone = true;
                }
            } finally {
                lock.unlock();
            }
            if (isdone) {
                notifyListener(listener);
            }
        }
    }

    private void notifyListeners(){
        if(listeners.isEmpty()){
            return;
        }
        
        for(ResponseListener listener : listeners){
            notifyListener(listener);
        }
    }
    
    private void notifyListener(ResponseListener listener){
        ResponseListener listenerCopy = listener;
        if (listenerCopy == null) {
            throw new NullPointerException("listener == NULL");
        }
        listener = null;
        Response rsp = response;
        if (rsp == null) {
            throw new IllegalStateException("response cannot be NULL");
        }

        try {
            if(rsp.isOK()) {
                listenerCopy.done(rsp);
            } else {
                RemoteException ex = createRemoteException(rsp);
                listenerCopy.caught(ex);
            }
        } catch(Exception e) {
            StringBuilder s = new StringBuilder(32);
            s.append("Response listener error for ").append(channel.getUrl());
            if(rsp.isOK()) {
                s.append(", reasult: ").append(rsp.getResult());
            }
            logger.error(s.toString(), e);
        }
    }

    private Object createResult() throws RemoteException {
        Response rsp = response;
        if (rsp == null) {
            throw new IllegalStateException("response cannot be NULL");
        }
        if (rsp.isOK()) {
            return rsp.getResult();
        }
        throw createRemoteException(rsp);
    }
    
    private RemoteException createRemoteException(Response rsp) {
        if(rsp.getStatus() == Response.CLIENT_TIMEOUT) {
            return new TimeoutException(TimeoutException.CLIENT_SIDE, channel, rsp.getErrorMessage());
        }
        if(rsp.getStatus() == Response.SERVER_TIMEOUT) {
            return new TimeoutException(TimeoutException.SERVER_SIDE, channel, rsp.getErrorMessage());
        }
        return new RemoteException(channel, rsp.getErrorMessage());
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

    private String getTimeoutMessage() {
        long now = System.currentTimeMillis();
        return new StringBuilder(32)
                .append(sent > 0 ? "Waiting server response timeout." : "Sending client request timeout.")
                .append(" start: ").append(TIME_FORMAT.format(new Date(start))).append(",")
                .append(" end: ").append(TIME_FORMAT.format(new Date())).append(",")
                .append(" elapsed: ").append(now - start).append("ms,")
                .append(" timeout: ").append(timeout).append("ms,")
                .append(" request: ").append(request).append(",")
                .append(" channel: ").append(channel.getLocalAddress()).append(" -> ").append(channel.getRemoteAddress()).toString();
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
        
        notifyListeners();
    }

    // ================================
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
                String msg = new StringBuffer(128)
                        .append("The timeout response finally returned at ")
                        .append(TIME_FORMAT.format(new Date()))
                        .append(", response: ").append(response)
                        .append(channel == null ? ""
                                : ", channel: " + channel.getLocalAddress() + " -> " + channel.getRemoteAddress())
                        .toString();
                logger.warn(msg);
            }
        } finally {
            CHANNELS.remove(response.getId());
        }
    }

    private static class RemoteInvocationTimeoutScan implements Runnable {
        @Override
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
                            timeoutResponse.setErrorMessage(future.getTimeoutMessage());
                            // handle response.
                            InternalResponseFuture.received(future.getChannel(), timeoutResponse);
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(30);
                } catch (Throwable e) {
                    logger.error("Exception when scan the timeout invocation of remoting.", e);
                }
            }
        }
    }

    static {
        ThreadFactory factory = new NamedThreadFactory("kubbo-response-timer", true);
        factory.newThread(new RemoteInvocationTimeoutScan()).start();
    }

}