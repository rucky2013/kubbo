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
public class AbstractAttributable<T> implements Attributable<T>{
    private final Map<String, T> attributes = new ConcurrentHashMap<String, T>();
    
    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    @Override
    public T getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public T getAttribute(String key, T defaultValue) {
        if (attributes == null) {
            return defaultValue;
        }
        T value = attributes.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public Map<String, T> getAttributes() {
        return attributes;
    }
    
    @Override
    public void setAttribute(String key, T value) {
        if (value == null) {
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
