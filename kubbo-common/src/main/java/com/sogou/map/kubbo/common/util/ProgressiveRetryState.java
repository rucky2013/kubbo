/**
 * 
 */
package com.sogou.map.kubbo.common.util;

/**
 * @author liufuliang
 *
 */
public class ProgressiveRetryState {
    public static final int RETRY_MAX_INTERVAL = 60000; //1MINUTE
    public static final int RETRY_MIN_INTERVAL = 3000; //3SECOND
    int retryInterval = 0; //SECOND

    /**
     *  scale the interval
     * @return next interval
     */
    public int scale(){
        if(retryInterval < RETRY_MIN_INTERVAL){
            retryInterval = RETRY_MIN_INTERVAL;
            return retryInterval;
        }
        if(retryInterval >= RETRY_MAX_INTERVAL){
            return RETRY_MAX_INTERVAL;
        }
        retryInterval *= 2;
        return retryInterval;
    }
        
    public void reset(){
        retryInterval = 0;
    }
    
    public int interval(){
        return this.retryInterval;
    }
}
