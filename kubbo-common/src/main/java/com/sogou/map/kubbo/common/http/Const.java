package com.sogou.map.kubbo.common.http;

/**
 * Constant values and strings.
 */
public class Const {
    public static final String DEFAULT_USER_AGENT = "kubbo-common/http:v1";
    public static final String DEFAULT_COMPRESS = "gzip";
    public static final String AUTHORIZATION_BASIC = "Basic";
    public static final String AUTHORIZATION_TOKEN = "Bearer";
    public static final String CHUNKED = "chunked";
    

    public static final String APP_FORM = "application/x-www-form-urlencoded";
    public static final String APP_JSON = "application/json";
    public static final String APP_BINARY = "application/octet-stream";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String HDR_CONTENT_TYPE = "Content-Type";
    public static final String HDR_CONTENT_ENCODING = "Content-Encoding";
    public static final String HDR_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HDR_ACCEPT = "Accept";
    public static final String HDR_CONNECTION = "Connection";
    public static final String HDR_AUTHORIZATION = "Authorization";
    public static final String HDR_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HDR_USER_AGENT = "User-Agent";
    public static final String UTF8 = "utf-8";

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final Class<? extends byte[]> BYTE_ARRAY_CLASS = EMPTY_BYTE_ARRAY.getClass();
    /** Minimal number of bytes the compressed content must be smaller than uncompressed */
    public static final int MIN_COMPRESSED_ADVANTAGE = 80;

    public static final int MAX_KEEPALIVE_CONNECTION_PER_HOST = 10;

    
}
