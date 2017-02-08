package com.sogou.map.kubbo.rpc.protocol;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.ConcurrentHashSet;
import com.sogou.map.kubbo.common.utils.SystemPropertyUtils;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Protocol;
import com.sogou.map.kubbo.rpc.utils.RpcHelper;

/**
 * abstract ProtocolSupport.
 * 
 * @author liufuliang
 */
public abstract class AbstractProtocol implements Protocol {

    protected final Logger logger = LoggerFactory.getLogger(AbstractProtocol.class);

    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>(); // group/path:version -> exporter

    //TODO SOFEREFENCE
    protected final Set<Invoker<?>> invokers = new ConcurrentHashSet<Invoker<?>>();
    
    
//    public Collection<Exporter<?>> getExporters() {
//        return Collections.unmodifiableCollection(exporterMap.values());
//    }
    
    Map<String, Exporter<?>> getExporterMap(){
        return exporterMap;
    }
    
    @Override
    public void destroy() {
        for (Invoker<?> invoker : invokers){
            if (invoker != null) {
                invokers.remove(invoker);
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Destroy reference: " + invoker.getUrl());
                    }
                    invoker.destroy();
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
        for (String key : new ArrayList<String>(exporterMap.keySet())) {
            Exporter<?> exporter = exporterMap.remove(key);
            if (exporter != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Unexport service: " + exporter.getInvoker().getUrl());
                    }
                    exporter.unexport();
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
    }
    
    protected static int getServerShutdownTimeout() {
        return SystemPropertyUtils.getInt(Constants.SHUTDOWN_WAIT_KEY, Constants.DEFAULT_SERVER_SHUTDOWN_TIMEOUT);
    }
    
    
    protected static String serviceKey(Invoker<?> invoker) {
        URL url = invoker.getUrl();
        return RpcHelper.serviceKey(url.getParameter(Constants.GROUP_KEY), 
                          url.getPath(), 
                          invoker.getInterface().getName(),
                          url.getParameter(Constants.VERSION_KEY));
    }


}