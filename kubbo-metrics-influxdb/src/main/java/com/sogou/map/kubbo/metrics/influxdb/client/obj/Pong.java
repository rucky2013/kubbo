package com.sogou.map.kubbo.metrics.influxdb.client.obj;

/**
 * Representation of the response for a influxdb ping.
 *
 * @author liufuliang
 *
 */
public class Pong {
    public static final Pong NULL = new Pong("Unkown", 0);
    private String version;
    private long responseTime;

    
    public Pong(){}
    
    public Pong(String version, long responseTime) {
        super();
        this.version = version;
        this.responseTime = responseTime;
    }

    /**
     * @return the status
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * @param version
     *            the status to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @return the responseTime
     */
    public long getResponseTime() {
        return this.responseTime;
    }

    /**
     * @param responseTime
     *            the responseTime to set
     */
    public void setResponseTime(final long responseTime) {
        this.responseTime = responseTime;
    }

}
