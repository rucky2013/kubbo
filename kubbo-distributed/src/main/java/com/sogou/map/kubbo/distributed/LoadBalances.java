/**
 * 
 */
package com.sogou.map.kubbo.distributed;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;

/**
 * @author liufuliang
 *
 */
public class LoadBalances {
    public static LoadBalance getExtension(URL url) {
        return Extensions.getExtension(url, Constants.LOADBALANCE_KEY, LoadBalance.class);
    }
    
    public static String getExtensionType(URL url){
        return Extensions.getExtensionType(url, Constants.LOADBALANCE_KEY, LoadBalance.class);
    }

    public static LoadBalance getExtension(String type) {
        return Extensions.getExtension(type, LoadBalance.class);
    }
    
    public static LoadBalance getDefaultExtension(){
        return Extensions.getDefaultExtension(LoadBalance.class);
    }
    
    public static LoadBalance getExtension(URL url, String methodName){
        String loadBalanceType = url.getMethodParameter(methodName, Constants.LOADBALANCE_KEY, "");
        if(loadBalanceType.isEmpty()){
            return getDefaultExtension();
        } else{
            return getExtension(loadBalanceType);
        }
    }
    
    private LoadBalances(){}
}
