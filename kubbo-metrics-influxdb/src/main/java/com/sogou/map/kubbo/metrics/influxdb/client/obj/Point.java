package com.sogou.map.kubbo.metrics.influxdb.client.obj;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.escape.Escaper;
import com.sogou.map.kubbo.common.escape.Escapers;


/**
 * Representation of a InfluxDB database Point.
 *
 * @author liufuliang
 *
 */
public class Point {
    private String measurement;
    private Map<String, String> tags;
    private Long time;
    private TimeUnit precision = TimeUnit.NANOSECONDS;
    private Map<String, Object> fields;

    private static final int MAX_FRACTION_DIGITS = 340;

    private static final Escaper FIELD_ESCAPER = Escapers.builder()
            .addEscape('\\', "\\\\")
            .addEscape('"', "\\\"")
            .build();
    private static final Escaper KEY_ESCAPER = Escapers.builder()
           .addEscape(' ', "\\ ")
           .addEscape(',', "\\,")
           .addEscape('=', "\\=")
           .build();
    
    Point(){
    }
    
    Point(String measurement, 
            Map<String, String> tags, 
            Long time, 
            TimeUnit precision, 
            Map<String, Object> fields) {
        this.measurement = measurement;
        this.tags = tags;
        this.time = time;
        this.precision = precision;
        this.fields = fields;
    }

    /**
     * Create a new Point Build build to create a new Point in a fluent manner.
     *
     * @param measurement
     *            the name of the measurement.
     * @return the Builder to be able to add further Builder calls.
     */

    public static Builder measurement(final String measurement) {
        return new Builder(measurement);
    }

    /**
     * Builder for a new Point.
     *
     * @author liufuliang
     *
     */
    public static final class Builder {
        private final String measurement;
        private final Map<String, String> tags = new TreeMap<String, String>();
        private Long time;
        private TimeUnit precision = TimeUnit.MICROSECONDS;
        private final Map<String, Object> fields = new TreeMap<String, Object>();

        /**
         * @param measurement
         */
        Builder(final String measurement) {
            this.measurement = measurement;
        }

        /**
         * Add a tag to this point.
         *
         * @param tagName
         *            the tag name
         * @param value
         *            the tag value
         * @return the Builder instance.
         */
        public Builder tag(final String tagName, final String value) {
            if (tagName != null && !tagName.isEmpty() && value != null && !value.isEmpty()) {
                tags.put(tagName, value);
            }
            return this;
        }

        /**
         * Add a Map of tags to add to this point.
         *
         * @param tagsToAdd
         *            the Map of tags to add
         * @return the Builder instance.
         */
        public Builder tags(final Map<String, String> tagsToAdd) {
            this.tags.putAll(tagsToAdd);
            return this;
        }

        public Builder field(final String field, final boolean value) {
            fields.put(field, value);
            return this;
        }

        public Builder field(final String field, final long value) {
            fields.put(field, value);
            return this;
        }

        public Builder field(final String field, final double value) {
            fields.put(field, value);
            return this;
        }

        public Builder field(final String field, final Number value) {
            fields.put(field, value);
            return this;
        }

        public Builder field(final String field, final String value) {
            if (value == null) {
                throw new IllegalArgumentException("value == null");
            }
            fields.put(field, value);
            return this;
        }

        /**
         * Add a Map of fields to this point.
         *
         * @param fieldsToAdd
         *            the fields to add
         * @return the Builder instance.
         */
        public Builder fields(final Map<String, Object> fieldsToAdd) {
            this.fields.putAll(fieldsToAdd);
            return this;
        }

        /**
         * Add a time to this point.
         *
         * @param time time
         * @param precision precision
         * @return the Builder instance.
         */
        public Builder time(final long time, final TimeUnit precision) {
            if (precision == null) {
                throw new IllegalArgumentException("precision == null");
            }
            this.time = time;
            this.precision = precision;
            return this;
        }

        /**
         * Create a new Point.
         *
         * @return the newly created Point.
         */
        public Point build() {
            if (this.fields.size() == 0) {
                throw new IllegalArgumentException("fields Empty");
            }
            if(this.time == null){
                this.time = System.currentTimeMillis();
                this.precision = TimeUnit.MILLISECONDS;
            }
            return new Point(this.measurement, this.tags, this.time, this.precision, this.fields);
        }
    }

    
    
    
    public String getMeasurement() {
        return measurement;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Long getTime() {
        return time;
    }

    public TimeUnit getPrecision() {
        return precision;
    }

    public Map<String, Object> getFields() {
        return fields;
    }
    
    /**
     * calculate the lineprotocol entry for a single Point.
     *
     * Documentation is WIP : https://github.com/influxdb/influxdb/pull/2997
     *
     * https://github.com/influxdb/influxdb/blob/master/tsdb/README.md
     *
     * @return the String without newLine.
     */
    public String toLineProtocol() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.measurement);
        sb.append(concatenatedTags());
        sb.append(concatenateFields());
        sb.append(concatenatedTime());
        return sb.toString();
    }

    private StringBuilder concatenatedTags() {
        final StringBuilder sb = new StringBuilder();
        for (Entry<String, String> tag : this.tags.entrySet()) {
            String key = KEY_ESCAPER.escape(tag.getKey());
            String value = KEY_ESCAPER.escape(tag.getValue());
            sb.append(",").append(key).append("=").append(value);
        }
        sb.append(" ");
        return sb;
    }

    private StringBuilder concatenateFields() {
        final StringBuilder sb = new StringBuilder();
        final int fieldCount = this.fields.size();
        int loops = 0;

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(1);

        for (Entry<String, Object> field : this.fields.entrySet()) {
            loops++;
            Object value = field.getValue();
            if (value == null) {
                continue;
            }

            sb.append(KEY_ESCAPER.escape(field.getKey())).append("=");
            if (value instanceof String) {
                sb.append("\"").append(FIELD_ESCAPER.escape((String)value)).append("\"");
            } else if (value instanceof Number) {
                if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
                    sb.append(numberFormat.format(value));
                } else {
                    sb.append(value).append("i");
                }
            } else {
                sb.append(value);
            }

            if (loops < fieldCount) {
                sb.append(",");
            }
        }

        return sb;
    }

    private StringBuilder concatenatedTime() {
        final StringBuilder sb = new StringBuilder();
        sb.append(" ").append(TimeUnit.NANOSECONDS.convert(this.time, this.precision));
        return sb;
    }

}
