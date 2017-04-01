/**
 * 
 */
package com.sogou.map.kubbo.metrics;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.SPI;

/**
 * @author liufuliang
 *
 */

@SPI("influxdb")
public interface ReporterFactory {
    /**
     * 
     * @param address remote collector
     * @return Reporter
     */
    Reporter create(MetricRegistry registry, URL address);
}
