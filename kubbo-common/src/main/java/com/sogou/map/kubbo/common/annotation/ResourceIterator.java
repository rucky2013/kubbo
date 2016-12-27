package com.sogou.map.kubbo.common.annotation;

import java.io.InputStream;

/**
 * Interface for Resource Iterator, usually files.
 * 
 * @author liufuliang
 */
public interface ResourceIterator {

    /**
     * Please close after use.
     * 
     * @return null if no more streams left to iterate on
     */
    InputStream next();

    /**
     * Close.
     */
    void close();
}
