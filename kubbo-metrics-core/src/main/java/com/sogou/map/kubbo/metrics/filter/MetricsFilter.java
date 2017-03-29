package com.sogou.map.kubbo.metrics.filter;

import java.util.concurrent.ConcurrentHashMap;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Activate;
import com.sogou.map.kubbo.common.extension.ExtensionLoader;
import com.sogou.map.kubbo.common.utils.SystemPropertyUtils;
import com.sogou.map.kubbo.metrics.Metric;
import com.sogou.map.kubbo.metrics.Reporter;
import com.sogou.map.kubbo.metrics.ReporterFactory;
import com.sogou.map.kubbo.metrics.FrequencyElapsedRecorder;
import com.sogou.map.kubbo.metrics.SharedMetricsRegistry;
import com.sogou.map.kubbo.rpc.Filter;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;

 
/**
 * MetricsFilter
 * 
 * @author liufuliang
 */
@Activate(group = { Constants.PROVIDER})
public class MetricsFilter implements Filter {    
    private Reporter reporter;
    private static final ConcurrentHashMap<String, FrequencyElapsedRecorder> RECORDERS = new ConcurrentHashMap<String, FrequencyElapsedRecorder>();
    private static final String metricsAddress = SystemPropertyUtils.get(Constants.METRICS_ADDRESS_SYSTEM_PROPERTY, "");

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(!metricsAddress.isEmpty()){
            // create reporter
            if(reporter == null){
                URL url = invoker.getUrl();
                ReporterFactory reporterFactory = getReporterFactory(url);
                reporter = reporterFactory.create(URL.valueOf(metricsAddress));
            }
            
            long start = System.currentTimeMillis();
            try{
                // invoke
                Result result = invoker.invoke(invocation);
                return result;
            } finally{
                // mark response
                long end = System.currentTimeMillis();
                mark(invoker, invocation, (end - start));
            }
        }
        // invoke chain
        return invoker.invoke(invocation);
    }
    
    private void mark(Invoker<?> invoker, Invocation invocation, long time){
        if(reporter != null){
            String method = invoker.getInterface().getCanonicalName() + "." + invocation.getMethodName();
            FrequencyElapsedRecorder recorder = RECORDERS.get(method);
            if(recorder == null){
                FrequencyElapsedRecorder rec = SharedMetricsRegistry.instance().frequencyElapsedRecorder(method);
                rec.setAttribute(Metric.MEASUREMENT_KEY, Metric.FREQUENCY_ELAPSED);
                rec.setAttribute(Metric.TAG_METHOD, method);
                RECORDERS.putIfAbsent(method, rec);
                recorder = RECORDERS.get(method);
            }
            recorder.mark(time);
        } 
    }

    
    public static ReporterFactory getReporterFactory(URL url) {
        String type = url.getParameter(Constants.METRICS_KEY, Constants.DEFAULT_METRICES);
        return getReporterFactory(type);
    }

    public static ReporterFactory getReporterFactory(String type) {
        return ExtensionLoader.getExtensionLoader(ReporterFactory.class).getExtension(type);
    }

 

}