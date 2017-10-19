package com.sogou.map.kubbo.common;

import java.util.regex.Pattern;

import com.sogou.map.kubbo.common.util.NetUtils;

/**
 * Constants
 * 
 * @author liufuliang
 */
public final class Constants {

    public static final String  PROVIDER                           = "provider";

    public static final String  CONSUMER                           = "consumer";
            
    public static final String  DEFAULT_KUBERNETES_LABEL_ROLE      = "kubbo/role";

    public static final String  DEFAULT_DISCOVERY_ETCD_ROOT        = "kubbo";
    
    public static final String  DEFAULT_ENV_PREFIX                 = "kubbo";    
    
    
    public static final String  $INVOKE                            = "$invoke";

    public static final String  $ECHO                              = "$echo";

    public static final String  SEND_BLOCKING_KEY                  = "send.blocking";

    public static final boolean DEFAULT_SEND_BLOCKING              = false;

    public static final String  DEFAULT_CHARSET                    = "UTF-8";

    public static final String  DEFAULT_APPLICATION_NAME           = NetUtils.getLocalAddress().toString();

    public static final String  DEFAULT_APPLICATION_HOME           = ".";
    
   
    /*
     * threadpool
     */
    public static final String  THREADPOOL_KEY                     = "threadpool";

    public static final String  THREAD_NAME_KEY                    = "threadname";

    public static final String  IO_THREADS_KEY                     = "iothreads";

    public static final String  CORE_THREADS_KEY                   = "corethreads";

    public static final String  MAX_THREADS_KEY                    = "maxthreads";

    public static final String  QUEUES_KEY                         = "queues";
    
    public static final String  ALIVE_KEY                          = "alive";

    public static final String  DEFAULT_THREAD_NAME                = "kubbo";

    public static final String  DEFAULT_THREADPOOL                 = "limited";

    public static final String  DEFAULT_CLIENT_THREADPOOL          = "cached";

    public static final String  DEFAULT_SERVER_THREADPOOL          = "scalable";
 
    public static final int     DEFAULT_ALIVE                      = 60 * 1000;

    /*
     * threadpool(fixed, limited, cached)
     */
    public static final int     DEFAULT_CORE_THREADS               = 0;

    public static final int     DEFAULT_MAX_THREADS                = 200;

    public static final int     DEFAULT_QUEUES                     = 0;
    
    /*
     *  threadpool(scalable)
     */
    public static final int     DEFAULT_SCALABLE_CORE_THREADS      = 10;
    
    public static final int     DEFAULT_SCALABLE_MAX_THREADS       = 200;
    
    public static final int     DEFAULT_SCALABLE_QUEUES      	   = 100;
    
    /*
     * io threads
     */
    public static final int     DEFAULT_IO_THREADS                 = Runtime.getRuntime().availableProcessors() + 1;

    public static final int     DEFAULT_CONNECTIONS                = 0;

    public static final int     DEFAULT_ACCEPTS                    = 0;

    public static final int     DEFAULT_IDLE_TIMEOUT               = 600 * 1000;

    public static final int     DEFAULT_HEARTBEAT                  = 60 * 1000;

    public static final int     DEFAULT_TIMEOUT                    = 2000;

    public static final int     DEFAULT_CONNECT_TIMEOUT            = 3000;

    public static final int     DEFAULT_RETRY                      = 2;

    public static final boolean DEFAULT_FAILOVER_EVENT_TIMEOUT     = true;
    
    public static final int     DEFAULT_MAX_PAYLOAD                = 8 * 1024 * 1024; // 默认8m
   
    public static final int     DEFAULT_WEIGHT                     = 100;

    public static final String  REMOVE_VALUE_PREFIX                = "-";

    public static final String  HIDE_KEY_PREFIX                    = ".";

    public static final String  DEFAULT_KEY_PREFIX                 = "default.";

    public static final String  DEFAULT_KEY                        = "default";

    public static final String  LOADBALANCE_KEY                    = "loadbalance";

    public static final String  ANYHOST_KEY                        = "anyhost";

    public static final String  ANYHOST_VALUE                      = "0.0.0.0";

    public static final String  LOCALHOST_KEY                      = "localhost";

    public static final String  LOCALHOST_VALUE                    = "127.0.0.1";

    public static final String  APPLICATION_KEY                    = "application";

    public static final String  LOCAL_KEY                          = "local";

    public static final String  PROTOCOL_KEY                       = "protocol";

    public static final String  PROXY_KEY                          = "proxy";

    public static final String  WEIGHT_KEY                         = "weight";

    public static final String  EXECUTES_KEY                       = "executes";

    public static final String  MAX_PAYLOAD_KEY                    = "payload.max";

    public static final String  FILTER_KEY                         = "filter";

    public static final String  ACCESSLOG_KEY                      = "accesslog";

    public static final String  INVOKER_LISTENER_KEY               = "invoker.listener";

    public static final String  EXPORTER_LISTENER_KEY              = "exporter.listener";

    public static final String  ACTIVES_KEY                        = "actives";

    public static final String  CONNECTIONS_KEY                    = "connections";

    public static final String  ACCEPTS_KEY                        = "accepts";

    public static final String  IDLE_TIMEOUT_KEY                   = "idle.timeout";

    public static final String  HEARTBEAT_KEY                      = "heartbeat";

    public static final String  HEARTBEAT_TIMEOUT_KEY              = "heartbeat.timeout";

    public static final String  CONNECT_TIMEOUT_KEY                = "connect.timeout";

