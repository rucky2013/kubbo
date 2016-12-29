package com.sogou.map.kubbo.rpc;

/**
 * InvokerDelegate
 * @author liufuliang
 */
public interface ExporterDelegate<T> extends Exporter<T> {
    Exporter<T> getExporter();

}