/**
 * 
 */
package com.sogou.map.kubbo.configuration;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author liufuliang
 *
 */
public class PropertiesConfiguratorTest {
    public static void main(String[] args){
        new PropertiesConfiguratorTest().testConfigure();
    }
    @Test
    public void testConfigure(){
        PropertiesConfigurator.configure(PropertiesConfiguratorTest.class.getResourceAsStream("/kubbo.properties"));
        KubboConfiguration configuration = KubboConfiguration.getInstance();
        Assert.assertTrue(configuration.isConfigured());
    }

}
