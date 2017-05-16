/**
 * 
 */
package com.sogou.map.kubbo.boot.configuration.element;

import com.sogou.map.kubbo.boot.configuration.Configuration;

/**
 * @author liufuliang
 *
 */
public class ServerConfiguration implements Configuration{
    private static final long serialVersionUID = 1L;
    public static final String TAG = "server";
    
    /** 绑定地址 */
    String bind = "kubbo://0.0.0.0:40660";

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }
}
