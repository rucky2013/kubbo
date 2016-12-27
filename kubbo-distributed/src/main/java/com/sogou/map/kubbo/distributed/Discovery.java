package com.sogou.map.kubbo.distributed;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Adaptive;
import com.sogou.map.kubbo.common.extension.SPI;

/** (SPI Singleton ThreadSafe)
 * @author liufuliang
 *
 */
@SPI("etcd")
public interface Discovery {
	@Adaptive(Constants.DISCOVERY_KEY)
	<T> Directory<T> subscribe(Class<T> type, URL url);
}
