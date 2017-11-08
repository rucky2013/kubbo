package com.sogou.map.kubbo.rpc.filters;

import java.util.concurrent.ConcurrentHashMap;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Activate;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.rpc.Filter;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.RateLimiter;
import com.sogou.map.kubbo.rpc.RateLimiterFactory;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * AccessLogFilter
 * 
 * @author liufuliang
 */
@Activate(group = Constants.PROVIDER, order = -1, value = Constants.LIMITER_RATE_KEY)
public class RateLimitFilter implements Filter {
    
    private static final ConcurrentHashMap<String, RateLimiter> LIMITERS = new ConcurrentHashMap<String, RateLimiter>();
    
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RateLimiter limiter = getRateLimiter(invoker, invocation);
        if(! limiter.acquire()) {
            throw new RpcException(RpcException.CODE_FORBIDDEN,
                    new StringBuilder(64)
                            .append("Forbidden. Reject to invoke ")
                            .append(invoker.getInterface().getCanonicalName())
                            .append(".")
                            .append(invocation.getMethodName())
                            .append(" by rate limiter.")
                            .toString());
        }        
        return invoker.invoke(invocation);
    }
    
    public static RateLimiter getRateLimiter(Invoker<?> invoker, Invocation invocation) {
        String method = invoker.getInterface().getCanonicalName() + "." + invocation.getMethodName();
        RateLimiter limiter = LIMITERS.get(method);
        if(limiter == null) {
            URL url = invoker.getUrl();
            int limitRate = -1;
            int limitInterval = Constants.DEFAULT_LIMITER_INTERVAL;
            
            // global
            if(url.hasParameter(Constants.LIMITER_RATE_KEY)) {
                limitRate = url.getParameter(Constants.LIMITER_RATE_KEY, limitRate);
            }
            if(url.hasParameter(Constants.LIMITER_INTERVAL_KEY)) {
                limitInterval = url.getParameter(Constants.LIMITER_INTERVAL_KEY, limitInterval);
            }
            
            // method specific
            if(url.hasMethodParameter(method, Constants.LIMITER_RATE_KEY)) {
                limitRate = url.getMethodParameter(method, Constants.LIMITER_RATE_KEY, limitRate);
            } 
            if(url.hasMethodParameter(method, Constants.LIMITER_INTERVAL_KEY)) {
                limitInterval = url.getMethodParameter(method, Constants.LIMITER_INTERVAL_KEY, limitInterval);
            }
            
            if(limitRate < 0) {
                limiter = RateLimiter.AlwaysPermit;
            } else if(limitRate == 0) {
                limiter = RateLimiter.AlwaysReject;
            } else {
                RateLimiterFactory factory = Extensions.getExtension(url, Constants.LIMITER_KEY, RateLimiterFactory.class);
                limiter = factory.create(limitInterval, limitRate);
            }
            LIMITERS.putIfAbsent(method, limiter);
            limiter = LIMITERS.get(method);
        }

        return limiter;
    }

}