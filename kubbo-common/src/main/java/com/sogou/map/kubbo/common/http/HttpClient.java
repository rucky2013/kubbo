/**
 * 
 */
package com.sogou.map.kubbo.common.http;

/**
 * @author liufuliang
 *
 */
public interface HttpClient {
    /**
     * Build an HTTP GET request
     *
     * @return a request builder
     */
    public HttpRequestBuilder get(String url);

    /**
     * Build an HTTP HEAD request Spi
     *
     * @return a request builder
     */
    public HttpRequestBuilder head(String url);

    /**
     * Build an HTTP PUT request
     *
     * @return a request builder
     */
    public HttpRequestBuilder put(String url);

    /**
     * Build an HTTP POST request
     *
     * @return a request builder
     */
    public HttpRequestBuilder post(String url);

    /**
     * Build an HTTP DELETE request
     *
     * @return a request builder
     */
    public HttpRequestBuilder delete(String url);

    /**
     * Build an HTTP OPTIONS request
     *
     * @return a request builder
     */
    public HttpRequestBuilder options(String url);
}
