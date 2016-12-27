/**
 * 
 */
package com.sogou.map.kubbo.boot.configuration.element;

/**
 * @author liufuliang
 *
 */
public class ServerElement implements Configuration{
	private static final long serialVersionUID = 1L;
	public static final String TAG = "server";
	
	String bind;

	public String getBind() {
		return bind;
	}

	public void setBind(String bind) {
		this.bind = bind;
	}
	
	
	
}
