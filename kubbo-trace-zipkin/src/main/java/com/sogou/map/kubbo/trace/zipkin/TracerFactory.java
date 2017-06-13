/**
 * 
 */
package com.sogou.map.kubbo.trace.zipkin;

import com.sogou.map.kubbo.common.URL;

import brave.Tracing;

/**
 * @author liufuliang
 *
 */
public interface TracerFactory {
    Tracing create(URL address);
}
