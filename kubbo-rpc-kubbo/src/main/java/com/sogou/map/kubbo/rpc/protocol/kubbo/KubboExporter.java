package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.util.Map;

import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.protocol.AbstractExporter;

/**
 * KubboExporter
 * 
 * @author liufuliang
 */
public class KubboExporter<T> extends AbstractExporter<T> {

    private final String                        key;

    private final Map<String, Exporter<?>> exporterMap;

    public KubboExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap){
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
    }

    @Override
    public void unexport() {
        super.unexport();
        exporterMap.remove(key);
    }

}