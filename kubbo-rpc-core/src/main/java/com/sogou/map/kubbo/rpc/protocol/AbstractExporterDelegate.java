/**
 * 
 */package com.sogou.map.kubbo.rpc.protocol;

import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.ExporterDelegate;
import com.sogou.map.kubbo.rpc.Invoker;

/**
 * @author liufuliang
 *
 */
public class AbstractExporterDelegate<T> implements ExporterDelegate<T> {
	protected Exporter<T> exporter;

	public AbstractExporterDelegate(Exporter<T> exporter) {
        if (exporter == null) {
            throw new IllegalArgumentException("exporter == NULL");
        }
		this.exporter = exporter;
	}

	@Override
	public Invoker<T> getInvoker() {
		return exporter.getInvoker();
	}

	@Override
	public void unexport() {
		exporter.unexport();
	}

	@Override
	public Exporter<T> getExporter() {
		return exporter;
	}
	
    @Override
    public String toString() {
        return exporter.toString();
    }

}
