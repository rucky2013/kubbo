/**
 * 
 */
package com.sogou.map.kubbo.distributed.discovery.kubernetes;

import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.distributed.Directory;
import com.sogou.map.kubbo.distributed.Discovery;


/**
 * @author liufuliang
 *
 */
public class EtcdDiscovery implements Discovery {
    public final static String NAME = "etcd";

    @Override
    public <T> Directory<T> subscribe(Class<T> type, URL url) {
        EtcdDiscoveryDirectory<T> directory = new EtcdDiscoveryDirectory<T>(type, url);
        directory.synchronize();
        directory.watchUpdate();
        return directory;
    }

}
