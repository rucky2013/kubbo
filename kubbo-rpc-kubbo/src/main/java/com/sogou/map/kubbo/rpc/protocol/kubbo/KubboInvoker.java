package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.utils.AtomicPositiveInteger;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.TimeoutException;
import com.sogou.map.kubbo.remote.session.SessionClient;
import com.sogou.map.kubbo.remote.session.ResponseFuture;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcContext;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.RpcInvocation;
import com.sogou.map.kubbo.rpc.RpcResult;
import com.sogou.map.kubbo.rpc.protocol.AbstractInvoker;
import com.sogou.map.kubbo.rpc.utils.Rpcs;

/**
 * KubboInvoker
 * 
 * @author liufuliang
 */
public class KubboInvoker<T> extends AbstractInvoker<T> {

    private final SessionClient[]      clients;

    private final AtomicPositiveInteger index = new AtomicPositiveInteger();
            
    private final ReentrantLock     destroyLock = new ReentrantLock();
    
    private final Set<Invoker<?>> invokers;
    
    public KubboInvoker(Class<T> serviceType, URL url, SessionClient[] clients){
        this(serviceType, url, clients, null);
    }
    
    public KubboInvoker(Class<T> serviceType, URL url, SessionClient[] clients, Set<Invoker<?>> invokers){
        super(serviceType, url, new String[] { 
        			Constants.GROUP_KEY, 
        			Constants.VERSION_KEY, 
        			Constants.TOKEN_KEY, 
        			Constants.TIMEOUT_KEY,
        			Constants.APPLICATION_KEY});
        this.clients = clients;
        this.invokers = invokers; 
    }

    @Override
    protected Result doInvoke(final Invocation invocation) throws RpcException {
        RpcInvocation inv = (RpcInvocation) invocation;
        final String methodName = Rpcs.getMethodName(invocation);
        
        //attach path, interface
        inv.setAttachment(Constants.PATH_KEY, getUrl().getPath());
        inv.setAttachment(Constants.INTERFACE_KEY, getInterface().getName());
        
        //select one client
        SessionClient currentClient;
        if (clients.length == 1) {
            currentClient = clients[0];
        } else {
            currentClient = clients[index.getAndIncrement() % clients.length];
        }
        
        try {
            boolean isAsync = Rpcs.isAsync(getUrl(), invocation);
            boolean isOneway = Rpcs.isOneway(getUrl(), invocation);
            int timeout = getUrl().getMethodParameter(methodName, Constants.TIMEOUT_KEY,Constants.DEFAULT_TIMEOUT);
            if (isOneway) {
            	boolean blocking = getUrl().getMethodParameter(methodName, Constants.SEND_BLOCKING_KEY, Constants.DEFAULT_SEND_BLOCKING);
                currentClient.send(inv, blocking);
                RpcContext.getContext().setFuture(null);
                return RpcResult.NULL;
            } else if (isAsync) {
            	ResponseFuture future = currentClient.request(inv, timeout);
                RpcContext.getContext().setFuture(new JDKFutureAdapter<Object>(future));
                return RpcResult.NULL;
            } else {
            	RpcContext.getContext().setFuture(null);
                return (Result) currentClient.request(inv, timeout).get();
            }
        } catch (TimeoutException e) {
            throw new RpcException(RpcException.TIMEOUT_EXCEPTION, "Invoke remote method timeout. method: " + invocation.getMethodName() + ", provider: " + getUrl() + ", cause: " + e.getMessage(), e);
        } catch (RemotingException e) {
            throw new RpcException(RpcException.NETWORK_EXCEPTION, "Failed to invoke remote method: " + invocation.getMethodName() + ", provider: " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isAvailable() {
        if (!super.isAvailable())
            return false;
        for (SessionClient client : clients){
            if (client.isConnected() && !client.hasAttribute(Constants.CHANNEL_ATTRIBUTE_READONLY_KEY)){
                return true ;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        if (super.isDestroyed()){
            return;
        } else {
            //double check ,避免多次关闭
            destroyLock.lock();
            try{
                if (super.isDestroyed()){
                    return ;
                }
                super.destroy();
                if (invokers != null){
                    invokers.remove(this);
                }
                for (SessionClient client : clients) {
                    try {
                        client.close();
                    } catch (Throwable t) {
                        logger.warn(t.getMessage(), t);
                    }
                }
                
            }finally {
                destroyLock.unlock();
            }
        }
    }
}