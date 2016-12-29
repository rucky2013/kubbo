package com.sogou.map.kubbo.distributed.discovery.kubernetes.boot;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.SystemPropertyUtils;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private void run(){
        String etcd = SystemPropertyUtils.get("kubbo.discovery.etcd", "http://127.0.0.1:2379");
        String kubernetes = SystemPropertyUtils.get("kubbo.discovery.kubernetes", "https://kubernetes.default");
        String kubernetesToken = SystemPropertyUtils.get("kubbo.discovery.kubernetes.token", "");
        String kubernetesUser = SystemPropertyUtils.get("kubbo.discovery.kubernetes.user", "");
        String kubernetesPassword = SystemPropertyUtils.get("kubbo.discovery.kubernetes.password", "");
        
        logger.info("kubbo.discovery.etcd=" + etcd);
        logger.info("kubbo.discovery.kubernetes=" + kubernetes);
        
        Kubernetes2Discovery discovery = new Kubernetes2Discovery(etcd, kubernetes, kubernetesToken, kubernetesUser, kubernetesPassword);
        discovery.start();
    }
    
    public static void main(String[] args){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.run();
    }
}
