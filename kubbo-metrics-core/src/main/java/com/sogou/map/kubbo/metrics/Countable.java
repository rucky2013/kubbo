/**
 * 
 */
package com.sogou.map.kubbo.metrics;

/**
 * @author liufuliang
 *
 */
public interface Countable {
    /**
     * Returns the current count.
     *
     * @return the current count
     */
    long count();
    
    
    /**
     * Returns the current count then dec.
     *
     * @return the current count
     */
    long take();
    
    
    /**
     * Rreset
     *
     */
    void reset();
}
