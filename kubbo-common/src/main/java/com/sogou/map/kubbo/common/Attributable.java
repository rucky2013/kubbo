/**
 * 
 */
package com.sogou.map.kubbo.common;

import java.util.Map;

/**
 * @author liufuliang
 *
 */
public interface Attributable<T> {
    /**
     * has attribute.
     * 
     * @param key key.
     * @return has or has not.
     */
    boolean hasAttribute(String key);

    /**
     * set attribute.
     * 
     * @param key key.
     * @param value value.
     */
    void setAttribute(String key, T value);
    
    /**
     * get attribute.
     * 
     * @param key key.
     * @return value.
     */
    T getAttribute(String key);
    
    
    /**
     * get attribute.
     * 
     * @param key key.
     * @param defaultValue default value
     * @return value.
     */
    T getAttribute(String key, T defaultValue);
    
    
    /**
     * get attributes.
     *
     * @return attributes.
     */
    Map<String, T> getAttributes();
    
    /**
     * remove attribute.
     * 
     * @param key key.
     */
    void removeAttribute(String key);
    
    /**
     * remove attribute.
     */
    void removeAttributes();
}
