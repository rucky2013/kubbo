/**
 * 
 */
package com.sogou.map.kubbo.remote.serialization.kryo.obj;

import java.io.Serializable;

/**
 * @author liufuliang
 *
 */
public class Label implements Serializable{
    public static final Color COLOR_1       = new Color(38, 162, 224);
    public static final Color COLOR_2       = new Color(253, 96, 60);
    
	public static final Label TIME_LESS 			= new Label("时间短", COLOR_1, 1);
	public static final Label NORMAL 				= new Label("走常规", COLOR_1, 2);
	public static final Label LENGTH_LESS 			= new Label("距离短", COLOR_1, 3);
	public static final Label JAM_LESS 				= new Label("拥堵少", COLOR_1, 4);
	public static final Label TRAFFIC_LIGHT_LESS 	= new Label("红绿灯少", COLOR_1, 5);
	public static final Label FERRY 				= new Label("轮渡", COLOR_2);
	


	
	protected String title;
	protected Color color;
	protected int priority = Integer.MAX_VALUE; //less is good
	public Label(){
		
	}
	public Label(String title, Color color) {
		super();
		this.title = title;
		this.color = color;
	}
	public Label(String title, Color color, int priority) {
		super();
		this.title = title;
		this.color = color;
		this.priority = priority;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
