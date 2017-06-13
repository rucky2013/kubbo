/**
 * 
 */
package com.sogou.map.kubbo.trace.zipkin;

import com.sogou.map.kubbo.common.URL;

import brave.sampler.Sampler;

/**
 * @author liufuliang
 *
 */
public interface SamplerFactory {
    Sampler create(URL address);
}
