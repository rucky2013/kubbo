/**
 * 
 */
package com.sogou.map.kubbo.common.annotation.impl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.sogou.map.kubbo.common.annotation.ClassAnnotationDiscoveryListener;
import com.sogou.map.kubbo.common.annotation.ResourceIterator;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;


/**
 * @author liufuliang
 *
 */
public class JavassistAnnotationDiscovery extends AbstractAnnotationDiscovery{
    public static final String NAME = "javassist";

    @Override
    protected void doDiscover(ResourceIterator itr) throws IOException{
        InputStream is = null;
        while ((is = itr.next()) != null) {
            // make a data input stream
            DataInputStream dstream = new DataInputStream(new BufferedInputStream(is));
            try {
                // get java-assist class file
                ClassFile classFile = new ClassFile(dstream);

                // discover class-level annotations
                discoverAndIntimateForClassAnnotations(classFile, true, true);
            } finally {
                dstream.close();
                is.close();
            }
        }
    }
    
    /**
     * Discovers Class Annotations
     *
     * @param classFile
     */
    private void discoverAndIntimateForClassAnnotations (ClassFile classFile, boolean visible, boolean invisible) {
        Set<Annotation> annotations = new HashSet<Annotation>();
    
        if (visible) {
            AnnotationsAttribute visibleA = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
            if (visibleA != null) annotations.addAll(Arrays.asList(visibleA.getAnnotations()));
        }
    
        if (invisible) {
            AnnotationsAttribute invisibleA = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.invisibleTag);
            if (invisibleA != null) annotations.addAll(Arrays.asList(invisibleA.getAnnotations()));
        }
    
        // now tell listeners
        for (Annotation annotation : annotations) {
            // String versions of listeners
            Set<ClassAnnotationDiscoveryListener> listeners = classAnnotationListeners.get(annotation.getTypeName());
            if (null != listeners) {
                for (ClassAnnotationDiscoveryListener listener : listeners) {
                    listener.discovered(classFile.getName(), annotation.getTypeName());
                }
            }
        }
    }
}
