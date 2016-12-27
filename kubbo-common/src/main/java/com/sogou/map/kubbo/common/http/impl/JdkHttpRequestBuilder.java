/**
 * 
 */
package com.sogou.map.kubbo.common.http.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.sogou.map.kubbo.common.http.AbstractHttpRequestBuilder;
import com.sogou.map.kubbo.common.http.AlwaysAcceptHostnameVerifier;
import com.sogou.map.kubbo.common.http.AlwaysAcceptX509TrustManager;
import com.sogou.map.kubbo.common.http.Const;
import com.sogou.map.kubbo.common.http.HttpResponse;
import com.sogou.map.kubbo.common.http.Https;
import com.sogou.map.kubbo.common.http.Method;
import com.sogou.map.kubbo.common.json.JSONArray;
import com.sogou.map.kubbo.common.json.JSONObject;

/**
 * @author liufuliang
 *
 */
public class JdkHttpRequestBuilder extends AbstractHttpRequestBuilder {

	public JdkHttpRequestBuilder(Method method, String url) {
		super(method, url);
	}

	/* (non-Javadoc)
	 * @see com.sogou.map.kubbo.common.http.AbstractHttpRequestBuilder#doExecute()
	 */
	@Override
	protected HttpResponse doExecute() throws IOException {
		//query params
		String url = JdkHttpRequestBuilder.this.url;
        if (! Method.isBodyAble(method) && hasParams()) {
        	url += (url.contains("?") ? "&" : "?") + Https.toQueryString(params);
        }
        //connection
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        //ssl
        initializeSSL(connection);
        //method
        connection.setRequestMethod(method.name());
        //redirect
        connection.setInstanceFollowRedirects(followRedirects);
        //cache
        connection.setUseCaches(cache);
        //connect timeout
        if(connectTimeout > 0){
        	connection.setConnectTimeout(connectTimeout);
        }
        //read timeout
        if(readTimeout > 0){
        	connection.setReadTimeout(readTimeout);
        }
        //ifModifiedSince
        if (ifModifiedSince > 0) {
            connection.setIfModifiedSince(ifModifiedSince);
        }
        //keepalive
        if(! keepalive){
            connection.setRequestProperty(Const.HDR_CONNECTION, "close");
        }
        //gzip
        if (gzip) {
            connection.setRequestProperty(Const.HDR_ACCEPT_ENCODING, Const.DEFAULT_COMPRESS);
        }
        //headers
        if(hasHeaders()){
        	addRequestProperties(connection, headers);
        }
        //request
        if(Method.isBodyAble(method)){
        	if(isStreamBody()){
                streamBody(connection, body, false);
        	} else{
        		writeBody(connection, body);
        	}
        } else {
        	connection.connect();
        }
        //response
        HttpResponse response = new JdkHttpResponse(connection, chunkedHandler);
        if(! keepalive){
        	response.setAutoDisconnect();
        }
        
        return response;
	}
	
    private void writeBody(HttpURLConnection connection, Object body) throws IOException {
        byte[] requestBody = null;
        if (hasParams()) {
        	connection.setRequestProperty(Const.HDR_CONTENT_TYPE, Const.APP_FORM);
        	requestBody = Https.toQueryString(params).getBytes(Const.UTF8);
        } else if (! hasBody()) {
        	requestBody = null;
        } else if (body instanceof JSONObject) {
        	connection.setRequestProperty(Const.HDR_CONTENT_TYPE, Const.APP_JSON);
        	requestBody =  ((JSONObject) body).toString().getBytes(Const.UTF8);
        } else if (body instanceof JSONArray) {
        	connection.setRequestProperty(Const.HDR_CONTENT_TYPE, Const.APP_JSON);
        	requestBody =  ((JSONArray) body).toString().getBytes(Const.UTF8);
        } else if (body instanceof byte[]) {
        	connection.setRequestProperty(Const.HDR_CONTENT_TYPE, Const.APP_BINARY);
            requestBody = (byte[]) body;
        } else {
        	connection.setRequestProperty(Const.HDR_CONTENT_TYPE, Const.TEXT_PLAIN);
        	requestBody = body.toString().getBytes(Const.UTF8);
        }

        if (requestBody != null) {
            connection.setFixedLengthStreamingMode(requestBody.length);
            connection.setDoOutput(true);
	    	OutputStream os = null;
	        try {
	            os = connection.getOutputStream();
	            os.write(requestBody);
	            os.flush();
	        } finally {
	            if (os != null) {
	                try { os.close(); } catch (Exception ignored) {}
	            }
	        }
	    }
    }
	
	private void streamBody(HttpURLConnection connection, Object body, boolean compress) throws IOException {
        InputStream is;
        boolean closeStream;
		long length = -1L;
    	if (body instanceof File) {
            length = compress ? -1L : ((File) body).length();
            is = new FileInputStream((File) body);
            closeStream = true;
        } else {
            length = -1L;
            is = (InputStream) body;
            closeStream = false;
        }

        if (length > Integer.MAX_VALUE) {
            length = -1L; // use chunked streaming mode
        }

        if(length < 0){
        	connection.setChunkedStreamingMode(-1); // use default chunk size
        } else {
            connection.setFixedLengthStreamingMode((int) length);
        }

        //attributes
    	connection.setRequestProperty(Const.HDR_CONTENT_TYPE, Const.APP_BINARY);
        connection.setDoOutput(true);

        OutputStream os = null;
        try {
            os = connection.getOutputStream();
            if (compress) {
                os = new GZIPOutputStream(os);
            }
            Https.copyStream(is, os);
            os.flush();
        } finally {
            if (os != null) {
                try { os.close(); } catch (Exception ignored) {}
            }
            if (is != null && closeStream) {
                try { is.close(); } catch (Exception ignored) {}
            }
        }
    }
	
	private void addRequestProperties(HttpURLConnection connection, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            addRequestProperty(connection, entry.getKey(), entry.getValue());
        }
    } 
	
	private void addRequestProperty(HttpURLConnection connection, String name, Object value) {
        if (name == null || name.length() == 0 || value == null) {
            throw new IllegalArgumentException("name and value must not be empty");
        }

        String valueAsString;
        if (value instanceof Date) {
            valueAsString = getRfc1123DateFormat().format((Date) value);
        } else if (value instanceof Calendar) {
            valueAsString = getRfc1123DateFormat().format(((Calendar) value).getTime());
        } else {
            valueAsString = value.toString();
        }

        connection.addRequestProperty(name, valueAsString);
    }
	
	private DateFormat getRfc1123DateFormat() {
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        format.setLenient(false);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }
	
	
    private void initializeSSL(HttpURLConnection connection){
        if ( connection instanceof HttpsURLConnection ) {
            HttpsURLConnection sslConnection = (HttpsURLConnection) connection;
            if (hostnameVerifier == null) {
            	hostnameVerifier = new AlwaysAcceptHostnameVerifier();
            }
            if (sslSocketFactory == null) {
				try {
	        		SSLContext sslcontext = SSLContext.getInstance("TLS");
	        		sslcontext.init(null, new TrustManager[]{ new AlwaysAcceptX509TrustManager() }, null);
	            	sslSocketFactory = sslcontext.getSocketFactory();
				} catch (NoSuchAlgorithmException e) {
				} catch (KeyManagementException e) {
				} 
            }
            sslConnection.setHostnameVerifier(hostnameVerifier);
            sslConnection.setSSLSocketFactory(sslSocketFactory);
        }
    }

}
