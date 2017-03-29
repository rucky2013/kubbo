/**
 * 
 */
package com.sogou.map.kubbo.metrics;

import java.util.Map;

/**
 * @author liufuliang
 *
 */
public interface MetricSet extends Metric {
    /**
     * A map of metric names to metrics.
     *
     * @return the metrics
     */
    Map<String, Metric> getMetrics();
}
