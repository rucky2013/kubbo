package com.sogou.map.kubbo.metrics.influxdb.client;

import java.util.List;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.BatchPoints;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.Pong;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.Query;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.QueryResult;


/**
 * Interface with all available methods to access a InfluxDB database.
 *
 * A full list of currently available interfaces is implemented in:
 *
 * @author liufuliang
 *
 */
public interface InfluxDB {

  /**
   * ConsistencyLevel for write Operations.
   */
  public enum ConsistencyLevel {
    /** Write succeeds only if write reached all cluster members. */
    ALL("all"),
    /** Write succeeds if write reached any cluster members. */
    ANY("any"),
    /** Write succeeds if write reached at least one cluster members. */
    ONE("one"),
    /** Write succeeds only if write reached a quorum of cluster members. */
    QUORUM("quorum");
    private final String value;

    private ConsistencyLevel(final String value) {
      this.value = value;
    }

    /**
     * Get the String value of the ConsistencyLevel.
     *
     * @return the lowercase String.
     */
    public String value() {
      return this.value;
    }
  }

  /**
   * Ping this influxDB.
   *
   * @return the response of the ping execution.
   */
  public Pong ping();

  /**
   * Return the version of the connected influxDB Server.
   *
   * @return the version String, otherwise unknown.
   */
  public String version();

  /**
   * Write a set of Points to the influxdb database with the new (>= 0.9.0rc32) lineprotocol.
   *
   * @param batchPoints
   * @return successfull or not
   */
  public boolean write(BatchPoints batchPoints);

  /**
   * Execute a query against a database.
   *
   * @param query
   *            the query to execute.
   * @return a List of Series which matched the query.
   */
  public QueryResult query(Query query);

  /**
   * Create a new Database.
   *
   * @param name
   *            the name of the new database.
   */
  public boolean createDatabase(String name);

  /**
   * Delete a database.
   *
   * @param name
   *            the name of the database to delete.
   */
  public boolean deleteDatabase(String name);

  /**
   * Describe all available databases.
   *
   * @return a List of all Database names.
   */
  public List<String> describeDatabases();

  /**
   * Check if a database exists.
   *
   * @param name
   *            the name of the database to search.
   *
   * @return true if the database exists or false if it doesn't exist
   */
  public boolean databaseExists(String name);
 
}
