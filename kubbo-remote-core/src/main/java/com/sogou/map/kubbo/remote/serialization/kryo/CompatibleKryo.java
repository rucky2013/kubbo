package com.sogou.map.kubbo.remote.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;

/**
 * @author liufuliang
 */
public class CompatibleKryo extends Kryo {

    private static final Logger logger = LoggerFactory.getLogger(CompatibleKryo.class);

    @Override
    @SuppressWarnings("rawtypes")
    public Serializer getDefaultSerializer(Class type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }

        if (!type.isArray() && !type.isEnum() && !isZeroArgConstructorAvailable(type)) {
            if (logger.isWarnEnabled()) {
                logger.warn(type.getName() + " has no zero-arg constructor and this will affect the serialization performance");
            }
            return new JavaSerializer();
        }
        //return new CompatibleFieldSerializer(this, type) ;
        return super.getDefaultSerializer(type);
    }
    
    public static boolean isZeroArgConstructorAvailable(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
