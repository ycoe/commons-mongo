package com.duoec.commons.mongo;

import com.duoec.commons.mongo.annotation.Ignore;
import com.duoec.commons.mongo.exceptions.YMongoException;
import com.duoec.commons.mongo.reflection.BeanUtils;
import com.duoec.commons.mongo.reflection.ClassUtils;
import com.duoec.commons.mongo.reflection.ReflectionUtils;
import com.duoec.commons.mongo.reflection.SimpleTypeConverter;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import com.duoec.commons.mongo.reflection.dto.FieldMate;
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
    public static final int OPTION_NULL = 0;
    public static final int OPTION_READ = 1;
    public static final int OPTION_INSERT = 2;
    public static final int OPTION_UPDATE = 4;

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
                if (ignore.read()) {
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
        return toDocument(entity, true, OPTION_NULL);
    }

    /**
     * @param entity  类实例
     * @param options 操作，可使用当前类的 OPTION_INSERT | OPTION_READ | OPTION_UPDATE，主要用于过滤@Ignore标识的字段
     * @param <T>
     * @return
     */
    public static <T> Document toDocument(T entity, int options) {
        return toDocument(entity, true, options);
    }

    private static <T> Document toDocument(T entity, boolean isMain, int options) {
        if (entity == null) {
            return null;
        }

        Class clazz = entity.getClass();
        ClassMate classMate = ReflectionUtils.getClassMate(clazz);

        Document doc = new Document();
        for (Map.Entry<String, FieldMate> entry : classMate.getFieldMateMap().entrySet()) {
            String name = entry.getKey();
            FieldMate fieldMate = entry.getValue();

            if (options > 0) {
                Ignore ignore = fieldMate.getIgnore();
                if (ignore != null) {
                    //如果被标识为@Ignore
                    if (ignore.insert() && (options & OPTION_INSERT) == OPTION_INSERT) {
                        continue;
                    } else if (ignore.read() && (options & OPTION_READ) == OPTION_READ) {
                        continue;
                    } else if (ignore.update() && (options & OPTION_UPDATE) == OPTION_UPDATE) {
                        continue;
                    }
                }
            }

            Object value = ReflectionUtils.getField(fieldMate, entity);
            if (value == null) {
                //值为空
                continue;
            }

            if (fieldMate.isSimpleType()) {
                //简单类型
                if ("id".equals(name) && isMain) {
                    //写_id
                    name = "_id";
                }
            } else {
                //非简单类型
                if (fieldMate.isList()) {
                    //List
                    value = toListDocument((List) value, options);
                } else if (fieldMate.isMap()) {
                    //Map
                    value = toMapDocument((Map) value, options);
                } else {
                    //普通实体
                    value = toDocument(value, false, options);
                }
            }

            if (value != null) {
                doc.put(name, value);
            }
        }

        if (doc.isEmpty() && !isMain) {
            return null;
        }
        return doc;
    }

    private static Document toMapDocument(Map value, int options) {
        Document doc = new Document();
        value.keySet().forEach(key -> {
            if (key.getClass() != String.class) {
                return;
            }
            Object val = value.get(key);
            if(!ClassUtils.isSimpleType(val.getClass())) {
                //如果不是基本数据类型时
                val = toDocument(val, false, options);
            }
            doc.put(key.toString(), val);
        });
        if (doc.isEmpty()) {
            return null;
        } else {
            return doc;
        }
    }

    private static List toListDocument(List value, int options) {
        if (value.isEmpty()) {
            return null;
        }
        List listDoc = Lists.newArrayList();
        for (Object item : value) {
            if (ClassUtils.isSimpleType(item.getClass())) {
                //如果是基本类型
                listDoc.add(item);
                continue;
            }

            //非基本类型
            Document doc = toDocument(item, false, options);
            if (doc != null) {
                listDoc.add(doc);
            }
        }
        if (listDoc.isEmpty()) {
            return null;
        } else {
            return listDoc;
        }
    }

    public static <T> Document getUpdateDocument(T entity) {
        UpdateDoc updateDoc = getUpdateData(null, entity);
        return updateDoc.toDocument();
    }

    private static UpdateDoc getUpdateData(String parentPaths, Object entity) {
        UpdateDoc updateDoc = new UpdateDoc();
        ClassMate classMate = ReflectionUtils.getClassMate(entity.getClass());
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
                putUnsetDocument(parentPaths, updateDoc, fieldName);
                continue;
            }

            if (fieldMate.isSimpleType()) {
                //简单类型
                updateDoc.addSet((parentPaths == null) ? fieldName : parentPaths + "." + fieldName, value);
                continue;
            }

            //非简单类型
            if (fieldMate.isList()) {
                // List
                value = getListDocument((List) value, fieldMate);
                updateDoc.addSet((parentPaths == null) ? fieldName : parentPaths + "." + fieldName, value);
                continue;
            } else if (fieldMate.isMap()) {
                // Map
                value = getMapDocument(value, fieldMate);
                updateDoc.addSet((parentPaths == null) ? fieldName : parentPaths + "." + fieldName, value);
                continue;
            } else {
                //JavaBean
                UpdateDoc itemUpdateDoc = getUpdateData(null, value);
                Document setDocs = itemUpdateDoc.getSetDocs();
                if (setDocs != null && !setDocs.isEmpty()) {
                    updateDoc.addSet((parentPaths == null) ? fieldName : parentPaths + "." + fieldName, setDocs);
                } else {
                    putUnsetDocument(parentPaths, updateDoc, fieldName);
                }
                continue;
            }
        }

        return updateDoc;
    }

    private static List getListDocument(List listValue, FieldMate fieldMate) {
        List listDocs = Lists.newArrayList();
        if (!ClassUtils.isSimpleType(fieldMate.getGenericType())) {
            //如果泛型是不简单类型
            for (Object item : listValue) {
                UpdateDoc itemData = getUpdateData(null, item);
                Document setDocs = itemData.getSetDocs();
                if (setDocs != null) {
                    listDocs.add(setDocs);
                }
            }
        } else {
            //简单类型
            for (Object item : listValue) {
                listDocs.add(item);
            }
        }

        return listDocs;
    }

    private static Document getMapDocument(Object value, FieldMate fieldMate) {
        Map<String, Object> mapValue = (Map<String, Object>) value;
        Document mapDoc = new Document();
        if (!ClassUtils.isSimpleType(fieldMate.getGenericType())) {
            //如果泛型是不简单类型
            for (Map.Entry<String, Object> itemEntry : mapValue.entrySet()) {
                UpdateDoc itemData = getUpdateData(null, itemEntry.getValue());
                Document setDocs = itemData.getSetDocs();
                if (setDocs != null) {
                    mapDoc.put(itemEntry.getKey(), setDocs);
                }
            }
        } else {
            for (Map.Entry<String, Object> itemEntry : mapValue.entrySet()) {
                mapDoc.put(itemEntry.getKey(), itemEntry.getValue());
            }
        }

        return mapDoc;
    }

    private static void putUnsetDocument(String parents, UpdateDoc updateDoc, String fieldName) {
        if (Strings.isNullOrEmpty(parents)) {
            updateDoc.addUnset(fieldName, true);
        } else {
            updateDoc.addUnset(parents + "." + fieldName, true);
        }
    }

    /**
     * 获取某个实体的所有字符Projections
     *
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
                if (ignore.read()) {
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
