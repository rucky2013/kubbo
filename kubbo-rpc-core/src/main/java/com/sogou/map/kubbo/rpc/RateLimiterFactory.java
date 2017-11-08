/**
 * 
 */
package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.extension.SPI;
import com.sogou.map.kubbo.rpc.limiter.TokenBucketRateLimiterFactory;

/**
 * @author liufuliang
 *
 */

@SPI(TokenBucketRateLimiterFactory.name)
public interface RateLimiterFactory {
    RateLimiter create(int interval, int rate);
}
