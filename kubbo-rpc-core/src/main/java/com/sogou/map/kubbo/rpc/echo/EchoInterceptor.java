package com.sogou.map.kubbo.rpc.echo;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.extension.Activate;
import com.sogou.map.kubbo.rpc.Interceptor;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.RpcResult;

/**
 * EchoInterceptor
 * 
 * @author liufuliang
 */
@Activate(group = Constants.PROVIDER, order = -10000)
public class EchoInterceptor implements Interceptor {

    public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
        if(inv.getMethodName().equals(Constants.$ECHO) 
                && inv.getArguments() != null 
                && inv.getArguments().length == 1 ){
            return new RpcResult(inv.getArguments()[0]);
        }
        return invoker.invoke(inv);
    }

}