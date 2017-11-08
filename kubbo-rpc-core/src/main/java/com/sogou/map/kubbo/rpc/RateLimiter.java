/**
 * 
 */
package com.sogou.map.kubbo.rpc;

/**
 * @author liufuliang
 *
 */

public interface RateLimiter {
    boolean acquire();
    
    public static final RateLimiter AlwaysPermit = new RateLimiter() {
        @Override
        public boolean acquire() {
            return true;
        }
    };
    
    public static final RateLimiter AlwaysReject = new RateLimiter() {
        @Override
        public boolean acquire() {
            return false;
        }
    };
    
}
