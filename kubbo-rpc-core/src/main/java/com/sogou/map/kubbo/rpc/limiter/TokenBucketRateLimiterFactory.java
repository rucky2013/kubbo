/**
 * 
 */
package com.sogou.map.kubbo.rpc.limiter;

import com.sogou.map.kubbo.rpc.RateLimiter;
import com.sogou.map.kubbo.rpc.RateLimiterFactory;

/**
 * @author liufuliang
 *
 */
public class TokenBucketRateLimiterFactory implements RateLimiterFactory {

    public static final String name = "tokenbucket";
    
    @Override
    public RateLimiter create(int interval, int rate) {
        return new TokenBucketRateLimiter(interval, rate);
    }
}
