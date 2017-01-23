package com.duoec.commons.mongo.codec;

import com.duoec.commons.mongo.annotation.Ignore;
import com.duoec.commons.mongo.exceptions.YMongoException;
import com.duoec.commons.mongo.reflection.ClassUtils;
import com.duoec.commons.mongo.reflection.ReflectionUtils;
import com.duoec.commons.mongo.reflection.SimpleTypeConverter;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import com.duoec.commons.mongo.reflection.dto.FieldMate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bson.BsonBinarySubType;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ycoe on 16/6/22.
 */
public class YCodec<T> implements Codec<T> {
    private static final Logger logger = LoggerFactory.getLogger(YCodec.class);

    private static final String ID_FIELD_NAME = "_id";

    private static final BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap();
    private static final Map<Class, Codec> CODEC_MAP = Maps.newHashMap();

    private CodecRegistry registry;

    private Class<T> clazz;

    /**
     * 是否主document,此时需要转换
     */
    private boolean isMainDocument = true;

    private AutoIncrementFun autoIncrementFun;

    private YCodec(CodecRegistry registry, Class clazz, boolean isMainDocument, AutoIncrementFun autoIncrementFun) {
        this(registry, clazz, autoIncrementFun);
        this.isMainDocument = isMainDocument;
    }

    public YCodec(CodecRegistry registry, Class clazz, AutoIncrementFun autoIncrementFun) {
        this.registry = registry;
        this.clazz = clazz;
        this.autoIncrementFun = autoIncrementFun;
    }

