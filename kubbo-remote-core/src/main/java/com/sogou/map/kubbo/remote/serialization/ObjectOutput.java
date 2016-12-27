package com.sogou.map.kubbo.remote.serialization;

import java.io.IOException;

/**
 * Object output.
 * 
 * @author liufuliang
 */
public interface ObjectOutput extends DataOutput {

	/**
	 * write object.
	 * 
	 * @param obj object.
	 */
	void writeObject(Object obj) throws IOException;

}