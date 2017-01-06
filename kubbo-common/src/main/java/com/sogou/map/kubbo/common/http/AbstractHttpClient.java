/**
 * 
 */
package com.sogou.map.kubbo.common.http;

/**
 * @author liufuliang
 *
 */
public abstract class AbstractHttpClient implements HttpClient{
    /**
     * Build an HTTP GET request
     *
     * @return a request builder
     */
    @Override
    public HttpRequestBuilder get(String url) {
        return request(Method.GET, url);
    }

    /**
     * Build an HTTP HEAD request Spi
     *
     * @return a request builder
     */
    @Override
    public HttpRequestBuilder head(String url) {
        return request(Method.HEAD, url);
    }

    /**
     * Build an HTTP PUT request
     *
     * @return a request builder
     */
    @Override
    public HttpRequestBuilder put(String url) {
        return request(Method.PUT, url);
    }

    /**
     * Build an HTTP POST request
     *
     * @return a request builder
     */
    @Override
    public HttpRequestBuilder post(String url) {
        return request(Method.POST, url);
    }

    /**
     * Build an HTTP DELETE request
     *
     * @return a request builder
     */
    @Override
    public HttpRequestBuilder delete(String url) {
        return request(Method.DELETE, url);
    }

    /**
     * Build an HTTP OPTIONS request
     *
     * @return a request builder
     */
    @Override
    public HttpRequestBuilder options(String url) {
        return request(Method.OPTIONS, url);
    }
    
    /**
     * 
     * @param method
     * @param url
     * @return a request builder
     */
    public HttpRequestBuilder request(Method method, String url) {
        if (method == null) {
            throw new IllegalArgumentException("method == NULL");
        }
        if (url == null) {
            throw new IllegalArgumentException("url == NULL");
        }
        return doReqest(method, url);
    }
    
    public abstract HttpRequestBuilder doReqest(Method method, String url);
}
