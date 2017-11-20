/**
 * 
 */
package com.sogou.map.kubbo.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.StringUtils;
import com.sogou.map.kubbo.common.util.SystemPropertyUtils;
import com.sogou.map.kubbo.configuration.element.ApplicationConfiguration;
import com.sogou.map.kubbo.configuration.element.MetricsConfiguration;
import com.sogou.map.kubbo.configuration.element.ReferenceConfiguration;
import com.sogou.map.kubbo.configuration.element.ServerConfiguration;
import com.sogou.map.kubbo.configuration.element.TraceConfiguration;

/**
 * PropertiesConfigurator
 * 
 * @author liufuliang
 *
 */
public class PropertiesConfigurator {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigurator.class);

    public static void configure() {
        // system property / system env
        String kubboConfigurationFile = SystemPropertyUtils.get(Constants.KUBBO_CONFIGURATION_KEY);
        if (!StringUtils.isBlank(kubboConfigurationFile)) {
            logger.info("Using configuration: " + kubboConfigurationFile);
            configure(kubboConfigurationFile);
            return;
        }

        // classpath root
        InputStream in = PropertiesConfigurator.class.getResourceAsStream("/" + Constants.DEFAULT_KUBBO_CONFIGURATION);
        if (in != null) {
            logger.info("Using configuration: kubbo.properties in CLASSPATH root.");
            configure(in);
        }
    }

    public static void configure(String propertiesFilePath) {
        InputStream in;
        try {
            in = new FileInputStream(propertiesFilePath);
            configure(in);
        } catch (FileNotFoundException e) {
            logger.error("Read configuration FileNotFound", e);
        }
    }

    synchronized public static void configure(InputStream propertiesIn) {
        KubboConfiguration configuration = KubboConfiguration.getInstance();
        if(configuration.isConfigured()){
            throw new IllegalStateException("Kubbo already configured, don't configure it again.");
        }
        PropertiesEnvWrapper wrapper = new PropertiesEnvWrapper();
        wrapper.setEnvPrefix(Constants.DEFAULT_ENV_PREFIX);
        try {
            wrapper.wrap(propertiesIn);
        } catch (IOException e) {
            logger.error("Read configuration error", e);
            return;
        }

        // reference
        List<ReferenceConfiguration> references = parseReferenceDefination(wrapper);
        if (references != null) {
            configuration.addReferenceElements(references);
        }

        // server
        ServerConfiguration server = parseServerDefination(wrapper);
        if (server != null) {
            configuration.setServerElement(server);
        }

        // application
        ApplicationConfiguration application = parseApplicationDefination(wrapper);
        if (application != null) {
            configuration.setApplication(application);
        }
        
        // metrics
        MetricsConfiguration metrics = parseMetricsDefination(wrapper);
        if(metrics != null){
            configuration.setMetrics(metrics);
        }
        
        // trace
        TraceConfiguration trace = parseTraceDefination(wrapper);
        if(trace != null){
            configuration.setTrace(trace);
        }
        
        //wrapper.storeToSystemProperty();
        
        configuration.setConfigured(true);
    }
    
    private static List<ReferenceConfiguration> parseReferenceDefination(PropertiesEnvWrapper wrapper) {
        String header = ReferenceConfiguration.TAG + ".";
        List<ReferenceConfiguration> references = new ArrayList<ReferenceConfiguration>();
        for (String key : wrapper.keys()) {
            // key
            if (!key.startsWith(header)) {
                continue;
            }
            // value
            String value = wrapper.getString(key, "");
            if (value.isEmpty()) {
                continue;
            }
            key = StringUtils.trimHead(key, header);
            if (key.endsWith(".interface")) { // two line style
                String name = StringUtils.trimTail(key, ".interface");
                String address = wrapper.getString(header + name + ".address", "");
                if (!address.isEmpty()) {
                    ReferenceConfiguration reference = new ReferenceConfiguration();
                    reference.setName(name);
                    reference.setInterfaceType(value);
                    reference.setAddress(address);
                    references.add(reference);
                }
            } else if (!key.endsWith(".address")) { // single line style
                ReferenceConfiguration reference = new ReferenceConfiguration();
                reference.setInterfaceType(key);
                reference.setAddress(value);
                references.add(reference);
            }
        }

        return references;
    }

    private static ServerConfiguration parseServerDefination(PropertiesEnvWrapper wrapper) {
        ServerConfiguration server = new ServerConfiguration();
        String bind = wrapper.getString(ServerConfiguration.TAG + ".bind", "");
        if (!bind.isEmpty()) {
            server.setBind(bind);
        }
        return server;
    }
    
    private static MetricsConfiguration parseMetricsDefination(PropertiesEnvWrapper wrapper) {
        MetricsConfiguration metrics = new MetricsConfiguration();
        String address = wrapper.getString(MetricsConfiguration.TAG + ".address", "");
        if (!address.isEmpty()) {
            metrics.setAddress(address);;
        }
        return metrics;
    }
    
    private static TraceConfiguration parseTraceDefination(PropertiesEnvWrapper wrapper) {
        TraceConfiguration trace = new TraceConfiguration();
        String address = wrapper.getString(TraceConfiguration.TAG + ".address", "");
        if (!address.isEmpty()) {
            trace.setAddress(address);;
        }
        return trace;
    }

    private static ApplicationConfiguration parseApplicationDefination(PropertiesEnvWrapper wrapper) {
        ApplicationConfiguration application = new ApplicationConfiguration();
        String name = wrapper.getString(ApplicationConfiguration.TAG + ".name", "");
        if (!name.isEmpty()) {
            application.setName(name);
        }
        String home = wrapper.getString(ApplicationConfiguration.TAG + ".home", "");
        if (!home.isEmpty()) {
            application.setHome(home);
        }
        return application;
    }

}
