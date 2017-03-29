package com.sogou.map.kubbo.metrics.influxdb.client.obj;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Represents a Query against Influxdb.
 *
 * @author liufuliang
 *
 */
public class Query {

    private final String command;
    private final String database;


    /**
     * @param command
     * @param database
     * @param requiresPost
     *            true if the command requires a POST instead of GET to influxdb
     */
    public Query(String command, String database) {
        this.command = command;
        this.database = database;
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @return url encoded command
     */
    public String getEncodedCommand() {
        return encode(this.command);
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return this.database;
    }


    /**
     * Encode a command into {@code x-www-form-urlencoded} format.
     * 
     * @param command
     *            the command to be encoded.
     * @return a encoded command.
     */
    public static String encode(final String command) {
        try {
            return URLEncoder.encode(command, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
