package com.sogou.map.kubbo.remote.session;

/**
 * Response
 * 
 * @author liufuliang
 */
public class Response {
    public static final String HEARTBEAT_EVENT = null;
    public static final String READONLY_EVENT = "R";

    /**
     * ok.
     */
    public static final byte OK                = 20;

    /**
     * clien side timeout.
     */
    public static final byte CLIENT_TIMEOUT    = 30;

    /**
     * server side timeout.
     */
    public static final byte SERVER_TIMEOUT    = 31;

    /**
     * request format error.
     */
    public static final byte BAD_REQUEST       = 40;

    /**
     * response format error.
     */
    public static final byte BAD_RESPONSE      = 50;

    /**
     * service not found.
     */
    public static final byte SERVICE_NOT_FOUND = 60;

    /**
     * service error.
     */
    public static final byte SERVICE_ERROR     = 70;

    /**
     * internal server error.
     */
    public static final byte SERVER_ERROR      = 80;

    /**
     * internal server error.
     */
    public static final byte CLIENT_ERROR      = 90;

    private long             id               = 0;

    private String           version;

    private byte             status           = OK;

    private boolean          event         = false;

    private String           errorMsg;

    private Object           result;

    public Response(){
    }

    public Response(long id){
        this.id = id;
    }

    public Response(long id, String version){
        this.id = id;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
    
    public boolean isOK(){
    	return this.status == OK;
    }
    
    public boolean isEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = true;
        this.result = event;
    }
    
    public void setEvent(boolean isEvent) {
        this.event = isEvent;
    }

    public boolean isHeartbeat() {
        return event && HEARTBEAT_EVENT == result;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMsg;
    }

    public void setErrorMessage(String msg) {
        this.errorMsg = msg;
    }

    @Override
    public String toString() {
        return "Response [id=" + id + ", version=" + version + ", status=" + status + ", event=" + event
               + ", error=" + errorMsg + ", result=" + (result == this ? "this" : result) + "]";
    }
}