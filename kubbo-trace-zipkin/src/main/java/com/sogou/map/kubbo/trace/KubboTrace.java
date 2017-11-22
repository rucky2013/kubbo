/**
 * 
 */
package com.sogou.map.kubbo.trace;


import com.sogou.map.kubbo.common.Version;
import com.sogou.map.kubbo.trace.zipkin.SharedTracer;

import brave.Tracer;
import brave.Span;
import brave.Span.Kind;
import brave.Tracer.SpanInScope;

/**
 * @author liufuliang
 *
 */
public class KubboTrace {
        
    public static String traceId(){
        if(SharedTracer.isTraceEnabled()) {
            Tracer tracer = SharedTracer.instance();
            Span currentSpan = tracer.currentSpan();
            if(currentSpan != null) {
                return currentSpan.context().traceIdString();
            }
        }
        return "";
    }
    
    public static Trace trace(String operationName){
        if(!SharedTracer.isTraceEnabled()){
            return Trace.NOOP;
        }
        Tracer tracer = SharedTracer.instance();
        final brave.Span span = tracer.nextSpan()
                .kind(Kind.SERVER)
                .name(operationName)
                .tag("kubbo.version.server", Version.getVersion()).start();
        final SpanInScope scope = tracer.withSpanInScope(span);
        
        return new Trace(){
            @Override
            public void finish() {
                span.finish();
                scope.close();
            }

            @Override
            public void close() {
                finish();
            }
        };
    }
    
    
    private KubboTrace(){
    }
}
