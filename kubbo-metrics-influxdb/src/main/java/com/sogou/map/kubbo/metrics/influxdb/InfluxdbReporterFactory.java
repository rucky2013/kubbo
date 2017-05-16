/**
 * 
 */
package com.sogou.map.kubbo.metrics.influxdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.util.NetUtils;
import com.sogou.map.kubbo.metrics.Metric;
import com.sogou.map.kubbo.metrics.MetricRegistry;
import com.sogou.map.kubbo.metrics.Reporter;
import com.sogou.map.kubbo.metrics.ReporterFactory;
import com.sogou.map.kubbo.metrics.influxdb.client.InfluxDB;
import com.sogou.map.kubbo.metrics.influxdb.client.InfluxDBFactory;

/**
 * @author liufuliang
 *
 */
public class InfluxdbReporterFactory implements ReporterFactory {
    public static final String NAME = "influxdb";

    private static final Map<String, Reporter> reporters = new ConcurrentHashMap<String, Reporter>();
    
    @Override
    public Reporter create(MetricRegistry registry, URL address) {
        //singleton
        if(reporters.containsKey(address.getAddress())){
            return reporters.get(address.getAddress());
        }
        InfluxDB influxdb = InfluxDBFactory.connect(address);
        String application = address.getParameter(Constants.APPLICATION_KEY, NetUtils.getHostAddress());        
        InfluxdbReporter reporter = InfluxdbReporter.registry(registry)
                .rateUnit(TimeUnit.SECONDS)
                .durationUnit(TimeUnit.MILLISECONDS)
                .tag(Metric.TAG_APPLICATION, application)
                .tag(Metric.TAG_HOST, NetUtils.getHostName())
                .build(influxdb);
        reporters.put(address.getAddress(), reporter);
        return reporter;
    }

}
