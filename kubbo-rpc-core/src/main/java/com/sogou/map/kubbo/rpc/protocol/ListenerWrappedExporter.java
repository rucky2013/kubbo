/**
 * 
 */package com.sogou.map.kubbo.rpc.protocol;

import java.util.List;

import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.ExporterListener;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * @author liufuliang
 *
 */
public class ListenerWrappedExporter<T> extends AbstractExporterDelegate<T>{

    private static final Logger logger = LoggerFactory.getLogger(ListenerWrappedExporter.class);
    
    private final List<ExporterListener> listeners;

    public ListenerWrappedExporter(Exporter<T> exporter, List<ExporterListener> listeners){
    	super(exporter);
        this.listeners = listeners;
        if (listeners != null && listeners.size() > 0) {
            for (ExporterListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.exported(exporter);
                    } catch (RpcException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }
    
    @Override
    public void unexport() {
        try {
            exporter.unexport();
        } finally {
            if (listeners != null && listeners.size() > 0) {
                for (ExporterListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.unexported(this);
                        } catch (Throwable t) {
                            logger.error(t);
                        }
                    }
                }
            }
        }
    }
}
