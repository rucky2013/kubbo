/**
 * 
 */
package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;

/**
 * @author liufuliang
 *
 */
public class Protocols {
    public static Protocol getExtension(URL url) {
        return Extensions.getExtension(url, Constants.PROTOCOL_KEY, Protocol.class);
    }
    
    public static String getExtensionType(URL url){
        return Extensions.getExtensionType(url, Constants.PROTOCOL_KEY, Protocol.class);
    }

    public static Protocol getExtension(String type) {
        return Extensions.getExtension(type, Protocol.class);
    }

    private Protocols() {}
}
