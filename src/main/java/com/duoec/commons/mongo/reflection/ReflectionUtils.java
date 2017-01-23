package com.duoec.commons.mongo.reflection;

import com.alibaba.fastjson.JSONObject;
import com.duoec.commons.mongo.annotation.AutoIncrement;
import com.duoec.commons.mongo.annotation.Ignore;
import com.duoec.commons.mongo.exceptions.ReflenctionException;
import com.duoec.commons.mongo.reflection.dto.AutoIncrementInfo;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import com.duoec.commons.mongo.reflection.dto.FieldMate;
import com.duoec.commons.mongo.reflection.dto.MethodMate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ycoe on 17/1/6.
 */
public class ReflectionUtils {
    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);
    private static final Map<Class, ClassMate> CLASS_MAP = new HashMap();
    private static final Map<Field, FieldMate> FIELD_FIELD_MATE_MAP = new HashMap();

    private ReflectionUtils() {
    }

    public static ClassMate analyse(Class clazz) {
        if (CLASS_MAP.containsKey(clazz)) {
            //已经存在
            return CLASS_MAP.get(clazz);
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("分析类出错Introspector.getBeanInfo(" + clazz.getName() + ".class) error！", e);
        }
        ClassMate classMate = new ClassMate();
        classMate.setClazz(clazz);
        classMate.addReferClass(clazz);
        CLASS_MAP.put(clazz, classMate);

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            String fieldName = pd.getName();
            if ("class".equals(fieldName)) {
                continue;
            }
            FieldMate fieldMate = getFieldMate(classMate, clazz, fieldName);
            if (fieldMate == null) {
                continue;
            }

            Method setterMethod = pd.getWriteMethod();
            if (setterMethod != null) {
                MethodMate setterMate = getMethodMate(fieldMate, setterMethod);
                fieldMate.setSetter(setterMate);
            }

            Method getterMethod = pd.getReadMethod();
            if (getterMethod != null) {
                MethodMate getterMate = getMethodMate(fieldMate, getterMethod);
                fieldMate.setGetter(getterMate);
            }

            classMate.addFieldMate(fieldName, fieldMate);
        }
        Class<Object> targetClass = clazz.getSuperclass();
        if (targetClass != null && targetClass != Object.class) {
            analyse(targetClass);
        }

        return CLASS_MAP.get(clazz);
    }

    private static MethodMate getMethodMate(FieldMate fieldMate, Method method) {
        MethodMate methodMate = new MethodMate();
        methodMate.setFieldMate(fieldMate);
        methodMate.setMethod(method);
        return methodMate;
    }

    private static FieldMate getFieldMate(ClassMate classMate, Class clazz, String name) {
        Field field = null;
        NoSuchFieldException e = null;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e1) {
            e = e1;
            //尝试从父级中获取
            Class superClass = clazz.getSuperclass();
            if (superClass != Object.class) {
                return getFieldMate(classMate, superClass, name);
            }
        }
        if (field == null) {
            logger.error("获取Field: {}.{}失败！NoSuchFieldException", clazz.getName(), name, e);
            return null;
        }

        return getFieldMate(classMate, field);
    }

    private static FieldMate getFieldMate(ClassMate classMate, Field field) {
        if (FIELD_FIELD_MATE_MAP.containsKey(field)) {
            return FIELD_FIELD_MATE_MAP.get(field);
        }

        ClassUtils.setFieldAccessible(field);

        FieldMate fieldMate = new FieldMate();
        fieldMate.setClassMate(classMate);
        if (field.isAnnotationPresent(Ignore.class)) {
            Ignore ignore = field.getAnnotation(Ignore.class);
            fieldMate.setIgnore(ignore);
        }
        fieldMate.setField(field);
        fieldMate.setName(field.getName());

        if (field.isAnnotationPresent(AutoIncrement.class)) {
            setAutoIncrementInfo(classMate, fieldMate, field.getAnnotation(AutoIncrement.class));
        }

        FIELD_FIELD_MATE_MAP.put(field, fieldMate);

        Class fieldType = field.getType();
        if (fieldType == List.class) {
            //List
            Type genericType = field.getGenericType();
            Type[] argumentTypes = ((ParameterizedType) genericType).getActualTypeArguments();
            fieldType = (Class) argumentTypes[0];
            fieldMate.setGenericType(fieldType);
            fieldMate.setList(true);
            fieldMate.setSimpleType(false);
        } else if (fieldType == Map.class) {
            //MAP
            Type genericType = field.getGenericType();
            Type[] argumentTypes = ((ParameterizedType) genericType).getActualTypeArguments();
            fieldType = (Class) argumentTypes[1];
            fieldMate.setGenericType(fieldType);
            fieldMate.setMap(true);
            fieldMate.setSimpleType(false);
        } else if (!ClassUtils.isSimpleType(fieldType)) {
            ClassMate fieldTypeMate = analyse(fieldType);
            fieldTypeMate.getReferClassList().forEach(referClass -> classMate.addReferClass(referClass));
            fieldMate.setSimpleType(false);
        }
        return fieldMate;
    }

    private static void setAutoIncrementInfo(ClassMate classMate, FieldMate fieldMate, AutoIncrement autoIncrement) {
        AutoIncrementInfo info = new AutoIncrementInfo();
        Class<?> idType = fieldMate.getField().getType();
        if (idType == int.class || idType == Integer.class) {
            info.setInteger(true);
        } else if (idType == Long.class || idType == long.class) {
            info.setInteger(false);
        }
        info.setAutoIncrement(autoIncrement);
        fieldMate.setAutoIncrementInfo(info);
        classMate.setAutoIncrementInfo(info);
    }

    public static ClassMate getClassMate(Class clazz) {
        if (CLASS_MAP.containsKey(clazz)) {
            return CLASS_MAP.get(clazz);
        } else {
            return analyse(clazz);
        }
    }

    public static void setField(FieldMate fieldMate, Object target, Object value) {
//        MethodMate setter = fieldMate.getSetter();
//        if (setter != null) {
//            try {
//                setter.getMethod().invoke(target, value);
//            } catch (Exception e) {
//                //logger.error("设置字段{}.{}出错! value={}, valueType={}", fieldMate.getClassMate().getClazz().getName(), fieldMate.getName(), JSONObject.toJSONString(value), value.getClass().getName(), e);
//                throw new ReflenctionException(e.getMessage(), e);
//            }
//        }
        try {
            fieldMate.getField().set(target, value);
        } catch (IllegalAccessException e) {
            logger.error("设置字段{}.{}出错! value={}", fieldMate.getClassMate().getClazz().getName(), fieldMate.getName(), JSONObject.toJSONString(value), e);
//            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
            throw new ReflenctionException(e.getMessage(), e);
        }
    }

    public static <T> Object getField(FieldMate fieldMate, T entity) {
        try {
            return fieldMate.getField().get(entity);
        } catch (IllegalAccessException ex) {
            logger.error("获取字段{}.{}出错! value={}", fieldMate.getClassMate().getClazz().getName(), fieldMate.getName(), ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }
}
