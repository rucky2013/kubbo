/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.io.InputStream;

/**
 * @author liufuliang
 *
 */
public interface HttpResponse {

    public int getStatusCode();

    /**
     * Was the request successful (returning a 2xx status code)?
     * 
     * @return <code>true</code> when status code is between 200 and 299, else
     *         <code>false</code>
     */
    public boolean isSuccess();

    /**
     * 
     * @return isChunked
     */
    boolean isChunked();
    
    
    InputStream getInputStream();
    
    /**
     * Returns the text explaining the status code.
     * 
     * @return e.g. "Moved Permanently", "Created", ...
     */
    public String getResponseMessage();

    /**
     * Returns the MIME-type of the response body. <br>
     *
     * @return e.g. "application/json", "text/plain", ...
     */
    public String getContentType();

    
    /**
     * Returns the value of the expires header field. <br>
     *
     * @return the expiration date of the resource, or 0 if not known.
     */
    public long getExpiration();

    /**
     * Returns the value of the last-modified header field. <br>
     *
     * @return the date the resource was last modified, or 0 if not known.
     */
    public long getLastModified();

    public String getHeader(String headerName);

    /**
     * 
     */
    public void setAutoDisconnect();
    
    /**
     * A shortcut to check for successful status codes and throw exception in
     * case of non-2xx status codes. <br>
     * there might be cases where you want to inspect the response-object
     * first (check header values) and then have a short exit where the
     * response-code is not suitable for further normal processing.
     */
    public HttpResponse success() throws KubboHttpException;
    
    public <T> T asType(Class<T> type) throws KubboHttpException;

}
