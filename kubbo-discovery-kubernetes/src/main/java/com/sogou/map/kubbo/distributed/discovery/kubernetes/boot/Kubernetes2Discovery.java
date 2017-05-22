/**
 * 
 */
package com.sogou.map.kubbo.distributed.discovery.kubernetes.boot;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.etcd.EtcdClient;
import com.sogou.map.kubbo.common.etcd.EtcdOprationException;
import com.sogou.map.kubbo.common.http.KubboHttpException;
import com.sogou.map.kubbo.common.http.Watcher;
import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.json.JSONObject;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.threadpool.impl.CachedThreadPool;
import com.sogou.map.kubbo.common.util.ExecutorUtils;
import com.sogou.map.kubbo.common.util.ProgressiveRetryState;
import com.sogou.map.kubbo.distributed.discovery.kubernetes.client.KubernetesClient;

/**
 * @author liufuliang
 *
 */
public class Kubernetes2Discovery {
    private static final Logger logger = LoggerFactory.getLogger(Kubernetes2Discovery.class);

    KubernetesClient kubernetesClient;
    EtcdClient etcdClient;
    boolean destroy = false;
    ProgressiveRetryState retry = new ProgressiveRetryState();

    public Kubernetes2Discovery(String etcdAddress, String kubernetesAddress, 
            String kubernetesToken, String kubernetesUser, String kubernetesPassword){
        etcdClient = new EtcdClient(etcdAddress, Constants.DEFAULT_DISCOVERY_ETCD_ROOT);
        kubernetesClient = new KubernetesClient(kubernetesAddress, kubernetesToken, kubernetesUser, kubernetesPassword);
        loadVersion();
    }
    
    protected Executor workLoop = CachedThreadPool.getExecutor(3, 100, 5, TimeUnit.MINUTES, 
            new SynchronousQueue<Runnable>(), "Kubbo-discovery", true);
    
    
    private void loadVersion(){
        if(kubernetesClient.getResourceVersion() >= 0){
            return;
        }
        for(;;){
            try{
                String version = etcdClient.getValue("version");
                kubernetesClient.setResourceVersion(Long.parseLong(version));
            } catch(EtcdOprationException e){
                if(e.getCause() instanceof KubboHttpException){
                    retry.scale();
                    logger.warn("etcd HTTP exception, will retry " + retry.interval()/1000 + " second later");
                    try { Thread.sleep(retry.interval()); } catch (InterruptedException ex) {}
                    continue;
                }
            } catch(Throwable t){
                if(logger.isDebugEnabled()){
                    logger.debug(t.getMessage());
                }
            }
            logger.info("etcd connection is ready.");
            logger.info("kubbo discovery resource version is " + kubernetesClient.getResourceVersion());
            retry.reset();
            break;
        }

    }
    private void upgradeVersion(long resourceVersion) {
        //local
        kubernetesClient.setResourceVersion(resourceVersion);
        
        //etcd
        while(true){
            long version = etcdClient.optLong("version");
            if(kubernetesClient.getResourceVersion() <= version){
                return;
            }
            try{
                //update
                if(etcdClient.set("version", 
                        String.valueOf(kubernetesClient.getResourceVersion()),
                        version > 0 ? String.valueOf(version) : null) != EtcdClient.SET_OK){
                    continue;
                }
            } catch(EtcdOprationException e){
                logger.warn("discovery version set exception." + kubernetesClient.getResourceVersion());
            }

            break;
        }	
    }
    
    /**
     *  TODO kubernetes model
     * @param kubernetesObj
     * @throws EtcdOprationException
     * @throws JSONException
     */
    private void doUpdate(JSONObject kubernetesObj) throws EtcdOprationException, JSONException{
        String kind = kubernetesObj.optString("kind");
        if(! kind.equalsIgnoreCase("Endpoints")){
            return;
        }
        //meta
        JSONObject metaObj = kubernetesObj.getJSONObject("metadata");
        String namespace = metaObj.optString("namespace", "default");
        String name = metaObj.optString("name");
        long resourceVersion = metaObj.optLong("resourceVersion");
        
        //upgrade version
        upgradeVersion(resourceVersion);

        
        //cas update
        long prevIndex = -1;
        String etcdApi = Constants.PROVIDER + "/" + namespace + "/" + name;
        while(true){
            //etcd meta
            JSONObject etcdObj = etcdClient.get(etcdApi);
            if(!etcdObj.has("errorCode")){
                JSONObject nodeObj = etcdObj.getJSONObject("node");
                JSONObject etcdKubernetesObj = new JSONObject(nodeObj.optString("value"));
                JSONObject etcdMetaObj = etcdKubernetesObj.getJSONObject("metadata");
                long etcdResourceVersion = etcdMetaObj.optLong("resourceVersion");
                
                if(resourceVersion <= etcdResourceVersion){
                    return;
                }
                prevIndex = nodeObj.optLong("modifiedIndex"); //cas prevIndex
            }
            
            //update
            long index = etcdClient.set(etcdApi, kubernetesObj.toString(), prevIndex);
            if(index != EtcdClient.SET_OK){
                if(index > 0 && index != prevIndex){
                    prevIndex = index;
                    if(logger.isDebugEnabled()){
                        logger.debug("etcd set error,  will try again");
                    }
                    continue;
                }
            }
            break;
        }		
    }
    
    private void update(final JSONObject kubernetesObj){
        if(kubernetesObj == null){
            return;
        }
        workLoop.execute(new Runnable(){
            @Override
            public void run() {
                try{
                    doUpdate(kubernetesObj);
                } catch(Throwable t){
                    logger.warn("discovery update exception", t);
                }
            }
        });
    }
    
    public void start(){
        while(!isDestroy()){
            //etcd max cache window is 1000
            long remoteMaxResourceVersion = kubernetesClient.fetchRemoteMaxResourceVersion();
            if(kubernetesClient.getResourceVersion() > 0 && remoteMaxResourceVersion - kubernetesClient.getResourceVersion() >= 1000){
                kubernetesClient.setResourceVersion(remoteMaxResourceVersion);
            }
            
            // watch
            String selector = Constants.DEFAULT_KUBERNETES_LABEL_ROLE + "=" + Constants.PROVIDER;
            kubernetesClient.watchEndpoints(selector, new Watcher<JSONObject>(){
                @Override
                public void received(JSONObject obj) {
                    if(obj.has("object")){
                        update(obj.optJSONObject("object"));
                        if(logger.isDebugEnabled()){
                            logger.debug("Received update: " + obj);
                        }
                    }
                    retry.reset();
                }

                @Override
                public void exceptionCaught(KubboHttpException exception) {                    
                    retry.scale();
                    logger.warn("Kubernetes discovery HTTP exception, will retry " + retry.interval()/1000 + " second later", exception);

                    if(retry.interval() > 0){
                        try { Thread.sleep(retry.interval()); } catch (InterruptedException e) {}
                    }                    
                }
            });
            
            
        }
    }
    
    public void stop(){
        this.destroy = true;
        ExecutorUtils.shutdownNow(workLoop, 2000);
    }
    
    public boolean isDestroy(){
        return this.destroy;
    }
}
