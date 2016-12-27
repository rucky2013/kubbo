/**
 * 
 */
package com.sogou.map.kubbo.remote.serialization.kryo.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liufuliang
 *
 */
public class Topology implements Serializable{
	public List<Vertext> vertextes = new ArrayList<Vertext>();		//控制点序列
	public List<Edge> edges = new ArrayList<Edge>();			//Link序列
	public Label label;
	
	public void addEdge(Edge e){
		edges.add(e);
	}
	public void addVertext(Vertext v){
		vertextes.add(v);
	}

}
