/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * @author liufuliang
 *
 */
public class Https {
	
    public static byte[] readBytes(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(is, baos);
        return baos.toByteArray();
    }
	
	public static byte[] gzip(byte[] input) throws IOException {
        GZIPOutputStream gzipOS = null;
        try {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            gzipOS = new GZIPOutputStream(byteArrayOS);
            gzipOS.write(input);
            gzipOS.flush();
            gzipOS.close();
            gzipOS = null;
            return byteArrayOS.toByteArray();
        } finally {
            if (gzipOS != null) {
                try { gzipOS.close(); } catch (Exception ignored) {}
            }
        }
    }
	
	public static boolean isGzipCompress(HttpURLConnection connection){
		String encoding = connection.getContentEncoding();
		return encoding != null && encoding.toLowerCase().indexOf("gzip") >= 0;
	}
	
	public static boolean isKeepAliveSupported(HttpURLConnection connection){
		String connectionHeader = connection.getHeaderField("Connection");
		return connectionHeader == null || connectionHeader.toLowerCase().indexOf("close") < 0;
	}

	
    /**
     * Convert a Map to a query string.
     * @param values the map with the values
     *               <code>null</code> will be encoded as empty string, all other objects are converted to
     *               String by calling its <code>toString()</code> method.
     * @return e.g. "key1=value&amp;key2=&amp;email=max%40example.com"
     */
    public static String toQueryString(Map<String, Object> values) {
        StringBuilder sbuf = new StringBuilder();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Object entryValue = entry.getValue();
            if (entryValue instanceof Object[]) {
                for (Object value : (Object[]) entryValue) {
                    appendParam(sbuf, entry.getKey(), value);
                }
            } else if (entryValue instanceof Iterable) {
                for (Object multiValue : (Iterable<?>) entryValue) {
                    appendParam(sbuf, entry.getKey(), multiValue);
                }
            } else {
                appendParam(sbuf, entry.getKey(), entryValue);
            }
        }

        return sbuf.length() > 0 ? sbuf.substring(1).toString() : sbuf.toString();
    }
    
    private static void appendParam(StringBuilder sbuf, String entryKey, Object value) {
        String sValue = value == null ? "" : String.valueOf(value);
        sbuf.append("&");
        sbuf.append(urlEncode(entryKey));
        sbuf.append('=');
        sbuf.append(urlEncode(sValue));
    }
    
    
    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
    
    
    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int count;
        while ((count = input.read(buffer)) != -1) {
        	output.write(buffer, 0, count);
        }

    }
    
	private Https(){
		
	}
}
