/**
 * 
 */
package com.sogou.map.kubbo.trace;

import java.io.Closeable;


/**
 * @author liufuliang
 *
 */
public interface Trace extends Closeable {
    void finish();
    
    public static final Trace NOOP = new Trace(){
        @Override
        public void finish() {
        }

        @Override
        public void close() {
        }
    };
}
