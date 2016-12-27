package com.sogou.map.kubbo.common.utils;

/**
 * Helper Class for hold a value.
 *
 * @author liufuliang
 */
public class Holder<T> {
    
    private volatile T value;
    
    public void set(T value) {
        this.value = value;
    }
    
    public T get() {
        return value;
    }

}