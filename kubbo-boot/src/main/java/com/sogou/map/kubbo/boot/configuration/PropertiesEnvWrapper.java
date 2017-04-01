package com.sogou.map.kubbo.boot.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import com.sogou.map.kubbo.common.util.SystemPropertyUtils;


/**
 * @author liufuliang
 * 优先读取系统环境变量的配置, 环境变量以"_"代替所有非字母和数字
 * 次优读取系统变量的配置
 */
public class PropertiesEnvWrapper {
    Properties properties;
    String envPrefix = "";
    
    public String getEnvPrefix() {
        return envPrefix;
    }
    public void setEnvPrefix(String envPrefix) {
        this.envPrefix = envPrefix;
    }
    public void wrap(String propertiesFile) throws IOException{
        InputStream in = new FileInputStream(propertiesFile);
        properties = new Properties();
        properties.load(in);
    }
    public void wrap(InputStream propertiesIn) throws IOException{
        properties = new Properties();
        properties.load(propertiesIn);
    }
    
    /**
     * set system properties
     */
    public void storeToSystemProperty(){
        Enumeration<Object> keys = properties.keys();
        while(keys.hasMoreElements()){
            String key = (String) keys.nextElement();
            String value = properties.getProperty(key);
            if(!getEnvPrefix().isEmpty()){
                key = getEnvPrefix().toLowerCase() + "." + key;
            }
            if(SystemPropertyUtils.contains(key)){
                continue;
            }
            SystemPropertyUtils.set(key, value);
        }
    }
    
    private String getEnvConfiguration(String key){
        String envKey = key.toUpperCase().replaceAll("[^0-9A-Z]", "_");
        if(!getEnvPrefix().isEmpty()){
            envKey = getEnvPrefix().toUpperCase() + "_" + envKey;
        }
        String value = System.getenv(envKey);
        return value;
    }
    
    private String getSystemPropertyConfiguration(String key){
        String systemPropertyKey = key;
        if(!getEnvPrefix().isEmpty()){
            systemPropertyKey = getEnvPrefix().toLowerCase() + "." + systemPropertyKey;
        }
        String value = SystemPropertyUtils.get(systemPropertyKey);
        return value;
    }
    
    private String get(String key){
        String value = getEnvConfiguration(key);
        if(value == null){
            value = getSystemPropertyConfiguration(key);
        }
        if(value == null){
            value = properties.getProperty(key);
        }
        return value;
    }
    public boolean contains(String key) {
        return get(key) != null;
    }
    
    public Set<String> keys(){
        return properties.stringPropertyNames();
    }
    
    public String getString(String key, String defaultValue){
        String value = get(key);
        return value == null ? defaultValue : value;
    }
    public int getInt(String key, int defaultValue){
        String value = get(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }
    public long getLong(String key, long defaultValue){
        String value = get(key);
        return value == null ? defaultValue : Long.parseLong(value);
    }
    public double geDouble(String key, double defaultValue){
        String value = get(key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }
    public boolean getBoolean(String key, boolean defaultValue){
        String value = get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }
}
