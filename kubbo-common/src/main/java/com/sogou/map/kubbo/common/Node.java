package com.sogou.map.kubbo.common;


/**
 * Node
 * 
 * @author liufuliang
 */
public interface Node {

    /**
     * get url.
     * 
     * @return url.
     */
    URL getUrl();
    
    /**
     * is available.
     * 
     * @return available.
     */
    boolean isAvailable();

    /**
     * destroy.
     */
    void destroy();

}