/**
 * 
 */
package com.sogou.map.kubbo.trace.zipkin;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;

import brave.sampler.Sampler;

/**
 * @author liufuliang
 *
 */
public class ZipkinSamplerFactory implements SamplerFactory{

    @Override
    public Sampler create(URL address) {
        String name = address.getParameter(Constants.SAMPLER_KEY, FrequencySampler.name);   

        if(name.equalsIgnoreCase(FrequencySampler.name)){
            int interval = address.getParameter(Constants.INTERVAL_KEY, FrequencySampler.DEFAULT_INTERVALE);   
            return new FrequencySampler(interval);
        } else if(name.equalsIgnoreCase("always")){
            return Sampler.ALWAYS_SAMPLE;
        } else if(name.equalsIgnoreCase("never")){
            return Sampler.NEVER_SAMPLE;
        } else {
            return new FrequencySampler();
        }
    }

}
