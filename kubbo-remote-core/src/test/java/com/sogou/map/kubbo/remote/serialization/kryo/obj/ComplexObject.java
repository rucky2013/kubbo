/**
 * 
 */
package com.sogou.map.kubbo.remote.serialization.kryo.obj;

import java.util.ArrayList;
import java.util.List;


/**
 * @author liufuliang
 *
 */
public class ComplexObject extends Topology{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int tactic = 1;
	
	public List<Label> labels = new ArrayList<Label>(5);
}
