/**
 * 
 */
package com.sogou.map.kubbo.common.io;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author liufuliang
 *
 */
public class BytesTest {
    @Test
    public void testBase64(){
        String base64 = Bytes.bytes2base64("hello".getBytes());
        Assert.assertEquals("hello", new String(Bytes.base642bytes(base64)));
    }
}
