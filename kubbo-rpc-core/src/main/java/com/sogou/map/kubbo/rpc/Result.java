package com.sogou.map.kubbo.rpc;

/**
 * RPC invoke result. 
 * @author liufuliang
 */
public interface Result extends Attachable{

	/**
	 * Get invoke result.
	 * 
	 * @return result. if no result return null.
	 */
	Object getValue();

    /**
     * Has exception.
     * 
     * @return has exception.
     */
    boolean hasException();
    
	/**
	 * Get exception.
	 * 
	 * @return exception. if no exception return null.
	 */
	Throwable getException();


    /**
     * Recreate.
     * 
     * <code>
     * if (hasException()) {
     *     throw getException();
     * } else {
     *     return getValue();
     * }
     * </code>
     * 
     * @return result.
     * @throws if has exception throw it.
     */
    Object recreate() throws Throwable;



}