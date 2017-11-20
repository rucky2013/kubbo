package com.sogou.map.kubbo.trace.interceptor;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.Version;
import com.sogou.map.kubbo.common.extension.Activate;
import com.sogou.map.kubbo.rpc.Interceptor;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Invoker.Kind;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcContext;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.RpcResult;
import com.sogou.map.kubbo.rpc.concurrent.FutureListener;
import com.sogou.map.kubbo.trace.zipkin.SharedTracer;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.propagation.TraceContextOrSamplingFlags;

/**
 * TraceInterceptor
 * 
 * @author liufuliang
 */
@Activate(group = { Constants.PROVIDER, Constants.CONSUMER }, order = 2)
public class TraceInterceptor implements Interceptor {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (SharedTracer.isTraceEnabled()) {
            if (invoker.kind() == Kind.PROVIDER) { // server
                return traceServer(invoker, invocation);
            } else { // client
                return traceClient(invoker, invocation);
            }
        }

        // invoke chain
        return invoker.invoke(invocation);
    }

    private Result traceClient(Invoker<?> invoker, Invocation invocation) {        
        Tracer tracer = SharedTracer.instance();
        String method = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();
        final Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(method)
                .tag("kubbo.version.client", Version.getVersion())
                .tag("kubbo.reference", invoker.getInterface().getCanonicalName())
                .start();
        SharedTracer.injector().inject(span.context(), invocation.getAttachments());
                
        Result result = null;
        try {
            result = invoker.invoke(invocation);
            if (result == RpcResult.ASYNC) {
                RpcContext.get().getFuture().addListener(new FutureListener<Object>() {
                    @Override
                    public void done(Object result) {
                        span.finish();
                    }

                    @Override
                    public void caught(Throwable exception) {
                        span.finish();
                    }
                });
            }
            return result;
        } finally {
            if (result == RpcResult.ONEWAY) {
                span.flush();
            } else if (result != RpcResult.ASYNC) {
                span.finish();
            }
        }
    }

    private Result traceServer(Invoker<?> invoker, Invocation invocation) {
        Tracer tracer = SharedTracer.instance();
        String method = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();

        TraceContextOrSamplingFlags contextOrFlags = SharedTracer.extractor().extract(invocation.getAttachments());
        Span span = contextOrFlags.context() != null ? tracer.joinSpan(contextOrFlags.context())
                : tracer.newTrace(contextOrFlags.samplingFlags());
        span.kind(Span.Kind.SERVER)
            .name(method)
            .tag("kubbo.version.server", Version.getVersion())
            .start();
        
        SpanInScope scope = tracer.withSpanInScope(span);
        try {
            return invoker.invoke(invocation);
        } finally {
            span.finish();
            scope.close();
        }
    }
}