/**
 * 
 */
package com.sogou.map.kubbo.metrics;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * @author liufuliang
 */

public interface Reporter extends Closeable{
    /**
     * Starts the reporter polling at the given period.
     *
     * @param initialDelay the time to delay the first execution
     * @param period       the amount of time between polls
     * @param unit         the unit for {@code period}
     */
    void start(long initialDelay, long period, TimeUnit unit);
    
    /**
     * Starts the reporter polling at the given period.
     *
     * @param period the amount of time between polls
     * @param unit   the unit for {@code period}
     */
    void start(long period, TimeUnit unit);
    
    void stop();
    void report();
}
