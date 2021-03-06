/**
 * 
 */
package com.sogou.map.kubbo.distributed.discovery.kubernetes;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.http.HttpClient;
import com.sogou.map.kubbo.common.http.KubboHttpException;
import com.sogou.map.kubbo.common.http.impl.JdkHttpClient;
import com.sogou.map.kubbo.common.json.JSONArray;
import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.json.JSONObject;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.ProgressiveRetryState;
import com.sogou.map.kubbo.distributed.discovery.AbstractDiscoveryDirectory;
import com.sogou.map.kubbo.rpc.Protocols;

/**
 * @author liufuliang
 *
 */
public class EtcdDiscoveryDirectory<T> extends AbstractDiscoveryDirectory<T>{
    
    private static final Logger logger = LoggerFactory.getLogger(EtcdDiscoveryDirectory.class);
    
    private static final String DEFAULT_KUBERNETES_NAMESPACE = "default";
    private static final String DEFAULT_ETCD_KUBBO_DIRECTORY = "/v2/keys/kubbo/provider/";
    private static final String  DEFAULT_KUBERNETES_PORT_NAME = "kubbo";
    private static final String  DEFAULT_SERVICE_PATH = "/";

    
    long waitIndex = -1;
    String api;
    HttpClient etcdClient;
    ProgressiveRetryState retry = new ProgressiveRetryState();
    
    public EtcdDiscoveryDirectory(Class<T> type, URL url) {
        super(type, url);
        String servicekey = url.getPath();
        if(servicekey.indexOf("/") < 0){
            servicekey = DEFAULT_KUBERNETES_NAMESPACE + "/" + servicekey;
        }
        this.api = "http://" + url.getAddress() + DEFAULT_ETCD_KUBBO_DIRECTORY + servicekey;
        etcdClient = new JdkHttpClient();
        if(logger.isDebugEnabled()){
            logger.debug("Etcd api: " + api);
        }
    }
    
    /**
     * TODO model
     * 为了简单起见, 直接使用kubernetes的Endpoints Api
     * 应该建立Discovery Model, 与kubernetes api解耦
     * <code>
     * kubernetes Endpoints
     * </code>
     */
    @Override
    protected List<URL> fetch() {
        List<URL> urls = new ArrayList<URL>();
        while(!isDestroyed()){
            try{
                int[] acceptCodes = new int[]{400};
                JSONObject etcdObj = etcdClient.get(api)
                        .paramIf("wait", "true", waitIndex >= 0)
                        .paramIf("waitIndex", String.valueOf(waitIndex), waitIndex >= 0)
                        //.watch()
                        .readTimeout(24 * 60 * 60 * 1000) // 24h fix long time(several days) watch not work
                        .execute()
                        .success(acceptCodes)
                        .asType(JSONObject.class);	
                //EventIndexCleared
                if(etcdObj != null && etcdObj.has("errorCode")){
                    int code = etcdObj.optInt("errorCode", 0);
                    if(code == 401){
                        waitIndex = etcdObj.optInt("index", -1);
                        continue;
                    }
                }
                
                JSONObject nodeObj = etcdObj.getJSONObject("node");

                JSONObject kubernetesEndpointsObj = new JSONObject(nodeObj.optString("value"));
                JSONArray subsetsArray = kubernetesEndpointsObj.getJSONArray("subsets");
                for(int i = 0; i < subsetsArray.length(); ++i){
                    JSONObject subsetObj = subsetsArray.getJSONObject(i);
                    if(! subsetObj.has("addresses") || ! subsetObj.has("ports")){
                        continue;
                    }
                    JSONArray addressesArray = subsetObj.getJSONArray("addresses");
                    JSONArray portsArray = subsetObj.getJSONArray("ports");
                    if(addressesArray.isEmpty() || portsArray.isEmpty()){
                        continue;
                    }
                    //port
                    int port = 0;
                    for(int j = 0; j < portsArray.length(); ++j){
                        JSONObject portObj = portsArray.getJSONObject(j);
                        if(portObj.optString("name").equalsIgnoreCase(DEFAULT_KUBERNETES_PORT_NAME)){
                            port = portObj.optInt("port");
                            break;
                        } else if(port == 0){
                            port = portObj.optInt("port");
                        }
                    }
                    //address
                    for(int j = 0; j < addressesArray.length(); ++j){
                        JSONObject addressObj = addressesArray.getJSONObject(j);
                        String ip = addressObj.optString("ip");
                        if(!ip.isEmpty()){
                            URL url = new URL(Protocols.getExtensionType(getUrl()), ip, port, DEFAULT_SERVICE_PATH);
                            urls.add(url);
                        }
                    }
                }
                waitIndex = nodeObj.getLong("modifiedIndex") + 1;
                retry.reset();
                break;
            } catch(KubboHttpException e){
                // ignore read timeout
                Throwable cause = e.getCause();
                if(cause instanceof SocketTimeoutException && "Read timed out".equalsIgnoreCase(cause.getMessage())){
                    logger.debug("Read timed out, watch with timeout again.");
                    continue;
                }
                retry.scale();
                logger.warn("Etcd discovery HTTP exception for " +  getUrl() +  ", will retry " + retry.interval()/1000 + " second later.", e);
            } catch (JSONException e) {
                retry.scale();
                logger.warn("Etcd discovery Parse exception for " +  getUrl() +  ", will retry " + retry.interval()/1000 + "second later." , e);
            } 
            
            if(retry.interval() > 0){
                urls.clear();
                try { Thread.sleep(retry.interval()); } catch (InterruptedException e) {}
            }
        }
        if(logger.isDebugEnabled()){
            logger.debug("Discovery addresses fetch: " + urls);
        }
        
        // 打乱服务端点顺序, 
        // 防止客户端通过轮询负载算法将相同的接口请求都分发到相同的服务端点
        Collections.shuffle(urls);
        return urls;
    }
    
}
