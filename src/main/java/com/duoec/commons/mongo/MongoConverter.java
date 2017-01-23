package com.duoec.commons.mongo;

import com.alibaba.fastjson.JSONObject;
import com.duoec.commons.mongo.exceptions.YMongoException;
import com.duoec.commons.mongo.reflection.SimpleTypeConverter;
import com.duoec.commons.mongo.reflection.dto.FieldMate;
import com.duoec.commons.mongo.annotation.Ignore;
import com.duoec.commons.mongo.reflection.BeanUtils;
import com.duoec.commons.mongo.reflection.ClassUtils;
import com.duoec.commons.mongo.reflection.ReflectionUtils;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by ycoe on 16/3/18.
 */
public class MongoConverter {

    private static final Logger logger = LoggerFactory.getLogger(MongoConverter.class);

    private static final Map<Class, Document> PROJECTIONS = Maps.newConcurrentMap();

    private MongoConverter() {
    }

    /**
     * Document转换成类，此时，如果类中使用@Ignore时，当成是read
     *
     * @param <T>
     * @return
     */
    public static <T> T toEntity(Document doc, Class<T> clazz) {
        if (doc == null) {
            return null;
        }

        ClassMate classMate = ReflectionUtils.getClassMate(clazz);

        T data;
        try {
            data = clazz.newInstance();
        } catch (Exception e) {
            logger.error("初始化类" + clazz.getName() + "出错!", e);
            throw new YMongoException(e);
        }
        for (Map.Entry<String, FieldMate> entry : classMate.getFieldMateMap().entrySet()) {
            FieldMate fieldMate = entry.getValue();
            Ignore ignore = fieldMate.getIgnore();
            if (ignore != null) {
                //标识为忽略的字段
                if(ignore.read()) {
                    continue;
                }
            }

            String key = entry.getKey();
            String docKey = key;
            if ("id".equals(docKey)) {
                docKey = "_id";
            }
            if (!doc.containsKey(docKey)) {
                //如果key值不存在!
                if (!"id".equals(key)) {
                    continue;
                } else if (doc.containsKey(key)) {
                    docKey = key;
                }
            }
            Method method = fieldMate.getSetter().getMethod();
            Class<?> type = fieldMate.getField().getType();

            if (BeanUtils.isSimpleProperty(type)) {
                //简单类型
                try {
                    Object obj = doc.get(docKey);
                    String str = null;
                    if (obj != null) {
                        str = doc.get(docKey).toString();
                    }
                    Object value = SimpleTypeConverter.convert(str, type);
                    method.invoke(data, value);
                } catch (Exception e) {
                    logger.error("类型转换错误class={}, name={}, type={}", clazz.getName(), key, type.getName());
                    throw new YMongoException(e);
                }
            } else {
                //非简单类型处理
                if (type.isAssignableFrom(List.class)) {
                    //List<?>
                    Class genericType = fieldMate.getGenericType();
                    if (genericType != null) {
                        List vs = new ArrayList();
                        if (BeanUtils.isSimpleProperty(genericType)) {
                            List<Object> values = (List<Object>) doc.get(docKey);
                            for (Object value : values) {
                                vs.add(value);
                            }
                        } else {
                            List<Document> values = (List<Document>) doc.get(docKey);
                            for (Document value : values) {
                                Object genericClazzObj = toEntity(value, genericType);
                                vs.add(genericClazzObj);
                            }
                        }
                        try {
                            method.invoke(data, vs);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            throw new YMongoException(e);
                        }
                    }
                } else if (type.isAssignableFrom(Map.class)) {
                    Class valClass = fieldMate.getGenericType();
                    //整个map的值
                    Document kvs = (Document) doc.get(docKey, type);
                    Map dataMap = new HashMap();
                    Set<String> ks = kvs.keySet();

                    if (BeanUtils.isSimpleProperty(valClass)) {
                        //简单类型
                        for (String k : ks) {
                            Object v = kvs.get(k);
                            dataMap.put(k, v);
                        }
                    } else {
                        for (String k : ks) {
                            Document v = (Document) kvs.get(k);
                            dataMap.put(k, toEntity(v, valClass));
                        }
                    }

                    try {
                        method.invoke(data, dataMap);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new YMongoException(e);
                    }
                } else {
                    Document value = (Document) doc.get(docKey);
                    try {
                        method.invoke(data, toEntity(value, type));
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new YMongoException(e);
                    }
                }
            }
        }

        return data;
    }

    public static <T> Document toDocument(T entity) {
        if (entity == null) {
            return null;
        }

        Document doc = Document.parse(JSONObject.toJSONString(entity));
        Method method;
        try {
            method = entity.getClass().getMethod("getId");
            Object id = method.invoke(entity);
            doc.put("_id", id);
            doc.remove("id");
        } catch (Exception e) {
            logger.error("设置_id失败!", e);
        }
        return doc;
    }

