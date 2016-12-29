/**
 * 
 */
package com.sogou.map.kubbo.kubernetes;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author liufuliang
 *
 */
public class Main {
    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // TODO Auto-generated method stub
            
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        } 


    };
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, KeyManagementException{
        String url = "https://10.134.106.205:6443/api/v1/namespaces/default/endpoints/mtk";
        SSLContext sslcontext = SSLContext.getInstance("TLS"); 
        sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
        URL requestUrl = new URL(url); 
        HttpsURLConnection httpsConn = (HttpsURLConnection)requestUrl.openConnection();
        //设置套接工厂 
        httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());
    }
}
