package com.duoec.commons.mongo.reflection;

/**
 * Created by ycoe on 16/5/6.
 */
public class SimpleTypeConverter {
    private SimpleTypeConverter() {
    }

    public static Object convert(Object value, Class<?> clazz) {
        if (value == null) {
            return null;
        }
        Class<? extends Object> valueClass = value.getClass();
        if (int.class == valueClass || Integer.class == valueClass) {
            return convertFromNumber((Integer) value, clazz);
        } else if (boolean.class == valueClass || Boolean.class == valueClass) {
            throw new UnsupportedOperationException();
        } else if (long.class == valueClass || Long.class == valueClass) {
            return convertFromNumber((Long) value, clazz);
        } else if (double.class == valueClass || Double.class == valueClass) {
            return convertFromNumber((Double) value, clazz);
        } else if (short.class == valueClass || Short.class == valueClass) {
            return convertFromNumber((Short) value, clazz);
        } else if (byte.class == valueClass || Byte.class == valueClass) {
            return convertFromNumber((Byte) value, clazz);
        } else if (float.class == valueClass || Float.class == valueClass) {
            return convertFromNumber((Float) value, clazz);
        } else if (String.class == valueClass) {
            return convertFromString((String) value, clazz);
        } else {
            return value;
        }
    }

    private static Object convertFromString(String value, Class<?> clazz) {
        if (int.class == clazz || Integer.class == clazz) {
            return Integer.parseInt(value);
        } else if (boolean.class == clazz || Boolean.class == clazz) {
            return Boolean.parseBoolean(value);
        } else if (long.class == clazz || Long.class == clazz) {
            return Long.parseLong(value);
        } else if (double.class == clazz || Double.class == clazz) {
            return Double.parseDouble(value);
        } else if (short.class == clazz || Short.class == clazz) {
            return Short.parseShort(value);
        } else if (byte.class == clazz || Byte.class == clazz) {
            return Byte.parseByte(value);
        } else if (float.class == clazz || Float.class == clazz) {
            return Byte.parseByte(value);
        } else {
            return value;
        }
    }

    private static Object convertFromNumber(Number value, Class<?> clazz) {
        if (value == null) {
            return null;
        }
        if (int.class == clazz || Integer.class == clazz) {
            return value.intValue();
        } else if (boolean.class == clazz || Boolean.class == clazz) {
            return value != null ? (value.doubleValue() > 0 ? true : false) : null;
        } else if (long.class == clazz || Long.class == clazz) {
            return value.longValue();
        } else if (double.class == clazz || Double.class == clazz) {
            return value.doubleValue();
        } else if (short.class == clazz || Short.class == clazz) {
            return value.shortValue();
        } else if (byte.class == clazz || Byte.class == clazz) {
            return value.byteValue();
        } else if (float.class == clazz || Float.class == clazz) {
            return value.floatValue();
        } else if (String.class == clazz) {
            return value.toString();
        } else {
            return value;
        }
    }
}