    private ClassMate getClassMate(Class clazz) {
        ClassMate classMate = ReflectionUtils.getClassMate(clazz);
        List<Class> list = classMate.getReferClassList();
        for (Class refClass : list) {
            Codec codec = new YCodec(registry, refClass, false, autoIncrementFun);
            CODEC_MAP.put(refClass, codec);
        }
        return classMate;
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        ClassMate classMate = getClassMate(clazz);
        T entity = newInstance(clazz);
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String name = reader.readName();
            if (ID_FIELD_NAME.equals(name) && isMainDocument) {
                name = "id";
            }

            FieldMate fieldMate = classMate.getFieldMate(name);
            if (fieldMate == null) {
                logger.debug("skip " + classMate.getClazz().getName() + "." + name + "'s value");
                reader.skipValue();
                continue;
            }
            setEntityField(classMate, fieldMate, reader, entity, decoderContext);
        }
        reader.readEndDocument();
        return entity;
    }

    private Object readValue(final Type fieldType, final BsonReader reader, final DecoderContext decoderContext) {
        BsonType bsonType = reader.getCurrentBsonType();
        if (bsonType == BsonType.NULL) {
            reader.readNull();
            return null;
        } else if (bsonType == BsonType.OBJECT_ID) {
            return reader.readObjectId().toString();
        } else if (bsonType == BsonType.ARRAY) {
            if (fieldType == Class.class) {
                logger.error("错误类型！");
            }
            return readList((ParameterizedType) fieldType, reader, decoderContext);
        } else if (bsonType == BsonType.BINARY) {
            byte bsonSubType = reader.peekBinarySubType();
            if (bsonSubType == BsonBinarySubType.UUID_STANDARD.getValue() || bsonSubType == BsonBinarySubType.UUID_LEGACY.getValue()) {
                return registry.get(UUID.class).decode(reader, decoderContext);
            }
        } else if (bsonType == BsonType.DOCUMENT) {
            //对象或Map
            if (fieldType instanceof ParameterizedTypeImpl) {
                if (((ParameterizedTypeImpl) fieldType).getRawType() == Map.class) {
                    //map
                    return readMap((ParameterizedTypeImpl) fieldType, reader, decoderContext);
                } else {
                    throw new UnsupportedOperationException("");
                }
            }
            return new YCodec(registry, (Class) fieldType, false, autoIncrementFun).decode(reader, decoderContext);
        }
        Codec<?> codec = registry.get(bsonTypeClassMap.get(bsonType));
        return codec.decode(reader, decoderContext);
    }

    private List<Object> readList(ParameterizedType fieldType, final BsonReader reader, final DecoderContext decoderContext) {
        Type[] argumentTypes = fieldType.getActualTypeArguments();
        reader.readStartArray();
        List<Object> list = Lists.newArrayList();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(readValue(argumentTypes[0], reader, decoderContext));
        }
        reader.readEndArray();
        return list;
    }

    private Map<String, Object> readMap(ParameterizedTypeImpl fieldType, final BsonReader reader, final DecoderContext decoderContext) {
        Type[] typeArguments = fieldType.getActualTypeArguments();
        Class<?> mapValueClass = null;
        if (typeArguments != null && typeArguments.length == 2) {
            mapValueClass = (Class<?>) typeArguments[1];
        }
        Map<String, Object> map = Maps.newHashMap();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            map.put(reader.readName(), readValue(mapValueClass, reader, decoderContext));
        }
        reader.readEndDocument();
        return map;
    }

    private void setEntityField(ClassMate classMate, FieldMate fieldMate, BsonReader reader, T entity, DecoderContext decoderContext) {
        Ignore ignore = fieldMate.getIgnore();
        if (ignore != null) {
            //被标识为@Ignore
            if (ignore.read()) {
                logger.debug("skip " + classMate.getClazz().getName() + "." + fieldMate.getName() + "'s value");
                reader.skipValue();
                return;
            }
        }
        Object value = readValue(fieldMate.getField().getGenericType(), reader, decoderContext);
        if (value == null) {
            return;
        }

        Class<?> fieldType = fieldMate.getField().getType();
        if (fieldMate.isSimpleType() && !ClassUtils.equal(fieldType, value.getClass())) {
            //尝试调用基本数据转换
            value = SimpleTypeConverter.convert(value, fieldType);
        }
        ReflectionUtils.setField(fieldMate, entity, value);

        //直接设置，当类型不能时才尝试简单转换
//        try {
//            ReflectionUtils.setField(fieldMate, entity, value);
//        }catch (Exception e) {
//            Class<?> fieldType = fieldMate.getField().getType();
//            value = SimpleTypeConverter.convert(value, fieldType);
//            try {
//                ReflectionUtils.setField(fieldMate, entity, value);
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
    }

    private T newInstance(Class<T> clazz) {
        if (clazz.isAssignableFrom(List.class)) {
            return (T) Lists.newArrayList();
        } else if (clazz.isAssignableFrom(Map.class)) {
            return (T) Maps.newHashMap();
        }
        return ClassUtils.instantiate(clazz);
    }

    @Override
    public void encode(BsonWriter writer, T entity, EncoderContext encoderContext) {
        ClassMate classMate = getClassMate(clazz);
        writer.writeStartDocument();
        for (final Map.Entry<String, FieldMate> entry : classMate.getFieldMateMap().entrySet()) {
            FieldMate fieldMate = entry.getValue();
            Ignore ignore = fieldMate.getIgnore();
            if (ignore != null) {
                //如果被标识为@Ignore
                if (ignore.insert()) {
                    continue;
                }
            }

            String name = entry.getKey();
            if ("id".equals(name) && isMainDocument) {
                //写_id
                writer.writeName(ID_FIELD_NAME);
                writeId(fieldMate, entity, writer, encoderContext);
                continue;
            }

            Object value = ReflectionUtils.getField(fieldMate, entity);
            if (value != null) {
                writer.writeName(name);
                writeValue(writer, encoderContext, value);
            }
        }
        writer.writeEndDocument();
    }

    private void writeId(FieldMate fieldMate, T entity, BsonWriter writer, EncoderContext encoderContext) {
        Object value = ReflectionUtils.getField(fieldMate, entity);
        Class type = fieldMate.getField().getType();
        if (value == null) {
            if (type == String.class) {
                //尝试生成UUID
                value = ObjectId.get().toString();
                ReflectionUtils.setField(fieldMate, entity, value);
            } else if (fieldMate.getAutoIncrementInfo() != null) {
                value = autoIncrementFun.getNextSequence(fieldMate.getAutoIncrementInfo());
                ReflectionUtils.setField(fieldMate, entity, value);
            }
        } else if (ClassUtils.isPrimitiveOrWrapper(type)) {
            Long longValue = (Long) SimpleTypeConverter.convert(value, Long.class);
            if (longValue == 0 && fieldMate.getAutoIncrementInfo() != null) {
                value = autoIncrementFun.getNextSequence(fieldMate.getAutoIncrementInfo());
                ReflectionUtils.setField(fieldMate, entity, value);
            }
        }
        writeValue(writer, encoderContext, value);
    }

    private void writeValue(final BsonWriter writer, final EncoderContext encoderContext, final Object value) {
        Class<?> valueType = value.getClass();
        if (Iterable.class.isAssignableFrom(valueType)) {
            writeIterable(writer, (Iterable<Object>) value, encoderContext.getChildContext());
        } else if (Map.class.isAssignableFrom(valueType)) {
            writeMap(writer, (Map<String, Object>) value, encoderContext.getChildContext());
        } else {
            Codec codec;
            if (CODEC_MAP.containsKey(valueType)) {
                codec = CODEC_MAP.get(valueType);
            } else {
                codec = registry.get(value.getClass());
            }

            encoderContext.encodeWithChildContext(codec, writer, value);
        }
    }

    private void writeIterable(final BsonWriter writer, final Iterable<Object> list, final EncoderContext encoderContext) {
        writer.writeStartArray();
        for (final Object value : list) {
            if (value != null) {
                writeValue(writer, encoderContext, value);
            }
        }
        writer.writeEndArray();
    }

    private boolean skipField(final EncoderContext encoderContext, final String key) {
        return encoderContext.isEncodingCollectibleDocument() && key.equals(ID_FIELD_NAME);
    }

    private void writeMap(final BsonWriter writer, final Map<String, Object> map, final EncoderContext encoderContext) {
        writer.writeStartDocument();

        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (skipField(encoderContext, entry.getKey())) {
                continue;
            }
            Object value = entry.getValue();
            if (value != null) {
                writer.writeName(entry.getKey());
                writeValue(writer, encoderContext, value);
            }
        }
        writer.writeEndDocument();
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }
}
