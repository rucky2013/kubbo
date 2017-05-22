/**
 * 
 */
package com.sogou.map.kubbo.boot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.sogou.map.kubbo.boot.annotation.Hook;
import com.sogou.map.kubbo.boot.configuration.KubboConfiguration;
import com.sogou.map.kubbo.boot.configuration.PropertiesConfigurator;
import com.sogou.map.kubbo.boot.annotation.Export;
import com.sogou.map.kubbo.boot.context.AbstractApplicationContext;
import com.sogou.map.kubbo.boot.context.ApplicationContext;
import com.sogou.map.kubbo.boot.hook.LifecycleHook;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.annotation.AnnotationDiscovery;
import com.sogou.map.kubbo.common.annotation.ClassAnnotationDiscoveryListener;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;



/**
 * Bootstrap
 * @author liufuliang
 *
 */
public class Bootstrap {
    
    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    
    private ApplicationContext applicationContext = new AbstractApplicationContext();
    private URL bind;
    private List<String> services;
    private List<String> hooks;    
    private volatile boolean destroyed = false;
    
    private void configure(){
        PropertiesConfigurator.configure();
        this.bind = URL.valueOf(KubboConfiguration.getInstance().getServerBind());
    }
    
    private void scanResources() throws IOException{
        logger.info("Scanning for Export And Hook classes in the CLASSPATH");
        services = new ArrayList<String>(3);
        hooks = new ArrayList<String>(3);
        AnnotationDiscovery discovery = getAnnotationDiscovery();
        discovery.addListener(new ClassAnnotationDiscoveryListener() {
            @Override
            public String[] annotations() {
                return new String[] { Export.class.getName(), Hook.class.getName() };
            }
            @Override
            public void discovered(String clazz, String annotation) {
                if(annotation.endsWith(Export.class.getName())){
                    services.add(clazz);
                } else if(annotation.endsWith(Hook.class.getName())){
                    hooks.add(clazz);
                }
            }
        });
        discovery.discover();	
        logger.info("Export classes " + services);
        logger.info("Hook classes " + hooks);
    }
    
    private void initialize() throws Throwable{
        if(hooks == null){
            return;
        }
        for(String hook : hooks){
            Class<?> clazz = Class.forName(hook);
            Object hookInstance = clazz.newInstance();
            if(hookInstance instanceof LifecycleHook){
                ((LifecycleHook)hookInstance).initialize(applicationContext);
            }
        }
    }
    
    synchronized private void destroy() throws Throwable{
        if(destroyed){
            return;
        }
        Kubbo.destroy();
        if(hooks == null){
            return;
        }
        for(String hook : hooks){
            Class<?> clazz = Class.forName(hook);
            Object hookInstance = clazz.newInstance();
            if(hookInstance instanceof LifecycleHook){
                ((LifecycleHook)hookInstance).destroy(applicationContext);
            }
        }
        destroyed = true;
    }
    
    private void shutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try { Bootstrap.this.destroy(); } catch (Throwable e) {}
            }
        });
    }
    
    private void loop(){
        while(true){
            try { Thread.sleep(600*1000);; } catch (Throwable e) { }
        }
    }
    
    private void export() throws Throwable{
        if(services == null){
            logger.warn("No Export service found.");
            return;
        }
        for(String service : services){
            Class<?> clazz = Class.forName(service);
            Object serviceInstance = clazz.newInstance();
            Class<?> serviceType = clazz.getAnnotation(Export.class).value();
            if(serviceType.isInstance(serviceInstance)){
                Kubbo.exportGeneric(serviceInstance, serviceType, bind);
                logger.info("Export service: " + service);
            }
        }
        
    }
    
    private AnnotationDiscovery getAnnotationDiscovery(){
        AnnotationDiscovery discovery = Extensions.getDefaultExtension(AnnotationDiscovery.class);
        return discovery;
    }
    
    private void start(boolean loop) throws Throwable {
        //configure
        configure();
        
        //Annotation Scanning
        scanResources();
        
        //hook: initialize
        initialize();
        
        //export service
        export();
        
        //shutdownHook
        shutdownHook();
        
        //loop
        if(loop){ 
            loop();
        }
    }
    
    public void start() throws Throwable{
        start(true);
    }

//    public void stop() throws Throwable{
//        destroy();
//    }
    
    public static void main(String[] args) throws Throwable{
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.start();
    }

}
