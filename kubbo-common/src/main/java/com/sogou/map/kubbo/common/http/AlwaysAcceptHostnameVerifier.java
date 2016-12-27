/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author liufuliang
 *
 */
public class AlwaysAcceptHostnameVerifier implements HostnameVerifier{

	public boolean verify(String hostname, SSLSession session) {
		return true;
	}

}
