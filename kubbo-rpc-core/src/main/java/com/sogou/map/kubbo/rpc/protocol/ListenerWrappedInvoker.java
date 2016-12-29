/**
 * 
 */package com.sogou.map.kubbo.rpc.protocol;

import java.util.List;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.InvokerListener;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * @author liufuliang
 *
 */
public class ListenerWrappedInvoker<T> extends AbstractInvokerDelegate<T>{

    private static final Logger logger = LoggerFactory.getLogger(ListenerWrappedInvoker.class);
    
    private final List<InvokerListener> listeners;

    public ListenerWrappedInvoker(Invoker<T> invoker, List<InvokerListener> listeners){
        super(invoker);
        this.listeners = listeners;
        if (listeners != null && listeners.size() > 0) {
            for (InvokerListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.referred(invoker);
                    } catch (RpcException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }

    @Override
    public void destroy() {
        try {
            invoker.destroy();
        } finally {
            if (listeners != null && listeners.size() > 0) {
                for (InvokerListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.destroyed(invoker);
                        } catch (Throwable t) {
                            logger.error(t);
                        }
                    }
                }
            }
        }
    }
}
