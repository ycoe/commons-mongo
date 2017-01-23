package com.duoec.commons.mongo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ycoe on 16/5/4.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIncrement {
    /**
     * 存储名称
     * @return
     */
    String value() default "";

    /**
     * 开始值
     * @return
     */
    long start() default 1;

    /**
     * 进阶值
     * @return
     */
    int step() default 1;
}
