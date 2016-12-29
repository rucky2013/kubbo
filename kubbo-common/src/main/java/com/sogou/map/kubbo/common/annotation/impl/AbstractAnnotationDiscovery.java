/**
 * 
 */
package com.sogou.map.kubbo.common.annotation.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.sogou.map.kubbo.common.annotation.AnnotationDiscovery;
import com.sogou.map.kubbo.common.annotation.AnnotationDiscoveryListener;
import com.sogou.map.kubbo.common.annotation.ClassAnnotationDiscoveryListener;
import com.sogou.map.kubbo.common.annotation.Filter;
import com.sogou.map.kubbo.common.annotation.ResourceIterator;


/**
 * @author liufuliang
 *
 */
public abstract class AbstractAnnotationDiscovery implements AnnotationDiscovery{
    
    protected final Map<String, Set<ClassAnnotationDiscoveryListener>> classAnnotationListeners =
        new HashMap<String, Set<ClassAnnotationDiscoveryListener>>();
    
    private <L extends AnnotationDiscoveryListener> void addAnnotationListener (Map<String, Set<L>> map, L listener) {
        String[] annotations = listener.annotations();
        if (null == annotations || annotations.length == 0) {
            return;
        }

        for (String annotation : annotations) {
            Set<L> listeners = map.get(annotation);
            if (null == listeners) {
                listeners = new HashSet<L>();
                map.put(annotation, listeners);
            }
            listeners.add(listener);
        }
    }
    
    @Override
    public void addListener(AnnotationDiscoveryListener listener) {
        if( listener instanceof ClassAnnotationDiscoveryListener){
            addAnnotationListener(classAnnotationListeners, (ClassAnnotationDiscoveryListener)listener);
        }	
    }
    
    @Override
    public void discover() throws IOException {
        URL[] resources = findResources();		
        for (URL resource : resources) {
            ResourceIterator iterator = getResourceIterator(resource, new PackageFilter());
            if (iterator != null) {
                doDiscover(iterator);
            }
        }
    }
    
    protected abstract void doDiscover(ResourceIterator itr) throws IOException;
    

    private ResourceIterator getResourceIterator(URL url, Filter filter) throws IOException {
        String urlString = url.toString();
        if (urlString.endsWith("!/")) {
            urlString = urlString.substring(4);
            urlString = urlString.substring(0, urlString.length() - 2);
            url = new URL(urlString);
        }
    
        if (!urlString.endsWith("/")) {
            return new JarFileIterator(url.openStream(), filter);
        } else {
    
            if (!url.getProtocol().equals("file")) {
                throw new IOException("Unable to understand protocol: " + url.getProtocol());
            }
    
            String filePath = URLDecoder.decode(url.getPath(), "UTF-8");
            File f = new File(filePath);
            if (!f.exists()) return null;
    
            if (f.isDirectory()) {
                return new ClassFileIterator(f, filter);
            } else {
                return new JarFileIterator(url.openStream(), filter);
            }
        }
    }
    
    public final URL[] findResources() {
        URL[] ret = findResourcesForCurrentClasspath();
        if (ret.length == 0) ret = findResourcesForSystemClasspath();
        return ret;
    }
    
    private URL[] findResourcesForCurrentClasspath() {
        List<URL> urls = new ArrayList<URL>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                URL[] urlArray = ((URLClassLoader) loader).getURLs();
                urls.addAll(Arrays.asList(urlArray));
            }
            loader = loader.getParent();
        }
        return urls.toArray(new URL[urls.size()]);
    }

    private URL[] findResourcesForSystemClasspath() {
        List<URL> urls = new ArrayList<URL>();
        String classpath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(classpath,
                File.pathSeparator);

        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();

            File fp = new File(path);
            if (!fp.exists())
                throw new RuntimeException(
                        "File in java.class.path does not exist: " + fp);
            try {
                urls.add(fp.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }

}
