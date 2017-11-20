package com.sogou.map.kubbo.rpc.interceptors;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.extension.Activate;
import com.sogou.map.kubbo.common.json.JSONArray;
import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.rpc.Interceptor;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * AccessLogInterceptor
 * 
 * @author liufuliang
 */
@Activate(group = Constants.PROVIDER, order = 3, value = Constants.ACCESSLOG_KEY)
public class AccessLogInterceptor implements Interceptor {
    
    private static final Logger logger = LoggerFactory.getLogger("kubbo.accesslog");

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String source = invocation.getAttachment(Constants.APPLICATION_KEY, "-");
        String method = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();
        String arguments = "";
        try {
            JSONArray obj = new JSONArray(invocation.getArguments());
            arguments = obj.toString();
        } catch (JSONException e) {}
        
        StringBuffer log = new StringBuffer();
        log.append("Req,").append(method).append(" ").append(source).append(" ").append(arguments);
        
        if(logger.isInfoEnabled()){
            logger.info(log.toString());
        }
        
        return invoker.invoke(invocation);
    }

}