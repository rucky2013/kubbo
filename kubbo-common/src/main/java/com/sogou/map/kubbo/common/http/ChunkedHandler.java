/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sogou.map.kubbo.common.json.JSONArray;
import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.json.JSONObject;

/**
 * @author liufuliang
 *
 */
public interface ChunkedHandler {
	<T> void read(InputStream input, Class<T> asType, Watcher<T> watcher);
	
	
	public ChunkedHandler LINE = new ChunkedHandler(){
		@SuppressWarnings("unchecked")
		@Override
		public <T> void read(InputStream input, Class<T> asType, Watcher<T> watcher) {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			try{
				String reply = null;
				while ((reply = br.readLine()) != null) {
					if (!reply.equals("")) {
						try {
							T result = null;
							if (asType == String.class) {
								result = (T) reply;
							} else if (asType == Const.BYTE_ARRAY_CLASS) {
								result = (T) reply.getBytes();
							} else if (asType == JSONObject.class) {
								result = (T) new JSONObject(reply);
							} else if (asType == JSONArray.class) {
								result = (T) new JSONArray(reply);
							}
							watcher.received(result);
						} catch (JSONException e) {
							watcher.exceptionCaught(new KubboHttpException("payload is not a valid JSON object", e));
						}
					}
				}
			} catch (IOException e) {
				try { input.close(); } catch (IOException e1) {}
				watcher.exceptionCaught(new KubboHttpException(e));
			}

		}
	};
}
