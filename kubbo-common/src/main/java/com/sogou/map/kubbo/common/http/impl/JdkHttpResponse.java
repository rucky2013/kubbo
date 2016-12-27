/**
 * 
 */
package com.sogou.map.kubbo.common.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

import com.sogou.map.kubbo.common.http.AbstractHttpResponse;
import com.sogou.map.kubbo.common.http.AutoDisconnectInputStream;
import com.sogou.map.kubbo.common.http.ChunkedHandler;
import com.sogou.map.kubbo.common.http.Const;
import com.sogou.map.kubbo.common.http.Https;

/**
 * @author liufuliang
 *
 */
public class JdkHttpResponse extends AbstractHttpResponse{
	
	HttpURLConnection connection;
	
	JdkHttpResponse(HttpURLConnection connection) throws IOException {
		this(new JdkHttpConnection(connection), null);
		this.connection = connection;
	}
	JdkHttpResponse(HttpURLConnection connection, ChunkedHandler chunkedHandler) throws IOException {
		this(new JdkHttpConnection(connection), chunkedHandler);
		this.connection = connection;
	}
	
	protected JdkHttpResponse(JdkHttpConnection connection, ChunkedHandler chunkedHandler) throws IOException{
		super(connection, 
				connection.getHttpURLConnection().getResponseCode(),
				connection.getHttpURLConnection().getResponseMessage(),
				getWrappedInputStream(connection),
				chunkedHandler);
	}
	
	private static InputStream getWrappedInputStream(JdkHttpConnection connection) throws IOException{
		HttpURLConnection conn = connection.getHttpURLConnection();
		InputStream stream = conn.getResponseCode()/100 == 2 ? conn.getInputStream() : conn.getErrorStream();
		if(Https.isGzipCompress(conn)){
			stream = new GZIPInputStream(stream);
		}
		
		if(! Https.isKeepAliveSupported(conn)){
			stream = new AutoDisconnectInputStream(connection, stream);
		}
		return stream;
	}
	
	@Override
	public String getContentType() {
		return connection.getContentType();
	}

	public long getDate() {
		return connection.getDate();
	}

	@Override
	public long getExpiration() {
		return connection.getExpiration();
	}

	@Override
	public long getLastModified() {
		return connection.getLastModified();
	}

	@Override
	public String getHeader(String name) {
		return connection.getHeaderField(name);
	}

	@Override
	public boolean isChunked(){
		String transferEncoding = connection.getHeaderField(Const.HDR_TRANSFER_ENCODING);
		return transferEncoding != null && transferEncoding.equalsIgnoreCase(Const.CHUNKED);
	}
	
	/**
	 * Returns the value of the named field parsed as date (Millis since 1970). <br>
	 * See <a href=
	 * "http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getHeaderFieldDate(java.lang.String,+long)"
	 * > URLConnection.getHeaderFieldDate()</a>
	 *
	 * @param field
	 *            name of the header field
	 * @param defaultValue
	 *            the default value if the field is not present or malformed
	 * @return the value of the named header field, or the given default value
	 */
	public long getHeaderDate(String field, long defaultValue) {
		return connection.getHeaderFieldDate(field, defaultValue);
	}

	/**
	 * Returns the value of the named field parsed as a number. <br>
	 * See <a href=
	 * "http://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#getHeaderFieldInt(java.lang.String,+int)"
	 * > URLConnection.getHeaderFieldInt()</a>
	 *
	 * @param field
	 *            name of the header field
	 * @param defaultValue
	 *            the default value if the field is not present or malformed
	 * @return the value of the named header field, or the given default value
	 */
	public int getHeaderInt(String field, int defaultValue) {
		return connection.getHeaderFieldInt(field, defaultValue);
	}

	/**
	 * Get the "real" connection, typically to call some getters which are not
	 * provided by this Response object.
	 * 
	 * @return the connection object (many methods throw IllegalStateException
	 *         depending on the internal state).
	 */
	public HttpURLConnection getConnection() {
		return connection;
	}
}
