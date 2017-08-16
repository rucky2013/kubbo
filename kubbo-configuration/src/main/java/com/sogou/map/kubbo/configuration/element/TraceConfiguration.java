/**
 * 
 */
package com.sogou.map.kubbo.configuration.element;

import com.sogou.map.kubbo.configuration.Configuration;

/**
 * @author liufuliang
 *
 */
public class TraceConfiguration implements Configuration{

    private static final long serialVersionUID = 7998650641162529364L;

    public static final String TAG = "trace";
    
    /** 地址 */
    String address = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
