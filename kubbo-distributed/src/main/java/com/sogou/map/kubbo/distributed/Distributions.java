/**
 * 
 */
package com.sogou.map.kubbo.distributed;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * 分布式集群接口
 * @author liufuliang
 *
 */
public class Distributions {

    public static <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {       
        //discovery
        Discovery discovery = Extensions.getAdaptiveExtension(Discovery.class);
        Directory<T> directory = discovery.subscribe(type, url);
        // replication
        Replication replica = Extensions.getAdaptiveExtension(Replication.class);
        return replica.join(directory);
    }
    
    private Distributions(){
    }
}
