/**
 * 
 */
package com.sogou.map.kubbo.trace.zipkin;

import java.util.Map;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.configuration.KubboConfiguration;

import brave.Tracer;
import brave.Tracing;
import brave.propagation.Propagation.Getter;
import brave.propagation.Propagation.Setter;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContext.Injector;

/**
 * SharedTracer
 * 
 * @author liufuliang
 *
 */
public class SharedTracer {
    
    private static final Logger LOG = LoggerFactory.getLogger(SharedTracer.class);
    
    private static final KubboConfiguration CONF = KubboConfiguration.getInstance();

    private static final String traceAddress = CONF.getTrace().getAddress();

    static final Getter<Map<String, String>, String> getter = new Getter<Map<String, String>, String>(){
        @Override
        public String get(Map<String, String> carrier, String key) {
            return carrier.get(key);
        }
    };
    
    static final Setter<Map<String, String>, String> setter = new Setter<Map<String, String>, String>(){
        @Override
        public void put(Map<String, String> carrier, String key, String value) {
            carrier.put(key, value);
        }
    };
    
    private SharedTracer() {}
    
    public static class SingletonHolder {
        private static Tracing tracing = initTracing();
        private static Tracer instance = tracing.tracer();
    }

    public static Tracer instance() {
        Tracer tracer = SingletonHolder.instance;
        return tracer;
    }
    
    public static Extractor<Map<String, String>> extractor(){
        return SingletonHolder.tracing.propagation().extractor(getter);
    }
    
    public static Injector<Map<String, String>> injector(){
        return SingletonHolder.tracing.propagation().injector(setter);
    }
    
    public static boolean isTraceEnabled(){
        return !traceAddress.isEmpty();
    }

    
    public static TracerFactory getReporterFactory(URL url) {
        return new ZipkinTracerFactory();
    }
    
    private static Tracing initTracing(){
        if(!isTraceEnabled()){
            return null;
        }
        /*
         * trace address
         */
        URL trace = URL.valueOf(traceAddress);
        String applicationName = CONF.getApplication().getName();
        if(! applicationName.isEmpty()){
            trace = trace.addParameterIfAbsent(Constants.APPLICATION_KEY, applicationName);
        }
        /*
         * create tracer
         */
        TracerFactory tracerFactory = getReporterFactory(trace);
        Tracing tracing = tracerFactory.create(trace);
        
        LOG.info("Trace report to " + trace.toFullString());
        
        return tracing;
    }
}
