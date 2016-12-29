/**
 * 
 */package com.sogou.map.kubbo.rpc.protocol;

import java.util.HashMap;
import java.util.Map;

import com.sogou.map.kubbo.rpc.Attachable;

/**
 * @author liufuliang
 *
 */
public class AbstractAttachable<T extends AbstractAttachable<T>> implements Attachable {

    protected Map<String, String>  attachments;
    
    public AbstractAttachable() {
    }
    public AbstractAttachable(Map<String, String> attachments) {
        this.attachments = attachments == null ? new HashMap<String, String>() : attachments;
    }
    
    @SuppressWarnings("unchecked")
    private T asDerivedType() {
        return (T) this;
    }
    
    @Override
    public Map<String, String> getAttachments() {
        return attachments;
    }

    @Override
    public String getAttachment(String key) {
        if (attachments == null) {
            return null;
        }
        return attachments.get(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        if (attachments == null) {
            return defaultValue;
        }
        String value = attachments.get(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }
    
    public T setAttachments(Map<String, String> attachments) {
        this.attachments = attachments == null ? new HashMap<String, String>() : attachments;
        return asDerivedType();
    }
    
    public T setAttachment(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<String, String>();
        }
        if (value == null) {
            attachments.remove(key);
        } else {
            attachments.put(key, value);
        }
        return asDerivedType();
    }

    public T setAttachmentIfAbsent(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<String, String>();
        }
        if (!attachments.containsKey(key)) {
            attachments.put(key, value);
        }
        return asDerivedType();
    }

    public T addAttachments(Map<String, String> attachments) {
        if (attachments == null) {
            return asDerivedType();
        }
        if (this.attachments == null) {
            this.attachments = new HashMap<String, String>();
        }
        this.attachments.putAll(attachments);
        return asDerivedType();
    }

    public T addAttachmentsIfAbsent(Map<String, String> attachments) {
        if(attachments != null){
            for (Map.Entry<String, String> entry : attachments.entrySet()) {
                setAttachmentIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        return asDerivedType();
    }
    
    public T removeAttachment(String key) {
        if(attachments != null){
            attachments.remove(key);
        }
        return asDerivedType();
    }
    
    public void clearAttachments() {
        if(attachments != null){
            this.attachments.clear();
        }
    }
}
