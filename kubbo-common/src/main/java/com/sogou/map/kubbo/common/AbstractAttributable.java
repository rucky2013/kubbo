/**
 * 
 */
package com.sogou.map.kubbo.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liufuliang
 *
 */
public class AbstractAttributable implements Attributable{
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if (value == null) { // The null value unallowed in the ConcurrentHashMap.
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    @Override
    public void removeAttributes(){
        attributes.clear();
    }

}
