package com.sogou.map.kubbo.common.annotation;

/**
 * Interface to filter out filenames.
 * 
 * @author liufuliang
 */
public interface Filter {

    /**
     * If true, the file is accepted, else rejected.
     */
    boolean accepts(String filename);
}
