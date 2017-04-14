/**
 * 
 */
package com.sogou.map.kubbo.sample.api;

import java.io.Serializable;

/**
 * @author liufuliang
 *
 */
public class Message implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    String value;

    public Message(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    

}
