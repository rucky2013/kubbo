/**
 * 
 */
package com.sogou.map.kubbo.metrics;

/**
 * @author liufuliang
 *
 */
public class SharedMetricsRegistry {
    private static MetricRegistry INSTANCE = new MetricRegistry();
    
    public static MetricRegistry instance(){
        return INSTANCE;
    }
    
    private SharedMetricsRegistry(){}
}
