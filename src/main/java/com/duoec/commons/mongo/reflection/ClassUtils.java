package com.duoec.commons.mongo.reflection;

import com.duoec.commons.mongo.exceptions.ReflenctionException;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码复制自：org.springframework.util.ClassUtils
 * Created by ycoe on 17/1/6.
 */
public class ClassUtils {
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap();

    private static final Map<Class<?>, Boolean> otherSimpleType = Maps.newHashMap();

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        primitiveWrapperTypeMap.put(boolean.class, Boolean.class);
        primitiveWrapperTypeMap.put(byte.class, Byte.class);
        primitiveWrapperTypeMap.put(char.class, Character.class);
        primitiveWrapperTypeMap.put(double.class, Double.class);
        primitiveWrapperTypeMap.put(float.class, Float.class);
        primitiveWrapperTypeMap.put(int.class, Integer.class);
        primitiveWrapperTypeMap.put(long.class, Long.class);
        primitiveWrapperTypeMap.put(short.class, Short.class);

        otherSimpleType.put(String.class, true);
    }

    public static boolean isSimpleType(Class<?> clazz) {
        if (isPrimitiveOrWrapper(clazz)) {
            return true;
        }
        if (otherSimpleType.containsKey(clazz)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(isSimpleType(String.class) ? "true" : "false");
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    /**
     * 将此字段设置为可写
     *
     * @param field
     */
    public static void setFieldAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 判断两个类型是否一致，这里把原始类型和封装类型当成是一致的！
     *
     * @param aClass
     * @param bClass
     * @return
     */
    public static boolean equal(Class<?> aClass, Class<?> bClass) {
        if (aClass == bClass || aClass.isAssignableFrom(bClass)) {
            return true;
        }

        if (primitiveWrapperTypeMap.containsKey(aClass) && primitiveWrapperTypeMap.containsKey(bClass)) {
            if (aClass.isPrimitive()) {
                // aClass是基础数据类型
                if (bClass.isPrimitive()) {
                    return false;
                } else {
                    return aClass == primitiveWrapperTypeMap.get(bClass);
                }
            } else {
                // aClass不是基础数据类型
                if (bClass.isPrimitive()) {
                    return bClass == primitiveWrapperTypeMap.get(aClass);
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static <T> T instantiate(Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new ReflenctionException("Specified class is an interface: " + clazz.getName());
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new ReflenctionException("Is it an abstract class?: " + clazz.getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new ReflenctionException("Is the constructor accessible: " + clazz.getName(), ex);
        }
    }
}
