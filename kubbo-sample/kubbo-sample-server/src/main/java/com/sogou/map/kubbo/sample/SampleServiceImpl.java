/**
 * 
 */
package com.sogou.map.kubbo.sample;

import com.sogou.map.kubbo.boot.annotation.Export;


/**
 * @author liufuliang
 *
 */
@Export(SampleService.class)
public class SampleServiceImpl implements SampleService {

    @Override
    public Message echo(Message message) {
        return new Message("echo: " + message);
    }
}
