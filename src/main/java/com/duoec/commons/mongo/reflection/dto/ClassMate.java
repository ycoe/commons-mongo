package com.duoec.commons.mongo.reflection.dto;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ycoe on 17/1/6.
 */
public class ClassMate {
    /**
     * 类
     */
    private Class clazz;

    /**
     * 这个类相关联的其它类
     */
    private List<Class> referClassList = new ArrayList<>();

    /**
     * 属性与属性Field
     */
    private Map<String, FieldMate> fieldMateMap = Maps.newHashMap();

    /**
     * 当前类自增配置
     */
    private AutoIncrementInfo autoIncrementInfo;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Map<String, FieldMate> getFieldMateMap() {
        return fieldMateMap;
    }

    public void setFieldMateMap(Map<String, FieldMate> fieldMateMap) {
        this.fieldMateMap = fieldMateMap;
    }

    public void addFieldMate(String fieldName, FieldMate fieldMate) {
        fieldMateMap.put(fieldName, fieldMate);
    }

    public void addReferClass(Class clazz) {
        if(!referClassList.contains(clazz)) {
            referClassList.add(clazz);
        }
    }

    public List<Class> getReferClassList() {
        return referClassList;
    }

    public FieldMate getFieldMate(String fieldName) {
        return fieldMateMap.get(fieldName);
    }

    public AutoIncrementInfo getAutoIncrementInfo() {
        return autoIncrementInfo;
    }

    public void setAutoIncrementInfo(AutoIncrementInfo autoIncrementInfo) {
        this.autoIncrementInfo = autoIncrementInfo;
    }
}
