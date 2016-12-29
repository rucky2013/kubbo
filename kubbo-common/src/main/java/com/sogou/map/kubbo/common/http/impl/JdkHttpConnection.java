/**
 * 
 */
package com.sogou.map.kubbo.common.http.impl;

import java.net.HttpURLConnection;

import com.sogou.map.kubbo.common.http.HttpConnection;

/**
 * @author liufuliang
 *
 */
public class JdkHttpConnection implements HttpConnection {
    HttpURLConnection connection;

    public JdkHttpConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public HttpURLConnection getHttpURLConnection(){
        return this.connection;
    }
    
    @Override
    public void close() {
        if(this.connection != null){
            this.connection.disconnect();
        }
        
    }


}
