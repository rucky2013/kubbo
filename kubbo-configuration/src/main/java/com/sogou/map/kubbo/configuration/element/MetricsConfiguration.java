/**
 * 
 */
package com.sogou.map.kubbo.configuration.element;

import com.sogou.map.kubbo.configuration.Configuration;

/**
 * @author liufuliang
 *
 */
public class MetricsConfiguration implements Configuration{

    private static final long serialVersionUID = -8258573815975498210L;

    public static final String TAG = "metrics";
    
    /** 地址 TAG.address */
    String address = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
