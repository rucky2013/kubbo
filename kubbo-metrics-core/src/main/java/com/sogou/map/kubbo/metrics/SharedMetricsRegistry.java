/**
 * 
 */
package com.sogou.map.kubbo.metrics;

import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.SystemPropertyUtils;

/**
 * @author liufuliang
 *
 */
public class SharedMetricsRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SharedMetricsRegistry.class);
    private static final String metricsAddress = SystemPropertyUtils.get(Constants.METRICS_ADDRESS_SYSTEM_PROPERTY, "");

    private SharedMetricsRegistry() {}
    
    public static class SingletonHolder {
        private static MetricRegistry instance = new MetricRegistry();
        static {
            initReporter(instance);
        }
    }

    public static MetricRegistry instance() {
        MetricRegistry registry = SingletonHolder.instance;
        return registry;
    }
    
    public static boolean isReportEnabled(){
        return !metricsAddress.isEmpty();
    }

    public static ReporterFactory getReporterFactory(URL url) {
        String type = url.getParameter(Constants.METRICS_KEY, Constants.DEFAULT_METRICES);
        return getReporterFactory(type);
    }

    public static ReporterFactory getReporterFactory(String type) {
        return ExtensionLoader.getExtensionLoader(ReporterFactory.class).getExtension(type);
    }

    private static void initReporter(MetricRegistry registry){
        if(metricsAddress.isEmpty()){
            return;
        }
        /*
         * create reporter
         */
        URL metrics = URL.valueOf(metricsAddress);
        ReporterFactory reporterFactory = getReporterFactory(metrics);
        Reporter reporter = reporterFactory.create(registry, metrics);
        
        /*
         *  start reporter
         */
        int interval = metrics.getParameter(Constants.METRICS_INTERVAL_KEY, Constants.DEFAULT_METRICS_INTERVAL);
        // Calculating next minute.
        long millis = System.currentTimeMillis();
        long nextMinute = ((millis / 60000) + 1) * 60000;
        long delay = (nextMinute - System.currentTimeMillis());
        reporter.start(delay, interval, TimeUnit.MILLISECONDS);
        LOG.info("Metrics report to " + metricsAddress);
    }
}
