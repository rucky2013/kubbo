package com.sogou.map.kubbo.rpc;

/**
 * Invocation. 
 * @author liufuliang
 */
public interface Invocation extends Attachable{

	/**
	 * get method name.
	 * 
	 * @serial
	 * @return method name.
	 */
	String getMethodName();

	/**
	 * get parameter types.
	 * 
	 * @serial
	 * @return parameter types.
	 */
	Class<?>[] getParameterTypes();

	/**
	 * get arguments.
	 * 
	 * @serial
	 * @return arguments.
	 */
	Object[] getArguments();

    /**
     * get the invoker in current context.
     * 
     * @transient
     * @return invoker.
     */
    Invoker<?> getInvoker();

}