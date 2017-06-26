package com.sogou.map.kubbo.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.sogou.map.kubbo.common.lang.Defaults;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RpcInvocation;

/**
 * InvokerInvocationHandler
 * 
 * @author liufuliang
 */
public class InvokerInvocationHandler implements InvocationHandler {

    private final Invoker<?> invoker;
    
    public InvokerInvocationHandler(Invoker<?> handler){
        this.invoker = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
                
        Object result = invoker.invoke(new RpcInvocation(method, args)).recreate();
        if(result == null){
            // 异步调用会直接返回null, 对于原始类型, 需要返回默认值.
            Class<?> returnType = method.getReturnType();
            if(returnType.isPrimitive()){
                return Defaults.defaultValue(returnType);
            }
        }
        
        return result;
    }

}