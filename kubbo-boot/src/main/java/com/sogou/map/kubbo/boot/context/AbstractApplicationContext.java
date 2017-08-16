/**
 * 
 */
package com.sogou.map.kubbo.boot.context;

import com.sogou.map.kubbo.common.AbstractAttributable;
import com.sogou.map.kubbo.configuration.KubboConfiguration;
import com.sogou.map.kubbo.configuration.element.ApplicationConfiguration;

/**
 * @author fuliangliu
 *
 */
public class AbstractApplicationContext extends AbstractAttributable<Object> implements ApplicationContext{
    @Override
    public String getApplicationHome() {
        ApplicationConfiguration application = KubboConfiguration.getInstance().getApplication();
        return application.getHome();
    }
}
