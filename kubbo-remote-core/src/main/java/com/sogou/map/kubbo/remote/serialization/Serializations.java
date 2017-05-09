
package com.sogou.map.kubbo.remote.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    
    /**
     * 
     * @param type 序列化类型, 如“hessian”, "kryo"
     * @return 序列化对象
     */
    public static Serialization getSerialization(String type){
        return Extensions.getExtension(type, Serialization.class);
    }
    
    public static Serialization getSerializationById(Byte id) {
        return ID_SERIALIZATION_MAP.get(id);
    }
    
    /**
     * 
     * @param type 序列化器类型, 如“hessian”, "kryo"
     * @param obj 待序列化对象
     * @return 序列化后的字节数组
     * @throws IOException io异常
     */
    public static byte[] serialize(String type, Object obj) throws IOException{
        Serialization serialization = getSerialization(type);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        
        ObjectOutput ouput = serialization.serialize(ostream);
        ouput.writeObject(obj);
        ouput.flushBuffer();
        ostream.flush();
        
        if (ouput instanceof Releasable) {
            ((Releasable) ouput).release();
        }
        return ostream.toByteArray();
    }
    
    /**
     * 
     * @param type 序列化器类型, 如“hessian”, "kryo"
     * @param bytes 待反序列化的字节数组
     * @param dataType 反序列化后的数据类型
     * @return 反序列化后的数据对象
     * @throws IOException io异常
     * @throws ClassNotFoundException 数据类型错误
     */
    public static <T> T deserialize(String type, byte[] bytes, Class<T> dataType) throws IOException, ClassNotFoundException{
        Serialization serialization = getSerialization(type);
        InputStream istream = new ByteArrayInputStream(bytes);
        ObjectInput input = serialization.deserialize(istream);
        T obj = input.readObject(dataType);
        
        if (input instanceof Releasable) {
            ((Releasable) input).release();
        }
        return obj;
    }
        
    private Serializations() {}
}
