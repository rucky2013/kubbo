package com.sogou.map.kubbo.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A registry of metric instances.
 * 
 * @author liufuliang
 */
public class MetricRegistry implements MetricSet {
    private final ConcurrentMap<String, Metric> metrics = new ConcurrentHashMap<String, Metric>();

    public MetricRegistry() {
    }

    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        if (metric instanceof MetricSet) {
            registerAll(name, (MetricSet) metric);
        } else {
            final Metric existing = metrics.putIfAbsent(name, metric);
            if (existing != null) {
                throw new IllegalArgumentException("A metric named " + name + " already exists");
            }
        }
        return metric;
    }

    public void registerAll(MetricSet metrics) throws IllegalArgumentException {
        registerAll(null, metrics);
    }

    private void registerAll(String prefix, MetricSet metrics) throws IllegalArgumentException {
        for (Map.Entry<String, Metric> entry : metrics.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(name(prefix, entry.getKey()), (MetricSet) entry.getValue());
            } else {
                register(name(prefix, entry.getKey()), entry.getValue());
            }
        }
    }

    private static String name(String name, String... names) {
        final StringBuilder builder = new StringBuilder();
        append(builder, name);
        if (names != null) {
            for (String s : names) {
                append(builder, s);
            }
        }
        return builder.toString();
    }

    private static void append(StringBuilder builder, String part) {
        if (part != null && !part.isEmpty()) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(part);
        }
    }

    public Counter counter(String name) {
        return getOrAdd(name, MetricBuilder.COUNTERS);
    }
    
    public FrequencyElapsedRecorder frequencyElapsedRecorder(String name) {
        return getOrAdd(name, MetricBuilder.REQUENCY_ELAPSED);
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> T getOrAdd(String name, MetricBuilder<T> builder) {
        final Metric metric = metrics.get(name);
        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                return register(name, builder.newMetric());
            } catch (IllegalArgumentException e) {
                final Metric added = metrics.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    public boolean remove(String name) {
        final Metric metric = metrics.remove(name);
        if (metric != null) {
            return true;
        }
        return false;
    }

    public SortedSet<String> getNames() {
        return Collections.unmodifiableSortedSet(new TreeSet<String>(metrics.keySet()));
    }

    public SortedMap<String, Counter> getCounters() {
        return getMetrics(Counter.class);
    }
    
    public SortedMap<String, FrequencyElapsedRecorder> getFrequencyElapsedRecorders() {
        return getMetrics(FrequencyElapsedRecorder.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> SortedMap<String, T> getMetrics(Class<T> clazz) {
        final TreeMap<String, T> timers = new TreeMap<String, T>();
        for (Map.Entry<String, Metric> entry : metrics.entrySet()) {
            if (clazz.isInstance(entry.getValue())) {
                timers.put(entry.getKey(), (T) entry.getValue());
            }
        }
        return Collections.unmodifiableSortedMap(timers);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    /**
     * A quick and easy way of capturing the notion of default metrics.
     */
    private interface MetricBuilder<T extends Metric> {
        MetricBuilder<Counter> COUNTERS = new MetricBuilder<Counter>() {
            @Override
            public Counter newMetric() {
                return new Counter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Counter.class.isInstance(metric);
            }
        };
        
        MetricBuilder<FrequencyElapsedRecorder>  REQUENCY_ELAPSED = new MetricBuilder<FrequencyElapsedRecorder>() {
            @Override
            public FrequencyElapsedRecorder newMetric() {
                return new FrequencyElapsedRecorder();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return FrequencyElapsedRecorder.class.isInstance(metric);
            }
        };

        T newMetric();

        boolean isInstance(Metric metric);
    }
}
