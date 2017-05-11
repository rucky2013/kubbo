package com.sogou.map.kubbo.common.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Defaults
 * @author liufuliang
 */
public final class Defaults {
    private static final Map<Class<?>, Object> DEFAULTS;

    static {
        // Only add to this map via put(Map, Class<T>, T)
        Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
        put(map, boolean.class, false);
        put(map, char.class, '\0');
        put(map, byte.class, (byte) 0);
        put(map, short.class, (short) 0);
        put(map, int.class, 0);
        put(map, long.class, 0L);
        put(map, float.class, 0f);
        put(map, double.class, 0d);
        DEFAULTS = Collections.unmodifiableMap(map);
    }

    private static <T> void put(Map<Class<?>, Object> map, Class<T> type, T value) {
        map.put(type, value);
    }

    /**
     * Returns the default value of {@code type}
     * @param type class type
     * @return default value
     */
    public static <T> T defaultValue(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type == NULL");
        }
        // Primitives.wrap(type).cast(...) would avoid the warning, but we can't
        // use that from here

        @SuppressWarnings("unchecked") // the put method enforces this key-value
                                       // relationship
        T t = (T) DEFAULTS.get(type);
        return t;
    }
    
    private Defaults() {}

}
