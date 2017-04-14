
package com.sogou.map.kubbo.remote.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.extension.Extensions;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;

/**
 * @author liufuliang
 */
public class Serializations {
    private static final Logger logger = LoggerFactory.getLogger(Serializations.class);

    private static Map<Byte, Serialization> ID_SERIALIZATION_MAP = new HashMap<Byte, Serialization>();

    static {
        Set<String> supportedExtensions = Extensions.getSupportedExtensions(Serialization.class);
        for (String name : supportedExtensions) {
            Serialization serialization = Extensions.getExtension(name, Serialization.class);
            byte idByte = serialization.getContentTypeId();
            if (ID_SERIALIZATION_MAP.containsKey(idByte)) {
                logger.error("Serialization extension " + serialization.getClass().getName()
                                 + " has duplicate id to Serialization extension "
                                 + ID_SERIALIZATION_MAP.get(idByte).getClass().getName()
                                 + ", ignore this Serialization extension");
                continue;
            }
            ID_SERIALIZATION_MAP.put(idByte, serialization);
        }
    }

    public static Serialization getSerialization(URL url) {
        return Extensions.getExtension(url, Constants.SERIALIZATION_KEY, Serialization.class);
    }

    public static Serialization getSerialization(URL url, Byte id) {
        Serialization result = getSerializationById(id);
        if (result == null) {
            result = getSerialization(url);
        }
        return result;
    }
    
    public static Serialization getSerializationById(Byte id) {
        return ID_SERIALIZATION_MAP.get(id);
    }
    
    private Serializations() {}
}
