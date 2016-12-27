/**
 * 
 */
package com.sogou.map.kubbo.boot.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.utils.SystemPropertyUtils;

/**
 * @author fuliangliu
 *
 */
public class AbstractApplicationContext implements ApplicationContext{
	private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	@Override
	public String getApplicationHome() {
		return SystemPropertyUtils.get(Constants.APPLICATION_HOME_SYSTEM_PROPERTY, ".");

	}

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if (value == null) { // The null value unallowed in the ConcurrentHashMap.
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    @Override
	public void removeAttributes(){
    	attributes.clear();
    }

}