    public static <T> Document getUpdateDocument(T entity) {
        Document doc = getUpdateData(null, entity);
        Document setFields = (Document) doc.get("$set");
        if (setFields != null && setFields.containsKey("id")) {
            setFields.remove("id");
        }
        return doc;
    }

    private static Document getUpdateData(String parents, Object entity) {
        ClassMate classMate = ReflectionUtils.getClassMate(entity.getClass());
        Document updateData = new Document();
        Document setFields = new Document();
        Document unsetFields = new Document();
        for (Map.Entry<String, FieldMate> entry : classMate.getFieldMateMap().entrySet()) {
            FieldMate fieldMate = entry.getValue();
            Ignore ignore = fieldMate.getIgnore();
            if (ignore != null) {
                //被忽略的列
                if (ignore.update()) {
                    continue;
                }
            }
            Object value = ReflectionUtils.getField(fieldMate, entity);
            String fieldName = entry.getKey();
            if (value == null) {
                // 需要$unset掉
                putUnsetDocument(parents, unsetFields, fieldName);
                continue;
            }

            if (!fieldMate.isSimpleType()) {
                //非基础类型：map / list / 类
                if (fieldMate.isList()) {
                    if (!ClassUtils.isSimpleType(fieldMate.getGenericType())) {
                        //如果泛型是不简单类型
                        value = getListDocument(value);
                    } else {
                        value = null;
                    }
                } else if (fieldMate.isMap()) {
                    if (!ClassUtils.isSimpleType(fieldMate.getGenericType())) {
                        //如果泛型是不简单类型
                        value = getMapDocument(value);
                    } else {
                        value = null;
                    }
                } else {
                    Document itemData = getUpdateData(null, value);
                    if (itemData.containsKey("$set")) {
                        value = itemData.get("$set");

                        if (itemData.containsKey("$unset")) {
                            Document unsetDoc = (Document) itemData.get("$unset");
                            String currentParent = (parents == null) ? fieldName : parents + "." + fieldName;
                            unsetDoc.keySet().forEach(key -> putUnsetDocument(currentParent, unsetFields, key));
                        }
                    } else {
                        value = null;
                    }
                }
            }

            if (value == null) {
                putUnsetDocument(parents, unsetFields, fieldName);
            } else {
                //基础类型
                setFields.put(fieldName, value);
            }
        }

        if (!setFields.isEmpty()) {
            updateData.put("$set", setFields);
        }
        if (!unsetFields.isEmpty()) {
            updateData.put("$unset", unsetFields);
        }
        return updateData;
    }

    private static Document getMapDocument(Object value) {
        Map<String, Object> mapValue = (Map<String, Object>) value;
        Document mapDoc = new Document();
        for (Map.Entry<String, Object> itemEntry : mapValue.entrySet()) {
            Document itemData = getUpdateData(null, itemEntry.getValue());
            if (itemData.containsKey("$set")) {
                mapDoc.put(itemEntry.getKey(), itemData.get("$set"));
            }
        }
        return mapDoc;
    }

    private static List getListDocument(Object value) {
        List listValue = (List) value;
        List<Document> documents = Lists.newArrayList();
        for (Object item : listValue) {
            Document itemData = getUpdateData(null, item);
            if (itemData.containsKey("$set")) {
                documents.add((Document) itemData.get("$set"));
            }
        }
        return documents;
    }

    private static void putUnsetDocument(String parents, Document setUnsetFields, String fieldName) {
        if (Strings.isNullOrEmpty(parents)) {
            setUnsetFields.put(fieldName, true);
        } else {
            setUnsetFields.put(parents + "." + fieldName, true);
        }
    }

    /**
     * 获取某个实体的所有字符Projections
     * @param clazz
     * @return
     */
    public static Document getEntityProjections(Class clazz) {
        if (PROJECTIONS.containsKey(clazz)) {
            return PROJECTIONS.get(clazz);
        }
        ClassMate classMate = ReflectionUtils.getClassMate(clazz);
        Document projections = new Document();
        for (Map.Entry<String, FieldMate> entry : classMate.getFieldMateMap().entrySet()) {
            FieldMate fieldMate = entry.getValue();
            Ignore ignore = fieldMate.getIgnore();
            if (ignore != null) {
                if(ignore.read()) {
                    continue;
                }
            }
            projections.put(entry.getKey(), true);
        }
        PROJECTIONS.put(clazz, projections);
        System.out.println(projections.toJson());
        return projections;
    }
}
