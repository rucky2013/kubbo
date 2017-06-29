/**
 * 
 */
package com.sogou.map.kubbo.common.etcd;

import java.util.HashMap;
import java.util.Map;

import com.sogou.map.kubbo.common.http.HttpClient;
import com.sogou.map.kubbo.common.http.KubboHttpException;
import com.sogou.map.kubbo.common.http.impl.JdkHttpClient;
import com.sogou.map.kubbo.common.json.JSONArray;
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
    String etcdRoot;

    public EtcdClient(String etcdAddress, String root){
        this.etcdAddress = etcdAddress;
        if(!this.etcdAddress.startsWith("http://")){
            this.etcdAddress = "http://" + this.etcdAddress;
        }
        this.etcdRoot = root;
    }
    
    public String getEtcdAddress() {
		return etcdAddress;
	}

	public void setEtcdAddress(String etcdAddress) {
		this.etcdAddress = etcdAddress;
	}

	public String getEtcdRoot() {
		return etcdRoot;
	}

	public void setEtcdRoot(String etcdRoot) {
		this.etcdRoot = etcdRoot;
	}

	public Map<String, String> list() throws EtcdOprationException {
	    JSONObject obj = get("");
        if(obj.has("errorCode")){
            throw new EtcdOprationException(obj.toString());
        }
        Map<String, String> result = new HashMap<String, String>();
        String rootPrefix = "/" + getEtcdRoot() + "/";
        try {
            JSONObject nodeObj = obj.getJSONObject("node");
            if(nodeObj.has("nodes")){
                JSONArray nodesArray = nodeObj.getJSONArray("nodes");
                for(Object elem : nodesArray){
                    JSONObject node = (JSONObject)elem;
                    result.put(node.getString("key").substring(rootPrefix.length()), node.getString("value"));
                }
            } 
        } catch (JSONException e) {
            throw new EtcdOprationException(e);
        }
        return result;
	}
	
	public String getValue(String api) throws EtcdOprationException {
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
        String etcdApiKey = etcdAddress + "/v2/keys/" + etcdRoot;
        if(!api.isEmpty() && !api.equals("/")){
            etcdApiKey =  etcdApiKey + "/" + api;
        }
        try{
            JSONObject etcdObj = client.get(etcdApiKey)
                    .execute()
                    .asType(JSONObject.class);
            return etcdObj;
        } catch(KubboHttpException e){
            throw new EtcdOprationException(e);
        }
    }
        
    public JSONObject watch(String api) throws EtcdOprationException {
        return watch(api, -1);
    }

    public JSONObject watch(String api, long watchIndex) throws EtcdOprationException {
        String etcdApiKey = etcdAddress + "/v2/keys/" + etcdRoot;
        if(!api.isEmpty() && !api.equals("/")){
            etcdApiKey =  etcdApiKey + "/" + api;
        }
        int[] acceptCodes = new int[]{400};
        try {
            while(true){
                JSONObject etcdObj = client.get(etcdApiKey)
                        .param("wait", "true")
                        .param("recursive", "true")
                        .paramIf("waitIndex", String.valueOf(watchIndex), watchIndex >= 0)
                        .watch()
                        .success(acceptCodes)
                        .asType(JSONObject.class);
                if(etcdObj != null && etcdObj.has("errorCode")){
                    int code = etcdObj.optInt("errorCode", 0);
                    if(code == 401){
                        watchIndex = etcdObj.optInt("index", -1);
                        continue;
                    }
                }
                return etcdObj;
            }            
        } catch (KubboHttpException e) {
            throw new EtcdOprationException(e);
        }
    }
    
    public long set(String api, String value) throws EtcdOprationException{
        return set(api, value, 0);
    }
    
    public long set(String api, String value, int ttl) throws EtcdOprationException{
        return set(api, value, ttl, null);
    }
    
    public long set(String api, String value, String prevValue) throws EtcdOprationException{
        return set(api, value, 0, prevValue);
    }
    
    public long set(String api, String value, int ttl, String prevValue) throws EtcdOprationException{
        String etcdApiKey = etcdAddress + "/v2/keys/" + etcdRoot + "/" + api;
        try{
            JSONObject etcdObj = client.put(etcdApiKey)
                    .paramIf("prevValue", prevValue, prevValue != null)
                    .paramIf("ttl", String.valueOf(ttl), ttl > 0)
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
        return set(api, value, 0, prevIndex);
    }
    
    public long set(String api, String value, int ttl, long prevIndex) throws EtcdOprationException{
        String etcdApiKey = etcdAddress + "/v2/keys/" + etcdRoot + "/" + api;
        try{
            JSONObject etcdObj = client.put(etcdApiKey)
                    .paramIf("preValue", String.valueOf(prevIndex), prevIndex >= 0)
                    .paramIf("ttl", String.valueOf(ttl), ttl > 0)
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
