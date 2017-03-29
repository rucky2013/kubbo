package com.sogou.map.kubbo.metrics.influxdb.client;

import com.sogou.map.kubbo.common.URL;

/**
 * A Factory to create a instance of a InfluxDB Database adapter.
 *
 * @author liufuliang
 *
 */
public class InfluxDBFactory {
    /**
     * 
     * @param url e.g. http://metrics.mssp-system:8086?db=mssp&application=navi-link-rpc&interval=30
     * @return InfluxDB
     */
    public static InfluxDB connect(String url) {
        URL influxdb = URL.valueOf(url);
        return connect(influxdb);
    }
    
    public static InfluxDB connect(URL url) {
        String address = url.toIdentityString();
        String database = url.getParameter("db");
        
        return new InfluxDBImpl(address, database, null, null);
    }
    

}
