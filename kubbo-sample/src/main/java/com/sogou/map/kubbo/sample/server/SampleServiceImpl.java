/**
 * 
 */
package com.sogou.map.kubbo.sample.server;

import com.sogou.map.kubbo.boot.annotation.Export;
import com.sogou.map.kubbo.common.utils.NetUtils;
import com.sogou.map.kubbo.sample.api.SampleService;

/**
 * @author liufuliang
 *
 */
@Export(SampleService.class)
public class SampleServiceImpl implements SampleService {

    @Override
    public String echo(String message) {
        return "echo: " + message + ", from " + NetUtils.getLocalAddress();
    }

    @Override
    public void update(byte[] buf) {
        return;
    }

}
