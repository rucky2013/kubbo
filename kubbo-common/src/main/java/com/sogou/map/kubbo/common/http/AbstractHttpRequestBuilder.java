/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import com.sogou.map.kubbo.common.io.Bytes;

/**
 * @author liufuliang
 *
 */
public abstract class AbstractHttpRequestBuilder implements HttpRequestBuilder {
    protected int connectTimeout = 3000;
    protected int readTimeout = 5000;
    protected boolean cache = false;
    protected boolean followRedirects = false;
    protected boolean ensureSuccess = false;
    protected boolean gzip = false;
    protected boolean keepalive = true;
    protected long ifModifiedSince;
    protected String url;
    protected Map<String, Object> headers;
    protected Map<String, Object> params;
    protected Object body;
    protected SSLSocketFactory sslSocketFactory;
    protected HostnameVerifier hostnameVerifier;
    protected ChunkedHandler chunkedHandler;
    
    
    protected final Method method;
    public AbstractHttpRequestBuilder(Method method, String url) {
        this.method = method;
        this.url = url;
    }
    
    @Override
    public HttpRequestBuilder header(String key, Object value) {
        if (headers == null) {
            headers = new LinkedHashMap<String, Object>();
        }
        headers.put(key, value);
        return this;
    }

    @Override
    public HttpRequestBuilder param(String key, String value) {
        if (params == null) {
            params = new LinkedHashMap<String, Object>();
        }
        params.put(key, value);
        return this;
    }

    @Override
    public HttpRequestBuilder paramIf(String key, String value, boolean condition) {
        if(condition){
            return param(key, value);
        }
        return this;
    }
    
    @Override
    public HttpRequestBuilder basicAuthentication(String username, String password) {
        String authValue = Bytes.bytes2base64((username + ":" + password).getBytes());
        return header(Const.HDR_AUTHORIZATION, Const.AUTHORIZATION_BASIC + " " + authValue);
    }

    @Override
    public HttpRequestBuilder tokenAuthentication(String token){
        return header(Const.HDR_AUTHORIZATION, Const.AUTHORIZATION_TOKEN + " " + token);
    }
    
    @Override
    public HttpRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public HttpRequestBuilder readTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    @Override
    public HttpRequestBuilder connectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    @Override
    public HttpRequestBuilder gzip(boolean gzip) {
        this.gzip = gzip;
        return this;
    }

    @Override
    public HttpRequestBuilder keepalive(boolean keepalive) {
        this.keepalive = keepalive;
        return this;
    }
    
    @Override
    public HttpRequestBuilder cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public HttpRequestBuilder ifModifiedSince(long ifModifiedSince) {
        this.ifModifiedSince = ifModifiedSince;
        return this;
    }

    @Override
    public HttpRequestBuilder followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }
    
    @Override
    public HttpRequestBuilder setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    @Override
    public HttpRequestBuilder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }
    
    @Override
    public HttpRequestBuilder setChunkedHandler(ChunkedHandler handler){
        this.chunkedHandler = handler;
        return this;
    }
    
    
    @Override
    public HttpResponse watch() throws KubboHttpException {
        readTimeout(-1);
        return execute();
    }

    @Override
    public HttpResponse execute() throws KubboHttpException {
        header(Const.HDR_USER_AGENT, Const.DEFAULT_USER_AGENT);
        HttpRequest request = build();
        try {
            return request.execute();
        } catch (IOException e) {
            throw new KubboHttpException(e);
        }
    }

    @Override
    public <T> T watch(Class<T> asType) throws KubboHttpException {
        readTimeout(-1);
        return execute(asType);
    }

    @Override
    public <T> T execute(Class<T> asType) throws KubboHttpException {
        header(Const.HDR_USER_AGENT, Const.DEFAULT_USER_AGENT);
        HttpRequest request = build();
        try {
            HttpResponse response = request.execute();
            return response.asType(asType);
        } catch (IOException e) {
            throw new KubboHttpException(e);
        }
    }
    
    @Override
    public <T> void watch(Class<T> asType, Watcher<T> watcher) {
        readTimeout(-1);
        try{
            HttpResponse response = execute();
            response.success();
            if(response.isChunked() && chunkedHandler != null){
                chunkedHandler.read(response.getInputStream(), asType, watcher);
            } else{
                watcher.received(response.asType(asType));
            }
        } catch (KubboHttpException e) {
            watcher.exceptionCaught(e);
        }
    }
    
    protected boolean hasParams(){
        return this.params != null && !this.params.isEmpty();
    }
    protected boolean hasHeaders(){
        return this.headers != null && !this.headers.isEmpty();
    }
    protected boolean hasBody(){
        return this.body != null;
    }
    protected boolean isStreamBody(){
        return this.body != null && (this.body instanceof File || this.body instanceof InputStream);
    }
        
    @Override
    public HttpRequest build() {
        return new HttpRequest(){
            @Override
            public HttpResponse execute() throws IOException {
                return doExecute();
            }
        };			
    }
    
    protected abstract HttpResponse doExecute() throws IOException;

}

