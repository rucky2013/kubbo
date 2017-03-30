/**
 * 
 */
package com.sogou.map.kubbo.common;

import java.util.Map;

/**
 * @author liufuliang
 *
 */
public class AbstractAttributableDelegate<T> implements Attributable<T>{
    protected transient Attributable<T> attributable;
    
    public AbstractAttributableDelegate() {
    }

    public AbstractAttributableDelegate(Attributable<T> attributable) {
        this.attributable = attributable;
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributable.hasAttribute(key);
    }
    
    @Override
    public T getAttribute(String key) {
        return attributable.getAttribute(key);
    }

    @Override
    public T getAttribute(String key, T defaultValue) {
        return attributable.getAttribute(key, defaultValue);
    }

    @Override
    public Map<String, T> getAttributes() {
        return attributable.getAttributes();
    }
    
    @Override
    public void setAttribute(String key, T value) {
        attributable.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        attributable.removeAttribute(key);
    }
    
    @Override
    public void removeAttributes(){
        attributable.removeAttributes();
    }

}
