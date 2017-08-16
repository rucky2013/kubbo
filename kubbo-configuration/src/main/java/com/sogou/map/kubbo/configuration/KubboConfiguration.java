/**
 * 
 */
package com.sogou.map.kubbo.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sogou.map.kubbo.configuration.element.ApplicationConfiguration;
import com.sogou.map.kubbo.configuration.element.MetricsConfiguration;
import com.sogou.map.kubbo.configuration.element.ReferenceConfiguration;
import com.sogou.map.kubbo.configuration.element.ServerConfiguration;
import com.sogou.map.kubbo.configuration.element.TraceConfiguration;

/**
 * -Dkubbo.configuration=xxx
 * classpath根目录
 * setConfiguration()
 * 配置文件中的内容可以被环境变量和系统变量覆盖
 * @author liufuliang
 *
 */
public class KubboConfiguration implements Configuration{

    private static final long serialVersionUID = 1336741716966069602L;

    private volatile boolean configured = false;
    
    // reference
    List<ReferenceConfiguration> references = new ArrayList<ReferenceConfiguration>(10);

    // server
    ServerConfiguration server = new ServerConfiguration();

    // application
    ApplicationConfiguration application = new ApplicationConfiguration();
    
    // metrics
    MetricsConfiguration metrics = new MetricsConfiguration();
    
    // trace
    TraceConfiguration trace = new TraceConfiguration();
    
    public void addReferenceElement(ReferenceConfiguration reference){
        references.add(reference);
    }
    public void addReferenceElements(Collection<ReferenceConfiguration> reference){
        references.addAll(reference);
    }
    
    public void setServerElement(ServerConfiguration server){
        this.server = server;
    }
    public ServerConfiguration getServer(){
        return server;
    }
    public ApplicationConfiguration getApplication() {
        return application;
    }
    public void setApplication(ApplicationConfiguration application) {
        this.application = application;
    }    
    public MetricsConfiguration getMetrics() {
        return metrics;
    }
    public void setMetrics(MetricsConfiguration metrics) {
        this.metrics = metrics;
    }
    public TraceConfiguration getTrace() {
        return trace;
    }
    public void setTrace(TraceConfiguration trace) {
        this.trace = trace;
    }
    public boolean isConfigured(){
        return this.configured;
    }
    
    public void setConfigured(boolean configured) {
        this.configured = configured;
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
