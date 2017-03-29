/**
 * 
 */
package com.sogou.map.kubbo.common.escape;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author liufuliang
 *
 */
public class EscapersTest {
    @Test
    public void test(){
        Escaper escaper = Escapers.builder()
                .addEscape(',', "\\,")
                .addEscape('=', "\\=")
                .build();
        Assert.assertEquals(escaper.escape("app=navi,biz=mssp"), "app\\=navi\\,biz\\=mssp");
    }
}
