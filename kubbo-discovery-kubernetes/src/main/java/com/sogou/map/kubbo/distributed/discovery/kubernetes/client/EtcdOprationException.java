/**
 * 
 */
package com.sogou.map.kubbo.distributed.discovery.kubernetes.client;

import com.sogou.map.kubbo.common.KubboException;

/**
 * @author liufuliang
 *
 */
public class EtcdOprationException extends KubboException{

	private static final long serialVersionUID = -603616854565733593L;

	public EtcdOprationException(String message) {
        super(message);
    }

    public EtcdOprationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtcdOprationException(Throwable cause) {
        super(cause);
    }
}
