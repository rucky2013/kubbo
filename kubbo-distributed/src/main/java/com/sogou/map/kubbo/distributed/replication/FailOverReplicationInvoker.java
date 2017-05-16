package com.sogou.map.kubbo.distributed.replication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.NetUtils;
import com.sogou.map.kubbo.distributed.Directory;
import com.sogou.map.kubbo.distributed.LoadBalance;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;


/**
 * 失败转移，当出现失败，重试其它服务器，通常用于读操作，但重试会带来更长延迟。
 * 
 * <a href="http://en.wikipedia.org/wiki/Failover">Failover</a>
 * 
 * @author liufuliang
 */
public class FailOverReplicationInvoker<T> extends AbstractReplicationInvoker<T> {

    private static final Logger logger = LoggerFactory.getLogger(FailOverReplicationInvoker.class);

    public FailOverReplicationInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    public Result doInvoke(Invocation invocation, final List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        List<Invoker<T>> copyinvokers = invokers;
        
        //尝试次数
        int tries = getUrl().getMethodParameter(invocation.getMethodName(), Constants.RETRY_KEY, Constants.DEFAULT_RETRY) + 1;
        if (tries <= 0) {
            tries = 1;
        }
        
        // retry loop.
        RpcException exception = null;
        List<Invoker<T>> invoked = new ArrayList<Invoker<T>>(copyinvokers.size()); // invoked invokers.
        Set<String> providers = new HashSet<String>(tries);
        for (int i = 0; i < tries; i++) {
            //重试时，进行重新选择，避免重试时invoker列表已发生变化.
            //注意：如果列表发生了变化，那么invoked判断会失效，因为invoker示例已经改变
            if (i > 0) {
                copyinvokers = list(invocation);
            }
            Invoker<T> invoker = select(invocation, copyinvokers, invoked, loadbalance);
            invoked.add(invoker);
            
            try {
                Result result = invoker.invoke(invocation);
                if (exception != null && logger.isWarnEnabled()) {
                    logger.warn("Although retry the method " + invocation.getMethodName()
                            + " in the service " + getInterface().getName()
                            + " was successful by the provider " + invoker.getUrl().getAddress()
                            + ", but there have been failed providers " + providers 
                            + " (" + providers.size() + "/" + copyinvokers.size()
                            + ") from the discovery " + directory.getUrl().getAddress()
                            + " on the consumer " + NetUtils.getHostAddress()
                            + " Last error is: " + exception.getMessage(), exception);
                }
                return result;
            } catch (RpcException e) {
                if (e.isBiz()) { // biz exception.
                    throw e;
                }
                exception = e;
            } catch (Throwable e) {
                exception = new RpcException(e.getMessage(), e);
            } finally {
                providers.add(invoker.getUrl().getAddress());
            }
        }
        throw new RpcException(exception.getCode(), 
                "Failed to invoke the method "+ invocation.getMethodName() 
                + " in the service " + getInterface().getName() 
                + ". Tried " + tries + " times of the providers " + providers 
                + " (" + providers.size() + "/" + copyinvokers.size() 
                + ") from the discovery " + directory.getUrl().getAddress()
                + " on the consumer " + NetUtils.getHostAddress()
                + ". Last error is: " + exception.getMessage(), exception);
        }

}