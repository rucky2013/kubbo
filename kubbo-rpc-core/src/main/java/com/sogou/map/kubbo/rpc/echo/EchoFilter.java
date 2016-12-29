package com.sogou.map.kubbo.rpc.echo;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.extension.Activate;
import com.sogou.map.kubbo.rpc.Filter;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.RpcResult;

/**
 * EchoFilter
 * 
 * @author liufuliang
 */
@Activate(group = Constants.PROVIDER, order = -110000)
public class EchoFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
        if(inv.getMethodName().equals(Constants.$ECHO) 
                && inv.getArguments() != null 
                && inv.getArguments().length == 1 )
            return new RpcResult(inv.getArguments()[0]);
        return invoker.invoke(inv);
    }

}