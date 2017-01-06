package com.sogou.map.kubbo.rpc;

import com.sogou.map.kubbo.common.extension.SPI;

/**
 * ExporterListener. (SPI, Singleton, ThreadSafe)
 * 
 * @author liufuliang
 */
@SPI
public interface ExporterListener {

    /**
     * The exporter exported.
     * 
     * @see com.sogou.map.kubbo.rpc.Protocol#export(Invoker)
     * @param exporter
     * @throws RpcException
     */
    void exported(Exporter<?> exporter) throws RpcException;

    /**
     * The exporter unexported.
     * 
     * @see com.sogou.map.kubbo.rpc.Exporter#unexport()
     * @param exporter
     * @throws RpcException
     */
    void unexported(Exporter<?> exporter);

}