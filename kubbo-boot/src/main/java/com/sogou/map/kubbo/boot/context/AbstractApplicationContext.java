/**
 * 
 */
package com.sogou.map.kubbo.boot.context;

import com.sogou.map.kubbo.common.AbstractAttributable;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.utils.SystemPropertyUtils;

/**
 * @author fuliangliu
 *
 */
public class AbstractApplicationContext extends AbstractAttributable<Object> implements ApplicationContext{
    @Override
    public String getApplicationHome() {
        return SystemPropertyUtils.get(Constants.APPLICATION_HOME_SYSTEM_PROPERTY, ".");

    }
}
