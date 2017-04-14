/**
 * 
 */
package com.sogou.map.kubbo.sample.server;

import com.sogou.map.kubbo.boot.annotation.Export;
import com.sogou.map.kubbo.common.util.NetUtils;
import com.sogou.map.kubbo.sample.api.Message;
import com.sogou.map.kubbo.sample.api.SampleService;

/**
 * @author liufuliang
 *
 */
@Export(SampleService.class)
public class SampleServiceImpl implements SampleService {

    @Override
    public Message echo(Message message) {
        return new Message("echo: " + message + ", from " + NetUtils.getLocalAddress());
    }
}
