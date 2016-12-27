package com.sogou.map.kubbo.common.annotation.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sogou.map.kubbo.common.annotation.Filter;
import com.sogou.map.kubbo.common.annotation.ResourceIterator;

/**
 * The Class ClassFileIterator.
 * 
 * @author animesh.kumar
 */
public class ClassFileIterator implements ResourceIterator {

    /** files. */
    private List<File> files;

    /** The index. */
    private int index = 0;

    /**
     * Instantiates a new class file iterator.
     * 
     * @param file
     * @param filter
     */
    public ClassFileIterator(File file, Filter filter) {
        files = new ArrayList<File>();
        try {
        	init(files, file, filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // helper method to initialize the iterator
    private static void init(List<File> list, File dir, Filter filter) throws Exception {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
            	init(list, files[i], filter);
            } else {
                if (filter == null || filter.accepts(files[i].getAbsolutePath())) {
                    list.add(files[i]);
                }
            }
        }
    }

    @Override
    public final InputStream next() {
        if (index >= files.size()){
            return null;
        }
        File fp = (File) files.get(index++);
        try {
            return new FileInputStream(fp);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
    	// DO Nothing
    }
}
