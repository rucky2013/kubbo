package com.sogou.map.kubbo.metrics.influxdb.client;

import com.sogou.map.kubbo.common.http.HttpClient;
import com.sogou.map.kubbo.common.http.KubboHttpException;
import com.sogou.map.kubbo.common.http.impl.JdkHttpClient;
import com.sogou.map.kubbo.common.json.JSONObject;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.BatchPoints;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.Pong;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.Query;
import com.sogou.map.kubbo.metrics.influxdb.client.obj.QueryResult;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of a InluxDB API.
 *
 * @author liufuliang
 */
public class InfluxDBImpl implements InfluxDB {
    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBImpl.class);
    
    public static final String USER = "u";
    public static final String PASSWORD = "p";
    public static final String Q = "q";
    public static final String DB = "db";
    public static final String RP = "rp";
    public static final String PRECISION = "precision";
    public static final String CONSISTENCY = "consistency";
    public static final String EPOCH = "epoch";
    public static final String CHUNK_SIZE = "chunk_size";

    private final HttpClient httpclient;
    private final String address;
    private final String database;
    private final String username;
    private final String password;
    
    private AtomicBoolean dbExist = new AtomicBoolean(false);

    public InfluxDBImpl(String address, String database, String username, String password) {
        if(StringUtils.isBlank(address)) {
            throw new IllegalArgumentException("address == Null");
        }
        if(!address.startsWith("http://")){
            address = "http://" + address;
        }
        
        this.httpclient = new JdkHttpClient();
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;

    }

    @Override
    public Pong ping() {
        try {
            long start = System.currentTimeMillis();
            String version = httpclient.get(address + "/ping")
                .execute()
                .success()
                .getHeader("X-Influxdb-Version");
            long end = System.currentTimeMillis();

            Pong pong = new Pong();
            pong.setVersion(version);
            pong.setResponseTime(end - start);
            return pong;
        } catch (KubboHttpException e) {
            LOG.error("InfluxDB ping error.", e);
            return Pong.NULL;
        }

    }

    @Override
    public String version() {
        return ping().getVersion();
    }

    @Override
    public boolean write(BatchPoints batchPoints) {
        // create database if not exist
        if (!dbExist.get() && !StringUtils.isBlank(database)) {
            if (!createDatabase(database)) {
                return false;
            } else {
                dbExist.set(true);
            }
        }
        
        try {            
            JSONObject jsonobj = httpclient.post(address + "/write")
                    .paramIfNotEmpty(DB, database)
                    .param(CONSISTENCY, batchPoints.getConsistency().value())
                    .paramIfNotEmpty(USER, username)
                    .paramIfNotEmpty(PASSWORD, password)
                    .body(batchPoints.toLineProtocol())
                    .execute()
                    .success()
                    .asType(JSONObject.class);
            String msg = jsonobj.optString("error", "");
            if(msg.contains("database not found")){
                dbExist.set(false);
            } 
            if(!msg.isEmpty()){
                LOG.error("InfluxDB write error, " + msg);
                return false;
            }
            return true;
        } catch (KubboHttpException e) {
            LOG.error("InfluxDB write db exception", e);
            return false;
        }
    }

    @Override
    public QueryResult query(Query query) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public boolean createDatabase(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name == Null");
        }

        String command = String.format("CREATE DATABASE \"%s\"", name);
        if (this.version().startsWith("0.")) {
            command = String.format("CREATE DATABASE IF NOT EXISTS \"%s\"", name);
        }
        
        try {
            httpclient.get(address + "/query")
                .param(Q, command)
                .paramIfNotEmpty(USER, username)
                .paramIfNotEmpty(PASSWORD, password)
                .execute()
                .success();
            return true;
        } catch (KubboHttpException e) {
            LOG.error("InfluxDB create db error", e);
            return false;
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name == Null");
        }
        String command = String.format("DROP DATABASE \"%s\"", name);

        try {
            httpclient.get(address + "/query")
                .param(Q, command)
                .paramIf(USER, username, !StringUtils.isBlank(username))
                .paramIf(PASSWORD, password, !StringUtils.isBlank(password))
                .execute()
                .success();
            return true;
        } catch (KubboHttpException e) {
            LOG.error("InfluxDB delete db error", e);
            return false;
        }
    }

    @Override
    public List<String> describeDatabases() {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public boolean databaseExists(final String name) {
        throw new UnsupportedOperationException("Unsupported");
    }

}
