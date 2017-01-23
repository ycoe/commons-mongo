package com.duoec.commons.mongo.reflection.dto;

import com.duoec.commons.mongo.annotation.Ignore;

import java.lang.reflect.Field;

/**
 * Created by ycoe on 17/1/6.
 */
public class FieldMate {
    /**
     * Field
     */
    private Field field;

    /**
     * 属性名称
     */
    private String name;

    /**
     * Ignore注解
     */
    private Ignore ignore;

    /**
     * AutoIncrement注解
     */
    private AutoIncrementInfo autoIncrementInfo;

    /**
     * 字段对应的Getter
     */
    private MethodMate getter;

    /**
     * 字段对应的Setter
     */
    private MethodMate setter;

    /**
     * 所属classMate
     */
    private ClassMate classMate;

    /**
     * 如果是List / Map时，分别对应第一个，第二个泛型类型
     */
    private Class genericType;

    /**
     * 是否是列表
     */
    private boolean isList = false;

    /**
     * 是否是Map
     */
    private boolean isMap = false;

    /**
     * 是否是基本数据类型：原始类型、原始类型的封装类型、String
     */
    private boolean isSimpleType = true;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ignore getIgnore() {
        return ignore;
    }

    public void setIgnore(Ignore ignore) {
        this.ignore = ignore;
    }

    public AutoIncrementInfo getAutoIncrementInfo() {
        return autoIncrementInfo;
    }

    public void setAutoIncrementInfo(AutoIncrementInfo autoIncrementInfo) {
        this.autoIncrementInfo = autoIncrementInfo;
    }

    public MethodMate getGetter() {
        return getter;
    }

    public void setGetter(MethodMate getter) {
        this.getter = getter;
    }

    public MethodMate getSetter() {
        return setter;
    }

    public void setSetter(MethodMate setter) {
        this.setter = setter;
    }

    public ClassMate getClassMate() {
        return classMate;
    }

    public void setClassMate(ClassMate classMate) {
        this.classMate = classMate;
    }

    public Class getGenericType() {
        return genericType;
    }

    public void setGenericType(Class genericType) {
        this.genericType = genericType;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public boolean isMap() {
        return isMap;
    }

    public void setMap(boolean map) {
        isMap = map;
    }

    public boolean isSimpleType() {
        return isSimpleType;
    }

    public void setSimpleType(boolean simpleType) {
        isSimpleType = simpleType;
    }
}
