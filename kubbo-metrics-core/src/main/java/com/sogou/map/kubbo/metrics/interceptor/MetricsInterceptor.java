package com.sogou.map.kubbo.metrics.interceptor;

import java.util.concurrent.ConcurrentHashMap;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.extension.Activate;
import com.sogou.map.kubbo.metrics.Metric;
import com.sogou.map.kubbo.metrics.FrequencyElapsedRecorder;
import com.sogou.map.kubbo.metrics.SharedMetricsRegistry;
import com.sogou.map.kubbo.rpc.Interceptor;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

/**
 * MetricsInterceptor
 * 
 * @author liufuliang
 */
@Activate(group = { Constants.PROVIDER }, order = 1)
public class MetricsInterceptor implements Interceptor {
    private static final ConcurrentHashMap<String, FrequencyElapsedRecorder> RECORDERS = new ConcurrentHashMap<String, FrequencyElapsedRecorder>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (SharedMetricsRegistry.isReportEnabled()) {
            long start = System.currentTimeMillis();
            try {
                // invoke
                return invoker.invoke(invocation);
            } finally {
                // mark response
                long end = System.currentTimeMillis();
                mark(invoker, invocation, (end - start));
            }
        }
        // invoke chain
        return invoker.invoke(invocation);
    }

    private void mark(Invoker<?> invoker, Invocation invocation, long time) {
        String method = invoker.getInterface().getCanonicalName() + "." + invocation.getMethodName();
        FrequencyElapsedRecorder recorder = RECORDERS.get(method);
        if (recorder == null) {
            FrequencyElapsedRecorder rec = SharedMetricsRegistry.instance().frequencyElapsedRecorder(method);
            rec.setAttribute(Metric.MEASUREMENT_KEY, Metric.FREQUENCY_ELAPSED);
            rec.setAttribute(Metric.TAG_METHOD, method);
            RECORDERS.putIfAbsent(method, rec);
            recorder = RECORDERS.get(method);
        }
        recorder.mark(time);
    }
}