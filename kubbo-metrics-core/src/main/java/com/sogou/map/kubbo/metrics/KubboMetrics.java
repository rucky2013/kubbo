/**
 * 
 */
package com.sogou.map.kubbo.metrics;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @author liufuliang
 *
 */
public class KubboMetrics {
    private static final ConcurrentHashMap<String, FrequencyElapsedRecorder> RECORDERS = new ConcurrentHashMap<String, FrequencyElapsedRecorder>();

    private KubboMetrics(){}
    
    /**
     * frequency & elapsed time metrics utility
     * @param method method 
     * @param time elapsed time
     */
    public static void elapse(String method, long time) {
        if(!SharedMetricsRegistry.isReportEnabled()){
            return;
        }
        FrequencyElapsedRecorder recorder = RECORDERS.get(method);
        if (recorder == null) {
            FrequencyElapsedRecorder rec = SharedMetricsRegistry.instance().frequencyElapsedRecorder(method);
            rec.setAttribute(Metric.MEASUREMENT_KEY, Metric.FREQUENCY_ELAPSED);
            rec.setAttribute(Metric.TAG_METHOD, method);
            RECORDERS.putIfAbsent(method, rec);
            recorder = RECORDERS.get(method);
        }
        recorder.mark(time);
    }
}
