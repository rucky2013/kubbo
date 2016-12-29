/**
 * 
 */
package com.sogou.map.kubbo.distributed.discovery.kubernetes.client;

import com.sogou.map.kubbo.common.http.HttpClient;
import com.sogou.map.kubbo.common.http.KubboHttpException;
import com.sogou.map.kubbo.common.http.impl.JdkHttpClient;
import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.json.JSONObject;

/**
 * @author liufuliang
 *
 */
public class EtcdClient {
    public static final long SET_OK = -100;
    
    HttpClient client = new JdkHttpClient();
    String etcdAddress;
    String root;
    
    public EtcdClient(String etcdAddress, String root){
        this.etcdAddress = etcdAddress;
        this.root = root;
    }
    
    public String getValue(String api) throws EtcdOprationException{
        JSONObject obj = get(api);
        if(obj.has("errorCode")){
            throw new EtcdOprationException(obj.toString());
        }
        
        try {
            JSONObject nodeObj = obj.getJSONObject("node");
            return nodeObj.optString("value");
        } catch (JSONException e) {
            throw new EtcdOprationException(e);
        }
    }
    
    public String optValue(String api){		
        try {
            JSONObject obj = get(api);
            if(obj.has("errorCode")){
                return "";
            }
            JSONObject nodeObj = obj.getJSONObject("node");
            return nodeObj.optString("value");
        } catch (JSONException e) {
            return "";
        }catch (EtcdOprationException e) {
            return "";
        }
    }
    
    public long optLong(String api){
        try {
            JSONObject obj = get(api);
            if(obj.has("errorCode")){
                return 0;
            }
            JSONObject nodeObj = obj.getJSONObject("node");
            return nodeObj.optLong("value");
        } catch (JSONException e) {
            return 0;
        } catch (EtcdOprationException e) {
            return 0;
        }
    }
    
    public JSONObject get(String api) throws EtcdOprationException{
        String etcdApiKey = etcdAddress + "/v2/keys/" + root + "/" + api;
        try{
            JSONObject etcdObj = client.get(etcdApiKey)
                    .execute()
                    .asType(JSONObject.class);
            return etcdObj;
        } catch(KubboHttpException e){
            throw new EtcdOprationException(e);
        }
    }
    
    public long set(String api, String value) throws EtcdOprationException{
        return set(api, value, null);
    }
    
    public long set(String api, String value, String prevValue) throws EtcdOprationException{
        String etcdApiKey = etcdAddress + "/v2/keys/" + root + "/" + api;
        try{
            JSONObject etcdObj = client.put(etcdApiKey)
                    .paramIf("prevValue", prevValue, prevValue != null)
                    .param("value", value)
                    .execute()
                    .asType(JSONObject.class);
            if(etcdObj.has("errorCode")){
                return etcdObj.optLong("index");
            }
            return SET_OK;
        } catch(KubboHttpException e){
            throw new EtcdOprationException(e);
        }
    }
    
    public long set(String api, String value, long prevIndex) throws EtcdOprationException{
        String etcdApiKey = etcdAddress + "/v2/keys/" + root + "/" + api;
        try{
            JSONObject etcdObj = client.put(etcdApiKey)
                    .paramIf("preValue", String.valueOf(prevIndex), prevIndex >= 0)
                    .param("value", value)
                    .execute()
                    .asType(JSONObject.class);
            if(etcdObj.has("errorCode")){
                return etcdObj.optLong("index");
            }
            return SET_OK;
        } catch(KubboHttpException e){
            throw new EtcdOprationException(e);
        }
    }
}
