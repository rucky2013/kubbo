package com.sogou.map.kubbo.metrics.influxdb;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.metrics.Counter;
import com.sogou.map.kubbo.metrics.FrequencyElapsedRecorder;
import com.sogou.map.kubbo.metrics.Metric;
import com.sogou.map.kubbo.metrics.MetricRegistry;
import com.sogou.map.kubbo.metrics.ScheduledReporter;
import com.sogou.map.kubbo.metrics.influxdb.client.InfluxDB;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.BatchPoints;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.Point;


public final class InfluxdbReporter extends ScheduledReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxdbReporter.class);
    
    private final InfluxDB influxdb;
    private Map<String, String> tags;

    
    private InfluxdbReporter(MetricRegistry registry, InfluxDB influxDb, Map<String, String> tags,
                             TimeUnit rateUnit, TimeUnit durationUnit) {
        super(registry, "influxdb-reporter", rateUnit, durationUnit);
        this.influxdb = influxDb;
        this.tags = tags;

    }

    public static Builder registry(MetricRegistry registry) {
        return new Builder(registry);
    }
    
    
    public static final class Builder {
        private final MetricRegistry registry;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private Map<String, String> tags;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.tags = new TreeMap<String, String>();
        }

        public Builder tags(Map<String, String> tags) {
            this.tags.putAll(tags);
            return this;
        }
        
        public Builder tag(String key, String value) {
            this.tags.put(key, value);
            return this;
        }
        
        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit a unit of time
         * @return {@code this}
         */
        public Builder rateUnit(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit a unit of time
         * @return {@code this}
         */
        public Builder durationUnit(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public InfluxdbReporter build(InfluxDB influxDb) {
            return new InfluxdbReporter(registry, influxDb, tags, rateUnit, durationUnit);
        }
    }

    @Override
    public void report(MetricRegistry registry) {
        try {
            long now = System.currentTimeMillis();
            BatchPoints.Builder batch = BatchPoints.builder();
            Map<String, Metric> metrics = registry.getMetrics();
            for(Map.Entry<String, Metric> entry : metrics.entrySet()){
                Metric metric = entry.getValue();
                Point point = null;
                if(metric instanceof FrequencyElapsedRecorder){ //FrequencyElapsedRecorder
                    point = buildFrequencyElapsedRecorderPoint(now, (FrequencyElapsedRecorder)metric);
                } else if(metric instanceof Counter){ //Counter
                    point = buildCounterPoint(now,  (Counter)metric);
                }
                // add point
                if(point != null){
                    batch.point(point);
                }
            }
            
            // write to db
            BatchPoints points = batch.build();
            if(!points.isEmpty()){
                influxdb.write(points);
            }

        } catch (Exception e) {
            LOGGER.warn("Unable to report to InfluxDB. Discarding data.", e);
        }
    }
    
    private Point buildFrequencyElapsedRecorderPoint(long now, FrequencyElapsedRecorder recorder){
        long frequency = recorder.frequency();
        long elapsed = recorder.elapsed();
        if(recorder.frequency() == 0){
            return null;
        }
        String measurement = (String) recorder.getAttribute(Metric.MEASUREMENT_KEY);
        String tagMethod = (String) recorder.getAttribute(Metric.TAG_METHOD);
        
        Point point = Point.measurement(measurement)
            .tags(tags)
            .tag(Metric.TAG_METHOD, tagMethod)
            .time(now, TimeUnit.MILLISECONDS)
            .field("frequency", frequency)
            .field("elapsed", elapsed)
            .build();
        //clear
        recorder.clear(frequency, elapsed);
        return point;
    }
    private Point buildCounterPoint(long now, Counter counter){
        if(counter.count() == 0){
            return null;
        }
        String measurement = (String) counter.getAttribute(Metric.MEASUREMENT_KEY);
        String tagMethod = (String) counter.getAttribute(Metric.TAG_METHOD);
        
        Point point = Point.measurement(measurement)
            .tags(tags)
            .tag(Metric.TAG_METHOD, tagMethod)
            .time(now, TimeUnit.MILLISECONDS)
            .field("value", counter.take())
            .build();
        return point;
    }

}
