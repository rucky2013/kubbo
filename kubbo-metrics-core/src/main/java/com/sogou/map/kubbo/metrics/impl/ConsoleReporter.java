package com.sogou.map.kubbo.metrics.impl;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.metrics.Clock;
import com.sogou.map.kubbo.metrics.Counter;
import com.sogou.map.kubbo.metrics.MetricRegistry;
import com.sogou.map.kubbo.metrics.ScheduledReporter;

/**
 * A reporter which outputs measurements to console
 * 
 * @author liufuliang
 */
public class ConsoleReporter extends ScheduledReporter {
    private static final int CONSOLE_WIDTH = 80;

    private final PrintStream output;
    private final Locale locale;
    private final Clock clock;
    private final DateFormat dateFormat;

    private ConsoleReporter(MetricRegistry registry, PrintStream output, Locale locale, Clock clock, TimeZone timeZone,
            TimeUnit rateUnit, TimeUnit durationUnit) {
        super(registry, "kubbo-console-reporter", rateUnit, durationUnit);
        this.output = output;
        this.locale = locale;
        this.clock = clock;
        this.dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        dateFormat.setTimeZone(timeZone);
    }
    
    
    public static Builder registry(MetricRegistry registry) {
        return new Builder(registry);
    }

    /**
     * A builder for {@link ConsoleReporter} instances. Defaults to using the
     * default locale and time zone, writing to {@code System.out}, converting
     * rates to events/second, converting durations to milliseconds, and not
     * filtering metrics.
     */
    public static class Builder {
        private final MetricRegistry registry;
        private PrintStream output;
        private Locale locale;
        private Clock clock;
        private TimeZone timeZone;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.output = System.out;
            this.locale = Locale.getDefault();
            this.clock = Clock.DEFAULT;
            this.timeZone = TimeZone.getDefault();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
        }

        /**
         * Write to the given {@link PrintStream}.
         *
         * @param output
         *            a {@link PrintStream} instance.
         * @return {@code this}
         */
        public Builder output(PrintStream output) {
            this.output = output;
            return this;
        }

        /**
         * Format numbers for the given {@link Locale}.
         *
         * @param locale
         *            a {@link Locale}
         * @return {@code this}
         */
        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Use the given {@link Clock} instance for the time.
         *
         * @param clock
         *            a {@link Clock} instance
         * @return {@code this}
         */
        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        /**
         * Use the given {@link TimeZone} for the time.
         *
         * @param timeZone
         *            a {@link TimeZone}
         * @return {@code this}
         */
        public Builder timeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder rateUnit(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder durationUnit(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public ConsoleReporter build() {
            return new ConsoleReporter(registry, output, locale, clock, timeZone, rateUnit, durationUnit);
        }
    }

    @Override
    public void report(MetricRegistry registry) {
        Map<String, Counter> counters = registry.getCounters();
        final String dateTime = dateFormat.format(new Date(clock.getTime()));
        printWithBanner(dateTime, '=');
        output.println();

        if (!counters.isEmpty()) {
            printWithBanner("-- Counters", '-');
            for (Map.Entry<String, Counter> entry : counters.entrySet()) {
                output.println(entry.getKey());
                printCounter(entry);
            }
            output.println();
        }

        output.println();
        output.flush();
    }

    private void printCounter(Map.Entry<String, Counter> entry) {
        output.printf(locale, "             count = %d%n", entry.getValue().count());
    }

    private void printWithBanner(String s, char c) {
        output.print(s);
        output.print(' ');
        for (int i = 0; i < (CONSOLE_WIDTH - s.length() - 1); i++) {
            output.print(c);
        }
        output.println();
    }

}
