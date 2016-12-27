package com.sogou.map.kubbo.bench;

/**
 * 
 * @author liufuliang
 *
 */
public class Options {
	public static final Options DEFAULT = new Options(1, 1);

	/**
	 * the concurrency level
	 */
	private int concurrency;

	/**
	 * the total number of requests
	 */
	private int total;

	public Options(){
		
	}
	
	public Options(int concurrency, int total) {
		this.concurrency = concurrency;
		this.total = total;
	}

	public int getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
