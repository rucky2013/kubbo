/**
 * 
 */
package com.sogou.map.kubbo.trace.zipkin;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.util.NetUtils;

import brave.Tracing;
import brave.sampler.Sampler;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Sender;
import zipkin.reporter.urlconnection.URLConnectionSender;

/**
 * @author liufuliang
 *
 */
public class ZipkinTracerFactory implements TracerFactory{
    
    @Override
    public Tracing create(URL address){
        String application = address.getParameter(Constants.APPLICATION_KEY, NetUtils.getHostAddress());        

        Sender sender = URLConnectionSender.create(address.setProtocol("http").toIdentityString() + "/api/v1/spans");
        
        AsyncReporter<zipkin.Span> reporter = AsyncReporter.create(sender);
        
        SamplerFactory samplerFactory = getSamplerFactory();
        Sampler sampler = samplerFactory.create(address);
        
        // Create a tracing component with the service name you want to see in Zipkin.
        Tracing tracing = Tracing.newBuilder()
                         .localServiceName(application)
                         .reporter(reporter)
                         .sampler(sampler)
                         .build();
        
        return tracing;
    }
    
    private SamplerFactory getSamplerFactory(){
        return new ZipkinSamplerFactory();
    }
}
