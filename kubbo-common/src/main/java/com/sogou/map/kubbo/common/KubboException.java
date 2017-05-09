/**
 * 
 */
package com.sogou.map.kubbo.common;

/**
 * KubboException
 * 
 * @author liufuliang
 *
 */
public class KubboException extends Exception {

    static final long serialVersionUID = -606181585068444342L;

    public KubboException(String message) {
        super(message);
    }

    public KubboException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubboException(Throwable cause) {
        super(cause);
    }
}
