package com.sogou.map.kubbo.remote.session;

import java.util.concurrent.atomic.AtomicLong;

import com.sogou.map.kubbo.common.util.StringUtils;

/**
 * Request.
 * 
 * @author liufuliang
 */
public class Request {

    /** heartbeat event */
    public static final String EVENT_HEARTBEAT = null;

    /** readonly event */
    public static final String EVENT_READONLY = "R";
    
    /** request id seq */
    private static final AtomicLong REQUEST_SEQ = new AtomicLong(0);

    private final long id;

    private String  version;

    private boolean twoWay = true;
    
    private boolean event = false;

    private boolean broken = false;

    private Object data;

    public Request() {
        id = newId();
    }

    public Request(long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = true;
        this.data = event;
    }
    
    public void setEvent(boolean isEvent) {
        this.event = isEvent;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object msg) {
        this.data = msg;
    }

    public boolean isHeartbeat() {
        return event && EVENT_HEARTBEAT == data;
    }

    public void setHeartbeat(boolean isHeartbeat) {
        if (isHeartbeat) {
            setEvent(EVENT_HEARTBEAT);
        }
    }

    private static long newId() {
        return REQUEST_SEQ.getAndIncrement();
    }

    @Override
    public String toString() {
        return "Request [id=" + id + ", version=" + version + ", twoway=" + twoWay + ", event=" + event
               + ", broken=" + broken + ", data=" + (data == this ? "this" : StringUtils.safeToString(data)) + "]";
    }
}
