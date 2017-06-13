/**
 * 
 */
package com.sogou.map.kubbo.trace.zipkin;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.NamedThreadFactory;

import brave.sampler.Sampler;

/**
 * @author liufuliang
 *
 */
public class FrequencySampler extends Sampler{
    
    private static final Logger LOG = LoggerFactory.getLogger(FrequencySampler.class);

    public static final String name = "frequency";

    /** 默认采样频率5s一次 */
    public static final int DEFAULT_INTERVALE = 5000;

    /** 采样开关 */
    private AtomicBoolean toggle = new AtomicBoolean(true);
    
    private int interval = DEFAULT_INTERVALE;
    
    private final ScheduledExecutorService executor;
        
    public FrequencySampler(){
        this(DEFAULT_INTERVALE);
    }
    
    public FrequencySampler(int interval){
        this.interval = interval;
        this.executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("kubbo-frequency-trace-sampler", true));
        
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    toggle.set(true);
                } catch (Exception ex) {
                    LOG.error(FrequencySampler.this.getClass().getSimpleName(), ex);
                }
            }
         }, 0, this.interval, TimeUnit.MILLISECONDS);
    }
    
    
    @Override
    public boolean isSampled(long traceId) {
        // 当采样开关开启时, 只有1个请求会成功的把toggle置为false
        if(toggle.get()){
            return toggle.compareAndSet(true, false);
        }
        return false;
    }
    
    @Override 
    public String toString() {
        return "FrequencySample";
      }

}
