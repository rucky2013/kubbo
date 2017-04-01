/**
 * 
 */
package com.sogou.map.kubbo.common.http.impl;

import com.sogou.map.kubbo.common.http.AbstractHttpClient;
import com.sogou.map.kubbo.common.http.Const;
import com.sogou.map.kubbo.common.http.HttpRequestBuilder;
import com.sogou.map.kubbo.common.http.Method;
import com.sogou.map.kubbo.common.util.PlatformDependent;

/**
 * @author liufuliang
 *
 */
public class JdkHttpClient extends AbstractHttpClient{
    
    static {
        //http.maxConnections
        int httpMaxConnections = Integer.parseInt(System.getProperty("http.maxConnections", "0"));
        if(httpMaxConnections < Const.MAX_KEEPALIVE_CONNECTION_PER_HOST){
            System.setProperty("http.maxConnections", String.valueOf(Const.MAX_KEEPALIVE_CONNECTION_PER_HOST));
        }
        //https.protocols
        if(PlatformDependent.JAVA_VERSION == 6){
            System.setProperty("https.protocols", "TLSv1");
        }
        
        //System.setProperty("javax.net.debug", "all");
    }

    @Override
    public HttpRequestBuilder doReqest(Method method, String url) {
        return new JdkHttpRequestBuilder(method, url);
    }
}
