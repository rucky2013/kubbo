package com.sogou.map.kubbo.rpc;

/**
 * Invocation. 
 * @author liufuliang
 */
public interface Invocation extends Attachable{

    /**
     * get method name.
     * 
     * @return method name.
     */
    String getMethodName();

    /**
     * get parameter types.
     * 
     * @return parameter types.
     */
    Class<?>[] getParameterTypes();

    /**
     * get arguments.
     * 
     * @return arguments.
     */
    Object[] getArguments();

    /**
     * get the invoker in current context.
     * 
     * @return invoker.
     */
    Invoker<?> getInvoker();

}