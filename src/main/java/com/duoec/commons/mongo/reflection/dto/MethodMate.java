package com.duoec.commons.mongo.reflection.dto;

import java.lang.reflect.Method;

/**
 * Created by ycoe on 17/1/6.
 */
public class MethodMate {
    /**
     * 方法
     */
    private Method method;

    /**
     * 方法对应的FieldMate
     */
    private FieldMate fieldMate;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public FieldMate getFieldMate() {
        return fieldMate;
    }

    public void setFieldMate(FieldMate fieldMate) {
        this.fieldMate = fieldMate;
    }
}
