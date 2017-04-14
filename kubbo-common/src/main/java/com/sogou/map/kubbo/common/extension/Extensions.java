/**
 * 
 */
package com.sogou.map.kubbo.common.extension;

import java.util.List;
import java.util.Set;

import com.sogou.map.kubbo.common.URL;

/**
 * @author liufuliang
 *
 */
public class Extensions {
    public static <T> T getExtension(URL url, String key, Class<T> interfaceType) {
        String type = getExtensionType(url, key, interfaceType);
        return getExtension(type, interfaceType);
    }

    public static <T> T getExtension(String type, Class<T> interfaceType) {
        return ExtensionLoader.getExtensionLoader(interfaceType).getExtension(type);
    }

    public static <T> String getExtensionType(URL url, String key, Class<T> interfaceType) {
        String defaultExtensionName = ExtensionLoader.getExtensionLoader(interfaceType).getDefaultExtensionName();
        String type = url.getParameter(key, defaultExtensionName);
        return type;
    }

    public static <T> T getAdaptiveExtension(Class<T> interfaceType) {
        return ExtensionLoader.getExtensionLoader(interfaceType).getAdaptiveExtension();
    }

    public static <T> List<T> getActivateExtension(URL url, String key, Class<T> interfaceType) {
        return ExtensionLoader.getExtensionLoader(interfaceType).getActivateExtension(url, key);
    }
    
    public static <T> List<T> getActivateExtension(URL url, String key, String group, Class<T> interfaceType) {
        return ExtensionLoader.getExtensionLoader(interfaceType).getActivateExtension(url, key, group);
    }

    public static <T> T getDefaultExtension(Class<T> interfaceType) {
        return ExtensionLoader.getExtensionLoader(interfaceType).getDefaultExtension();
    }

    public static <T> Set<String> getSupportedExtensions(Class<T> interfaceType) {
        return ExtensionLoader.getExtensionLoader(interfaceType).getSupportedExtensions();
    }

    private Extensions() {
    }
}
