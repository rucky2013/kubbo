/**
 * 
 */
package com.sogou.map.kubbo.boot.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sogou.map.kubbo.boot.configuration.element.ApplicationConfiguration;
import com.sogou.map.kubbo.boot.configuration.element.ReferenceConfiguration;
import com.sogou.map.kubbo.boot.configuration.element.ServerConfiguration;

/**
 * -Dkubbo.configuration=xxx
 * classpath根目录
 * setConfiguration()
 * 配置文件中的内容可以被环境变量和系统变量覆盖
 * @author liufuliang
 *
 */
public class KubboConfiguration implements Configuration{

    private static final long serialVersionUID = 1L;
    
    volatile boolean configured = false;
    
    List<ReferenceConfiguration> references = new ArrayList<ReferenceConfiguration>(10);
    ServerConfiguration server = new ServerConfiguration();
    ApplicationConfiguration application = new ApplicationConfiguration();
    
    public void addReferenceElement(ReferenceConfiguration reference){
        references.add(reference);
    }
    public void addReferenceElements(Collection<ReferenceConfiguration> reference){
        references.addAll(reference);
    }
    
    public void setServerElement(ServerConfiguration server){
        this.server = server;
    }
    public String getServerBind(){
        return server == null ? null : server.getBind();
    }
    public ApplicationConfiguration getApplication() {
        return application;
    }
    public void setApplication(ApplicationConfiguration application) {
        this.application = application;
    }
    public boolean isConfigured(){
        return this.configured;
    }
    public String getReferenceAddress(String interfaceType){
        return getReferenceAddress(interfaceType, null);
    }
    
    public String getReferenceAddress(String interfaceType, String name){
        for(ReferenceConfiguration reference : references){
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
