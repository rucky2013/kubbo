/**
 * 
 */
package com.sogou.map.kubbo.boot.configuration.element;

import com.sogou.map.kubbo.boot.configuration.Configuration;

/**
 * @author liufuliang
 *
 */
public class ReferenceConfiguration implements Configuration{
    private static final long serialVersionUID = 1L;
    public static final String TAG = "reference";
    
    String name;
    String interfaceType;
    String address;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getInterfaceType() {
        return interfaceType;
    }
    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }
    
}
