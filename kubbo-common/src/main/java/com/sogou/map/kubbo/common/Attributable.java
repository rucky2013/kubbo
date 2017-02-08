/**
 * 
 */
package com.sogou.map.kubbo.common;

/**
 * @author liufuliang
 *
 */
public interface Attributable {
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
    void setAttribute(String key,Object value);
    
    /**
     * get attribute.
     * 
     * @param key key.
     * @return value.
     */
    Object getAttribute(String key);
    
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
