/**
 * 
 */
package com.sogou.map.kubbo.rpc.limiter;

import java.util.concurrent.atomic.AtomicInteger;

import com.sogou.map.kubbo.rpc.RateLimiter;

/**
 * @author liufuliang
 *
 */
public class TokenBucketRateLimiter implements RateLimiter {
        
    private int rate;                  //令牌放入速度
    private int interval;              //间隔
    private AtomicInteger tokens;      //当前水量
    private long timeStamp;            //时间戳

    
    public TokenBucketRateLimiter(int interval, int rate) {
        this.interval = interval;
        this.rate = rate;
        this.tokens = new AtomicInteger(rate);
        this.timeStamp = System.currentTimeMillis();
    }
    
    @Override
    public boolean acquire() {
        long now = System.currentTimeMillis();
        if (now > timeStamp + interval) {
            tokens.set(rate);
            timeStamp = now;
        }

        int value = tokens.get();
        boolean flag = false;
        while (value > 0 && !flag) {
            flag = tokens.compareAndSet(value, value - 1);
            value = tokens.get();
        }

        return flag;
    }
    
    
}
