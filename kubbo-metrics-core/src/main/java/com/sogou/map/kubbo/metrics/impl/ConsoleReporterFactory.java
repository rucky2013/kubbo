/**
 * 
 */
package com.sogou.map.kubbo.metrics.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.metrics.MetricRegistry;
import com.sogou.map.kubbo.metrics.Reporter;
import com.sogou.map.kubbo.metrics.ReporterFactory;

/**
 * @author liufuliang
 *
 */
public class ConsoleReporterFactory implements ReporterFactory {
    public static final String NAME = "console";

    private static final Map<String, Reporter> reporters = new ConcurrentHashMap<String, Reporter>();
    
    @Override
    public Reporter create(MetricRegistry registry, URL address) {
        //singleton
        if(reporters.containsKey(address.getAddress())){
            return reporters.get(address.getAddress());
        }
        
        ConsoleReporter reporter = ConsoleReporter.registry(registry)
                .rateUnit(TimeUnit.SECONDS)
                .durationUnit(TimeUnit.MILLISECONDS)
                .build();
        reporters.put(address.getAddress(), reporter);
        return reporter;
    }

}
