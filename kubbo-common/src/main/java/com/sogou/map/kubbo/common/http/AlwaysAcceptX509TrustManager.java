/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * @author liufuliang
 *
 */
public class AlwaysAcceptX509TrustManager implements X509TrustManager{

	public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
		
	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
		
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}
