/**
 * 
 */
package com.sogou.map.kubbo.distributed.discovery.kubernetes.client;

import java.util.concurrent.atomic.AtomicLong;

import com.sogou.map.kubbo.common.http.ChunkedHandler;
import com.sogou.map.kubbo.common.http.HttpClient;
import com.sogou.map.kubbo.common.http.HttpRequestBuilder;
import com.sogou.map.kubbo.common.http.KubboHttpException;
import com.sogou.map.kubbo.common.http.Watcher;
import com.sogou.map.kubbo.common.http.impl.JdkHttpClient;
import com.sogou.map.kubbo.common.json.JSONObject;
import com.sogou.map.kubbo.common.utils.StringUtils;

/**
 * @author liufuliang
 *
 */
public class KubernetesClient {
    AtomicLong resourceVersion = new AtomicLong(-1);
    HttpClient client = new JdkHttpClient();
    String kubernetesAddress;
    String kubernetesToken;
    String kubernetesUser;
    String kubernetesPassword;
    
    public KubernetesClient(String kubernetesAddress, String kubernetesToken, String kubernetesUser, String kubernetesPassword){
        this.kubernetesAddress = kubernetesAddress;
        this.kubernetesToken = kubernetesToken;
        this.kubernetesUser = kubernetesUser;
        this.kubernetesPassword = kubernetesPassword;
    }
    
    public void watch(String api, String selecter, Watcher<JSONObject> watcher){
        HttpRequestBuilder requestBuilder = client.get(kubernetesAddress + api + "?watch=true");
        //Constants.DEFAULT_KUBERNETES_LABEL_ROLE + "%3D" + Constants.PROVIDER);
        if(! StringUtils.isBlank(selecter)){
            requestBuilder.param("labelSelector", selecter);
        }
        if(resourceVersion.get() >= 0){
            requestBuilder.param("resourceVersion", resourceVersion.toString());
        }
        if(!kubernetesToken.isEmpty()){
            requestBuilder.tokenAuthentication(kubernetesToken);
        } else if(!kubernetesUser.isEmpty() && !kubernetesPassword.isEmpty()){
            requestBuilder.basicAuthentication(kubernetesUser, kubernetesPassword);
        }			
        requestBuilder.setChunkedHandler(ChunkedHandler.LINE);
        requestBuilder.watch(JSONObject.class, watcher);
    }
    
    public JSONObject fetch(String api) throws KubboHttpException{
        return fetch(api, null);
    }
    
    public JSONObject fetch(String api, String selecter) throws KubboHttpException{
        HttpRequestBuilder requestBuilder = client.get(kubernetesAddress + api);
        //Constants.DEFAULT_KUBERNETES_LABEL_ROLE + "%3D" + Constants.PROVIDER);
        if(! StringUtils.isBlank(selecter)){
            requestBuilder.param("labelSelector", selecter);
        }

        if(!kubernetesToken.isEmpty()){
            requestBuilder.tokenAuthentication(kubernetesToken);
        } else if(!kubernetesUser.isEmpty() && !kubernetesPassword.isEmpty()){
            requestBuilder.basicAuthentication(kubernetesUser, kubernetesPassword);
        }           
        return requestBuilder.execute(JSONObject.class);
    }
    
    public void watchEndpoints(String selecter, Watcher<JSONObject> watcher){
        watch("/api/v1/endpoints", selecter, watcher);
    }
    public void watchEndpoints(String namespace, String selecter, Watcher<JSONObject> watcher){
        watch("/api/v1/namespaces/"+ namespace +"/endpoints", selecter, watcher);
    }
    
    public long fetchRemoteMaxResourceVersion(){
        try {
            JSONObject obj = fetch("/api/v1/namespaces");
            JSONObject metaObj = obj.optJSONObject("metadata");
            return metaObj.optLong("resourceVersion", -1);
        } catch (Throwable t) {
            return -1;
        }
    }
    
    public void setResourceVersion(long version){
        boolean success = false;
        while(!success){
            long v = resourceVersion.get();
            if(version > v){
                success = resourceVersion.compareAndSet(v, version);
            } else{
                break;
            }
        }
    }
    public long getResourceVersion(){
        return this.resourceVersion.get();
    }
}
