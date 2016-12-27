/**
 * 
 */
package com.sogou.map.kubbo.distributed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liufuliang
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int oldvalue = 1;
		int newvalue = oldvalue + (oldvalue >> 1);
		System.out.println(newvalue);

		
		List<String> list = new ArrayList<String>(1);
		list.add("1");
		list.add("2");
		System.out.println(list);
	}

}
