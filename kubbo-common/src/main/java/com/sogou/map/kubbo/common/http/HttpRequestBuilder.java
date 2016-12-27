/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author liufuliang
 *
 */
public interface HttpRequestBuilder {
    /**
     * Add a request header.  
     *
     * @param header The header type
     * @param value The value
     * @return this
     */
    HttpRequestBuilder header(String key, Object value);

    /**
     * Add a query key/value pair to the URL
     *
     * @param key A key
     * @param value A value
     * @return this
     */
    HttpRequestBuilder param(String key, String value);
    
    
    /**
     * Add a query key/value pair to the URL
     *
     * @param key A key
     * @param value A value
     * @param condition
     * @return this
     */
    HttpRequestBuilder paramIf(String key, String value, boolean condition);

    /**
     * Set basic auth credentials
     *
     * @param username The username
     * @param password The password
     * @return this
     */
    HttpRequestBuilder basicAuthentication(String username, String password);

    
    /**
     * Set bearer token auth credentials
     *
     * @param token The token
     * @return this
     */
    HttpRequestBuilder tokenAuthentication(String token);
    
    /**
     * Set the request body.  May be a string, byte array, ByteBuf, InputStream,
     * Image or an Object which can be converted to JSON by a vanilla ObjectMapper.
     * <p/>
     * For custom serialization, convert to a byte stream first.
     *
     * @param o The body
     * @return this
     */
    HttpRequestBuilder body(Object body);
    
    /**
     * 
     * @param timeout milli
     * @return this
     */
    HttpRequestBuilder readTimeout(int timeout);
    
    /**
     * 
     * @param timeout milli
     * @return this
     */
    HttpRequestBuilder connectTimeout(int timeout);
    
    /**
     * 
     * @param compress
     * @return
     */
    HttpRequestBuilder gzip(boolean gzip);
    
    /**
     * 
     * @param keepalive
     * @return
     */
    HttpRequestBuilder keepalive(boolean keepalive);
    

    /**
     * 
     * @param cache
     * @return
     */
    HttpRequestBuilder cache(boolean cache);
    
    /**
     * 
     * @param ifModifiedSince
     * @return
     */
    HttpRequestBuilder ifModifiedSince(long ifModifiedSince);

    /**
     * 
     * @param followRedirects
     * @return
     */
    HttpRequestBuilder followRedirects(boolean followRedirects);
    /**
     * 
     * @param sslSocketFactory
     * @return
     */
    HttpRequestBuilder setSSLSocketFactory(SSLSocketFactory sslSocketFactory);
    
    /**
     * 
     * @param hostnameVerifier
     * @return
     */
    HttpRequestBuilder setHostnameVerifier(HostnameVerifier hostnameVerifier);
    
    /**
     * 
     * @param handler
     * @return
     */
    HttpRequestBuilder setChunkedHandler(ChunkedHandler handler);
    
    /**
     * 
     * @return
     */
    HttpRequest build();
    
    /*
     * 不设置超时时间, 适合于websocket chunked传输
     */
    <T> void watch(Class<T> asType, Watcher<T> watcher);
    
    /*
     * 不设置超时时间, 适合于http长轮询
     */
    <T> T watch(Class<T> asType) throws KubboHttpException;
    
    /*
     * 不设置超时时间, 适合于http长轮询
     */
    HttpResponse watch() throws KubboHttpException;
    
    /**
     * 
     * @param asType
     * @return
     * @throws KubboHttpException
     */
    <T> T execute(Class<T> asType) throws KubboHttpException;
    
    /**
     * 
     * @param asType 返回类型
     * @return
     * @throws KubboHttpException
     */
    HttpResponse execute() throws KubboHttpException;

    

}

