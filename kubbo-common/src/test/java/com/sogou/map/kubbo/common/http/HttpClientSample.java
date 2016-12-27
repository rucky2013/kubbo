/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import com.sogou.map.kubbo.common.http.impl.JdkHttpClient;
import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.json.JSONObject;

/**
 * @author liufuliang
 *
 */
public class HttpClientSample {
	public static void main(String[] args) throws KubboHttpException, JSONException, InterruptedException{
		HttpClient client = new JdkHttpClient();
		
		JSONObject obj1 = client.get("https://www.googleapis.com/oauth2/v1/certs")
				  .execute()
				  .asType(JSONObject.class);	
		System.out.println(obj1);
		
		
		JSONObject obj = client.get("http://maps.googleapis.com/maps/api/directions/json")
			        .param("origin", "47.8227,12.096933")
			        .param("destination", "47.8633,12.215533")
			        .param("mode", "walking")
			        .param("sensor", "true")
			        .keepalive(false)
			        .connectTimeout(2000)
			        .readTimeout(3000)
			        .gzip(true)
			        .execute()
			        .success()
			        .asType(JSONObject.class);
		System.out.println(obj);
		
		
		client.get("https://10.134.106.205:6443/api/v1/namespaces/default/endpoints?watch=true")
		        .param("labelSelector", "kubbo.io/role=provider")
				.basicAuthentication("mssp", "")
				.setChunkedHandler(ChunkedHandler.LINE)
				.watch(JSONObject.class, new Watcher<JSONObject>(){
					@Override
					public void received(JSONObject obj) {
						System.out.println(obj);
					}

					@Override
					public void exceptionCaught(KubboHttpException exception) {
						System.out.println(exception);
					}
				});
	}
}
