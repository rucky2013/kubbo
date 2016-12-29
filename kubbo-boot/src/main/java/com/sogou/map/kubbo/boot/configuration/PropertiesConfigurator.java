/**
 * 
 */
package com.sogou.map.kubbo.boot.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.sogou.map.kubbo.boot.Bootstrap;
import com.sogou.map.kubbo.boot.configuration.element.ReferenceElement;
import com.sogou.map.kubbo.boot.configuration.element.ServerElement;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.StringUtils;
import com.sogou.map.kubbo.common.utils.SystemPropertyUtils;


/**
 * Non-ThreadSafe
 * @author liufuliang
 *
 */
public class PropertiesConfigurator{
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigurator.class);

    public static void configure(){
        //system property
        String kubboConfigurationFile = SystemPropertyUtils.get(Constants.KUBBO_CONFIGURATION_KEY);
        if(! StringUtils.isBlank(kubboConfigurationFile)){
            configure(kubboConfigurationFile);
            logger.info("Use configuration: " + kubboConfigurationFile);
            return;
        } 

        //classpath root
        InputStream in = Bootstrap.class.getResourceAsStream("/" + Constants.DEFAULT_KUBBO_CONFIGURATION);
        if(in != null){
            configure(in);
            logger.info("Use configuration: kubbo.properties in Classpath root.");
        }
    }
        
    private static ReferenceElement parseReferenceElement(String key, PropertiesEnvWrapper wrapper){
        //key
        if(! key.startsWith("reference.")){
            return null;
        }
        //value
        String value = wrapper.getString(key, "");
        if(value.isEmpty()){ 
            return null;
        }
        key = StringUtils.trimHead(key, "reference.");
        if(key.endsWith(".interface")){   //two line style
            String name = StringUtils.trimTail(key, ".interface");
            String address = wrapper.getString("reference." + name + ".address", "");
            if(!address.isEmpty()){
                ReferenceElement reference = new ReferenceElement();
                reference.setName(name);
                reference.setInterfaceType(value);
                reference.setAddress(address);
                return reference;
            }
        } else if(! key.endsWith(".address")){ //single line style
            ReferenceElement reference = new ReferenceElement();
            reference.setInterfaceType(key);
            reference.setAddress(value);
            return reference;
        }
        return null;
    }
    
    private static ServerElement parseServerElement(String key, PropertiesEnvWrapper wrapper){
        //key
        if(! key.startsWith("server")){
            return null;
        }
        //value
        String value = wrapper.getString(key, "");
        if(value.isEmpty()){ 
            return null;
        }
        
        ServerElement server = new ServerElement();
        server.setBind(value);
        return server;
    }
    
    public static void configure(InputStream propertiesIn){
        KubboConfiguration configuration = KubboConfiguration.getInstance();
        PropertiesEnvWrapper wrapper = new PropertiesEnvWrapper();
        wrapper.setEnvPrefix("kubbo");
        try {
            wrapper.wrap(propertiesIn);
        } catch (IOException e) {
            logger.error("Read configuration error", e);
            return;
        }
        
        for(String key : wrapper.keys()){
            ReferenceElement reference = parseReferenceElement(key, wrapper);
            if(reference != null){
                configuration.addReferenceElement(reference);
            }
            ServerElement server = parseServerElement(key, wrapper);
            if(server != null){
                configuration.setServerElement(server);
            }
        }
        
        configuration.configured = true;
    }
    
    public static void configure(String propertiesFilePath){
        InputStream in;
        try {
            in = new FileInputStream(propertiesFilePath);
            configure(in);
            logger.info("Use configuration: " + propertiesFilePath);
        } catch (FileNotFoundException e) {
            logger.error("Read configuration FileNotFound", e);
        }
    }


}
