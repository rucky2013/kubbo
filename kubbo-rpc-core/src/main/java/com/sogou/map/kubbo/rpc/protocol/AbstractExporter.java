package com.sogou.map.kubbo.rpc.protocol;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Invoker;

/**
 * AbstractExporter.
 * 
 * @author liufuliang
 */
public abstract class AbstractExporter<T> implements Exporter<T> {

    protected final Logger   logger     = LoggerFactory.getLogger(AbstractExporter.class);

    private final Invoker<T> invoker;

    private volatile boolean unexported = false;

    public AbstractExporter(Invoker<T> invoker) {
        if (invoker == null)
            throw new IllegalStateException("service invoker == NULL");
        if (invoker.getInterface() == null)
            throw new IllegalStateException("service type == NULL");
        if (invoker.getUrl() == null)
            throw new IllegalStateException("service url == NULL");
        this.invoker = invoker;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public void unexport() {
        if (unexported) {
            return ;
        }
        unexported = true;
        getInvoker().destroy();
    }

    @Override
    public String toString() {
        return getInvoker().toString();
    }

}