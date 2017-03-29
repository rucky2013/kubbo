package com.sogou.map.kubbo.metrics.influxdb.client.obj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sogou.map.kubbo.metrics.influxdb.client.InfluxDB.ConsistencyLevel;

/**
 *
 * @author liufuliang
 *
 */
public class BatchPoints {
    private String retentionPolicy;
    private Map<String, String> tags;
    private List<Point> points;
    private ConsistencyLevel consistency;

    BatchPoints(){
    }
    
    BatchPoints(String retentionPolicy, 
            Map<String, String> tags, 
            List<Point> points,
            ConsistencyLevel consistency) {
        this.retentionPolicy = retentionPolicy;
        this.tags = tags;
        this.points = points;
        this.consistency = consistency;
    }

    /**
     * Create a new BatchPoints build to create a new BatchPoints in a fluent
     * manner.
     *
     * @param database
     *            the name of the Database
     * @return the Builder to be able to add further Builder calls.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The Builder to create a new BatchPoints instance.
     */
    public static final class Builder {
        private String retentionPolicy;
        private final Map<String, String> tags = new TreeMap<String, String>();
        private final List<Point> points = new ArrayList<Point>();
        private ConsistencyLevel consistency;

        /**
         * @param database
         */
        Builder() {
        }

        /**
         * The retentionPolicy to use.
         *
         * @param policy
         * @return the Builder instance
         */
        public Builder retentionPolicy(String policy) {
            this.retentionPolicy = policy;
            return this;
        }

        /**
         * Add a tag to this set of points.
         *
         * @param tagName
         *            the tag name
         * @param value
         *            the tag value
         * @return the Builder instance.
         */
        public Builder tag(String tagName, String value) {
            this.tags.put(tagName, value);
            return this;
        }

        /**
         * Add a Point to this set of points.
         *
         * @param pointToAdd
         * @return the Builder instance
         */
        public Builder point(Point pointToAdd) {
            this.points.add(pointToAdd);
            return this;
        }

        /**
         * Add a set of Points to this set of points.
         *
         * @param pointsToAdd
         * @return the Builder instance
         */
        public Builder points(Point... pointsToAdd) {
            this.points.addAll(Arrays.asList(pointsToAdd));
            return this;
        }

        /**
         * Set the ConsistencyLevel to use. If not given it defaults to
         * {@link ConsistencyLevel#ONE}
         *
         * @param consistencyLevel
         * @return the Builder instance
         */
        public Builder consistency(ConsistencyLevel consistencyLevel) {
            this.consistency = consistencyLevel;
            return this;
        }
        
        /**
         * Create a new BatchPoints instance.
         *
         * @return the created BatchPoints.
         */
        public BatchPoints build() {
            for (Point point : this.points) {
                point.getTags().putAll(this.tags);
            }
            if (this.consistency == null) {
                this.consistency = ConsistencyLevel.ONE;
            }            
            return new BatchPoints(this.retentionPolicy, this.tags, this.points, this.consistency);
        }
    }

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public List<Point> getPoints() {
        return points;
    }

    public ConsistencyLevel getConsistency() {
        return consistency;
    }
    
    public boolean isEmpty(){
        return points.isEmpty();
    }

    // measurement[,tag=value,tag2=value2...] field=value[,field2=value2...]
    // [unixnano]
    /**
     * calculate the lineprotocol for all Points.
     *
     * @return the String with newLines.
     */
    public String toLineProtocol() {
        StringBuilder sb = new StringBuilder();
        for (Point point : points) {
            sb.append(point.toLineProtocol()).append("\n");
        }
        return sb.toString();
    }
}
