/**
 * 
 */
package com.sogou.map.kubbo.boot.configuration;

import java.util.ArrayList;
import java.util.List;

import com.sogou.map.kubbo.boot.configuration.element.Configuration;
import com.sogou.map.kubbo.boot.configuration.element.ReferenceElement;
import com.sogou.map.kubbo.boot.configuration.element.ServerElement;

/**
 * -Dkubbo.configuration=xxx
 * classpath根目录
 * setConfiguration()
 * @author liufuliang
 *
 */
public class KubboConfiguration implements Configuration{

    private static final long serialVersionUID = 1L;
    
    volatile boolean configured = false;
    
    List<ReferenceElement> references = new ArrayList<ReferenceElement>(10);
    ServerElement server;
    
    public void addReferenceElement(ReferenceElement reference){
        references.add(reference);
    }
    
    public void setServerElement(ServerElement server){
        this.server = server;
    }
    public String getServerBind(){
        return server == null ? null : server.getBind();
    }
    public boolean isConfigured(){
        return this.configured;
    }
    public String getReferenceAddress(String interfaceType){
        return getReferenceAddress(interfaceType, null);
    }
    
    public String getReferenceAddress(String interfaceType, String name){
        for(ReferenceElement reference : references){
            if(reference.getInterfaceType().equals(interfaceType)){
                if(name == null 
                        || (reference.getName() !=null && reference.getName().equals(name))){
                    return reference.getAddress();
                }
            }
        }
        return null;
    }
    
    
    private KubboConfiguration(){}
    
    /*
     * singletone
     */
    public static class SingletonHolder {  
        private static KubboConfiguration instance = new KubboConfiguration();
    }
    public static KubboConfiguration getInstance() {  
        return SingletonHolder.instance;  
    }  

}
