/**
 * 
 */
package com.sogou.map.kubbo.metrics.influxdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.utils.NetUtils;
import com.sogou.map.kubbo.metrics.Metric;
import com.sogou.map.kubbo.metrics.MetricRegistry;
import com.sogou.map.kubbo.metrics.Reporter;
import com.sogou.map.kubbo.metrics.ReporterFactory;
import com.sogou.map.kubbo.metrics.SharedMetricsRegistry;
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
    public Reporter create(URL address) {
        //singleton
        if(reporters.containsKey(address.getAddress())){
            return reporters.get(address.getAddress());
        }
        MetricRegistry registry = SharedMetricsRegistry.instance();
        InfluxDB influxdb = InfluxDBFactory.connect(address);
        
        String application = address.getParameter(Constants.METRICS_APPLICATION_KEY, NetUtils.getHostName());
        int interval = address.getParameter(Constants.METRICS_INTERVAL_KEY, Constants.DEFAULT_METRICS_INTERVAL);
        
        InfluxdbReporter reporter = InfluxdbReporter.registry(registry)
                .rateUnit(TimeUnit.SECONDS)
                .durationUnit(TimeUnit.MILLISECONDS)
                .tag(Metric.TAG_APPLICATION, application)
                .tag(Metric.TAG_HOST, NetUtils.getHostName())
                .build(influxdb);
        reporter.start(interval, TimeUnit.MILLISECONDS);
        reporters.put(address.getAddress(), reporter);
        return reporter;
    }

}
