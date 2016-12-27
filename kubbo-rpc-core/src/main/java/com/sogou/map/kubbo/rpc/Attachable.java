/**
 * 
 */package com.sogou.map.kubbo.rpc;

import java.util.Map;

/**
 * @author liufuliang
 *
 */
public interface Attachable {
    /**
     * get attachments.
     *
     * @return attachments.
     */
    Map<String, String> getAttachments();

    /**
     * get attachment by key.
     *
     * @return attachment value.
     */
    String getAttachment(String key);

    /**
     * get attachment by key with default value.
     *
     * @return attachment value.
     */
    String getAttachment(String key, String defaultValue);
}
