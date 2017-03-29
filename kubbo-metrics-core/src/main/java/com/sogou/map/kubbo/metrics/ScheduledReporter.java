package com.sogou.map.kubbo.metrics;


import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.ExecutorUtils;
import com.sogou.map.kubbo.common.utils.NamedThreadFactory;

/**
 * abstract scheduled reporters 
 * @author liufuliang
 */
public abstract class ScheduledReporter implements Reporter {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledReporter.class);
    
    private final MetricRegistry registry;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduledFuture;
    
//    private final double durationFactor;
//    private final String durationUnit;
//    private final double rateFactor;
//    private final String rateUnit;

    protected ScheduledReporter(MetricRegistry registry,
                                String name,
                                TimeUnit rateUnit,
                                TimeUnit durationUnit) {
        this.registry = registry;
        this.executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory(name, true));
//        this.rateFactor = rateUnit.toSeconds(1);
//        this.rateUnit = calculateRateUnit(rateUnit);
//        this.durationFactor = 1.0 / durationUnit.toNanos(1);
//        this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
    }

    /**
     * Starts the reporter polling at the given period.
     *
     * @param period the amount of time between polls
     * @param unit   the unit for {@code period}
     */
    @Override
    public void start(long period, TimeUnit unit) {
       start(period, period, unit);
    }

    /**
     * Starts the reporter polling at the given period.
     *
     * @param initialDelay the time to delay the first execution
     * @param period       the amount of time between polls
     * @param unit         the unit for {@code period}
     */
    synchronized protected void start(long initialDelay, long period, TimeUnit unit) {
      if (this.scheduledFuture != null) {
          throw new IllegalArgumentException("Reporter already started");
      }

      this.scheduledFuture = executor.scheduleAtFixedRate(new Runnable() {
         @Override
         public void run() {
             try {
                 report();
             } catch (Exception ex) {
                 LOG.error(ScheduledReporter.this.getClass().getSimpleName(), ex);
             }
         }
      }, initialDelay, period, unit);
    }

    @Override
    public void stop() {
        ExecutorUtils.shutdownGracefully(executor, 1000);
    }

    /**
     * Stops the reporter and shuts down its thread of execution.
     */
    @Override
    public void close() {
        stop();
    }

    /**
     * Report the current values of all metrics in the registry.
     */
    @Override
    public void report() {
        synchronized (this) {
            report(registry);
        }
    }
    
    protected abstract void report(MetricRegistry registry);

    
    
//    protected String getRateUnit() {
//        return rateUnit;
//    }
//
//    protected String getDurationUnit() {
//        return durationUnit;
//    }
//
//    protected double convertDuration(double duration) {
//        return duration * durationFactor;
//    }
//
//    protected double convertRate(double rate) {
//        return rate * rateFactor;
//    }
//
//    private String calculateRateUnit(TimeUnit unit) {
//        final String s = unit.toString().toLowerCase(Locale.US);
//        return s.substring(0, s.length() - 1);
//    }

}
