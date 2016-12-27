/**
 * 
 */
package com.sogou.map.kubbo.common.http;

/**
 * @author liufuliang
 *
 */
public class KubboHttpException extends Exception {

	private static final long serialVersionUID = 6831076217953900452L;

	public KubboHttpException(String message) {
        super(message);
    }

    public KubboHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubboHttpException(Throwable cause) {
        super(cause);
    }
}
