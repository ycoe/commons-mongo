package com.duoec.commons.mongo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段忽略
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
    /**
     * 读取时是否忽略
     *
     * @return boolean 默认:true
     */
    boolean read() default true;

    /**
     * 新增时是否忽略
     *
     * @return boolean 默认:true
     */
    boolean insert() default true;

    /**
     * 更新时是否忽略
     *
     * @return boolean 默认:true
     */
    boolean update() default true;
}
