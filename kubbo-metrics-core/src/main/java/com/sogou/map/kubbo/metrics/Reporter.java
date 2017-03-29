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
    void start(long period, TimeUnit unit);
    void stop();
    void report();
}