    public static final String  TIMEOUT_KEY                        = "timeout";

    public static final String  RETRY_KEY                          = "retry";

    public static final String  FAILOVER_EVENT_TIMEOUT_KEY         = "failover.event.timeout";

    public static final String  SESSIONLAYER_KEY                   = "sessionlayer";

    public static final String  TRANSPORTLAYER_KEY                 = "transportlayer";

    public static final String  SERVER_KEY                         = "server";

    public static final String  CLIENT_KEY                         = "client";
    
    public static final String  DISCOVERY_KEY                      = "discovery";

    public static final String  ID_KEY                             = "id";

    public static final String  ASYNC_KEY                          = "async";

    public static final String  ONEWAY_KEY                         = "oneway";

    public static final String  TOKEN_KEY                          = "token";

    public static final String  METHOD_KEY                         = "method";

    public static final String  METHODS_KEY                        = "methods";

    public static final String  CHARSET_KEY                        = "charset";

    public static final String  RECONNECT_KEY                      = "reconnect";

    public static final String  SEND_RECONNECT_KEY                 = "send.reconnect";

    public static final int     DEFAULT_RECONNECT_PERIOD           = 2000;

    public static final String  PID_KEY                            = "pid";

    public static final String  TIMESTAMP_KEY                      = "timestamp";
    
    public static final String  WARMUP_KEY                         = "warmup";

    public static final int     DEFAULT_WARMUP                     = 5 * 60 * 1000;

    public static final String  CHECK_KEY                          = "check";
    
    public static final String  IMPLEMENTION_KEY                   = "implemention";

    public static final String  GROUP_KEY                          = "group";

    public static final String  PATH_KEY                           = "path";

    public static final String  INTERFACE_KEY                      = "interface";

    public static final String  GENERIC_KEY                        = "generic";

    public static final String  FILE_KEY                           = "file";

    public static final String  WAIT_KEY                           = "wait";

    public static final String  CLASSIFIER_KEY                     = "classifier";

    public static final String  VERSION_KEY                        = "version";

    public static final String  REVISION_KEY                       = "revision";

    public static final String  KUBBO_VERSION_KEY                  = "kubbo";

    public static final String  CHANNEL_HANDLER_KEY                = "channel.handler";

    public static final String  DEFAULT_CHANNEL_HANDLER            = "default";

    public static final String  ANY_VALUE                          = "*";

    public static final String  COMMA_SEPARATOR                    = ",";

    public static final Pattern COMMA_SPLIT_PATTERN                = Pattern.compile("\\s*[,]+\\s*");

    public final static String  PATH_SEPARATOR                     = "/";

    public static final String  SEMICOLON_SEPARATOR                = ";";

    public static final Pattern SEMICOLON_SPLIT_PATTERN            = Pattern.compile("\\s*[;]+\\s*");
    
    public static final String  INTERVAL_KEY                       = "interval";
    
    
    //channel.readonly
    public static final String  CHANNEL_ATTRIBUTE_READONLY_KEY     = "channel.readonly";

    public static final String  CHANNEL_SEND_READONLYEVENT_BLOCKING_KEY     = "channel.readonly.send.blocking";

    public static final String  CHANNEL_SEND_READONLYEVENT_KEY     = "channel.readonly.send";

    public static final String  SHUTDOWN_WAIT_KEY                  = "kubbo.shutdown.wait";

    /**
     * 默认值毫秒，避免重新计算.
     */
    public static final int     DEFAULT_SERVER_SHUTDOWN_TIMEOUT       = 10000;
    
    public static final String AUTO_ATTACH_INVOCATIONID_KEY 	      = "invocation.id.autoattach";
    
    public static final String INPUT_KEY                              = "input";
    
    public static final String OUTPUT_KEY                             = "output";
    
    public static final String TRUE                                   = "true";
    
    public static final String FALSE                                  = "false";    
    
    /*
     * global env & system property
     * 列在这里, 用于全局使用
     */
    public static final String KUBBO_CONFIGURATION_KEY              = "kubbo.configuration";

    public static final String DEFAULT_KUBBO_CONFIGURATION          = "kubbo.properties";
    
    /*
     * metrics
     */
    public static final int  DEFAULT_METRICS_INTERVAL               = 60 * 1000;
    
    public static final String  REPORTER_KEY                        = "reporter";
    
    /**
     * trace
     */
    public static final String SAMPLER_KEY                          = "sampler";
    
    /**
     * codec
     */
    public static final String  CODEC_KEY                            = "codec";

    public static final String  SERIALIZATION_KEY                    = "serialization";
    
    public static final String DECODE_EXECUTE_IN_TASK_THREAD_KEY     = "decode.execute.task";

    public static final boolean DEFAULT_DECODE_EXECUTE_IN_TASK_THREAD = false;

    public static final String  DECODE_BUFFER_KEY                    = "decode.buffer";
    
    public static final int     DEFAULT_DECODE_BUFFER_SIZE           = 8 * 1024; // default decode buffer size is 8k.
    
    public static final String  ENCODE_BUFFER_KEY                    = "encode.buffer";
    
    public static final int     DEFAULT_ENCODE_BUFFER_SIZE           = 1 * 1024; // default encode buffer size is 8k.
    
    public static final int     MAX_BUFFER_SIZE                      = 1024 * 1024; // 1M

    public static final int     MIN_BUFFER_SIZE                      = 1 * 1024;    //1K
    

    private Constants(){ }     
}
