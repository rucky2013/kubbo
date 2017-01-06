package com.sogou.map.kubbo.common.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSONObject Bean的标识。
 *
 * @author liufuliang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JSONBean {

    /**
     * 
     */
    String value() default "";

}