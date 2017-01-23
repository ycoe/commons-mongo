package com.duoec.commons.mongo.core;

import com.alibaba.fastjson.JSONObject;
import com.duoec.commons.mongo.reflection.dto.AutoIncrementInfo;
import com.duoec.commons.mongo.MongoConverter;
import com.duoec.commons.mongo.Pagination;
import com.duoec.commons.mongo.annotation.AutoIncrement;
import com.duoec.commons.mongo.codec.YCodec;
import com.duoec.commons.mongo.reflection.ReflectionUtils;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import com.duoec.commons.mongo.reflection.dto.FieldMate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycoe on 16/5/2.
 */
public abstract class BaseEntityDao<T> extends BaseDao {
    private static final Logger logger = LoggerFactory.getLogger(BaseEntityDao.class);

    protected abstract Class<T> getEntityClass();

    public void insertEntities(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        long t1 = System.currentTimeMillis();
        MongoCollection<T> collect = getMongoCollection();
        long t2 = System.currentTimeMillis();
        collect.insertMany(entities);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:insertEntities(List<T> entities),t1:{},t2:{},size:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, entities.size());
        }
    }

    public void insert(T entity) {
        long t1 = System.currentTimeMillis();
        MongoCollection<T> collect = getMongoCollection();
        long t2 = System.currentTimeMillis();
        collect.insertOne(entity);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:insert(T entity),t1:{},t2:{},entity:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, JSONObject.toJSONString(entity));
        }
    }

    /**
     * 通过ID更新
     * 如果字段为null，会进行$unset操作！
     *
     * @param entity
     */
    public void updateOneByEntityId(T entity) {
        Class clazz = entity.getClass();
        ClassMate classMate = ReflectionUtils.getClassMate(clazz);
        FieldMate fieldMate = classMate.getFieldMate("id");
        Object id = ReflectionUtils.getField(fieldMate, entity);
        updateOne(Filters.eq("_id", id), entity);
    }

    /**
     * 通过查询条件更新一条记录
     *
     * @param query
     * @param entity
     */
    public void updateOne(Bson query, T entity) {
        Document doc = getDocument(entity);
        doc.remove("_id");
        super.updateOne(query, new Document("$set", doc));
    }

    /**
     * 本方法更新会清空数据为null的字段，如果不需要清空，请使用updateOne()方法！
     *
     * @param entity
     */
    public void update(T entity) {
        Class clazz = entity.getClass();
        ClassMate classMate = ReflectionUtils.getClassMate(clazz);
        FieldMate fieldMate = classMate.getFieldMate("id");
        Object id = ReflectionUtils.getField(fieldMate, entity);
        Document updateData = MongoConverter.getUpdateDocument(entity);
        super.updateOne(Filters.eq("_id", id), updateData);
    }

    protected Document getDocument(T entity) {
        Document doc = MongoConverter.toDocument(entity);
        Class clazz = entity.getClass();
        ClassMate classMate = ReflectionUtils.getClassMate(clazz);
        AutoIncrementInfo autoIncrementInfo = classMate.getAutoIncrementInfo();

        if (autoIncrementInfo.isAutoIncrement()) {
            Object entityId = doc.get("_id");
            if (entityId == null || "0".equals(entityId.toString())) {
                //参见文档:https://docs.mongodb.org/manual/tutorial/create-an-auto-incrementing-field/
                Object id = getNextSequence(autoIncrementInfo);
                doc.put("_id", id);
                FieldMate fieldMate = classMate.getFieldMate("id");
                ReflectionUtils.setField(fieldMate, entity, id); //写入entity
            }
        }
        return doc;
    }

    /**
     * 更新某条记录,并返回更新前的Document,如果没有命中,则直接插入,并返回null!
     *
     * @param query  查询条件
     * @param entity 更新的实体
     * @return
     */
    public T updateAndReturnBeforeDoc(Bson query, T entity) {
        long t1 = System.currentTimeMillis();
        MongoCollection<T> collection = getMongoCollection();
        long t2 = System.currentTimeMillis();
        FindOneAndReplaceOptions replaceOptions = new FindOneAndReplaceOptions();
        replaceOptions.upsert(true); //如果没有命中,则直接插入
        T doc = collection.findOneAndReplace(query, entity, replaceOptions);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:updateAndReturnBeforeDoc(),t1:{},t2:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2);
        }
        return doc;
    }

    public T getEntityById(Object id) {
        return getEntityById(id, null);
    }

    public T getEntityById(Object id, Bson projection) {
        MongoCollection<T> collection = getMongoCollection();
        long t2 = System.currentTimeMillis();
        FindIterable<T> findIterable = collection.find(new Document("_id", id));
        if (projection != null) {
            findIterable = findIterable.projection(projection);
        }
        T doc = findIterable.first();
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:getEntityById(),id={}, t={}", getDatabaseName(), getCollectionName(), id, t3 - t2);
        }
        return doc;
    }

    public List<T> findEntities(Bson query, Bson sort, Integer skip, Integer limit) {
        return findEntities(query, sort, skip, limit, null);
    }

    public List<T> findEntities(Bson query, Bson sort, Integer skip, Integer limit, Bson projection) {
        long t1 = System.currentTimeMillis();
        MongoCollection<T> collection = getMongoCollection();
        long t2 = System.currentTimeMillis();
        if (query == null) {
            query = new Document();
        }
        FindIterable<T> findIterable = collection.find(query);
        if (sort != null) {
            findIterable = findIterable.sort(sort);
        }
        if (skip != null) {
            findIterable = findIterable.skip(skip);
        }
        if (limit != null) {
            findIterable = findIterable.limit(limit);
        }
        if (projection != null) {
            findIterable = findIterable.projection(projection);
        }
        MongoCursor<T> it = findIterable.iterator();
        long t3 = System.currentTimeMillis();
        List<T> list = new ArrayList<>();
        while (it.hasNext()) {
            T item = it.next();
            list.add(item);
        }
        long t4 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:findEntities(),query={}, sort={}, skip={}, limit={}, t={}", getDatabaseName(), getCollectionName(), JSONObject.toJSONString(query), sort, skip, limit, t3 - t2);
            logger.info("查询记录1,t1:{},t2:{},t3:{}", t2 - t1, t3 - t2, t4 - t3);
        }
        return list;
    }

    /**
     * 遍历所有结果
     *
     * @param sort
     * @param skip
     * @param limit
     * @return
     */
    public List<T> findAllEntities(Bson sort, Integer skip, Integer limit) {
        return findEntities(new Document(), sort, skip, limit);
    }

    public T getEntity(Bson query, Bson projection) {
        long t1 = System.currentTimeMillis();
        MongoCollection<T> collection = getMongoCollection();
        long t2 = System.currentTimeMillis();
        FindIterable<T> findIterable = collection.find(query).limit(1).skip(0);
        if (projection != null) {
            findIterable = findIterable.projection(projection);
        }
        MongoCursor<T> it = findIterable.iterator();
        long t3 = System.currentTimeMillis();
        T doc;
        if (it.hasNext()) {
            doc = it.next();
        } else {
            doc = null;
        }
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:findAllEntities(),query={}, t={}", getDatabaseName(), getCollectionName(), query, t3 - t2);
        }
        return doc;
    }

    public T getEntity(Bson query) {
        return getEntity(query, null);
    }

    protected Object getNextSequence(AutoIncrementInfo autoIncrementInfo) {
        AutoIncrement autoIncrement = autoIncrementInfo.getAutoIncrement();
        String name = autoIncrement.value();
        if (Strings.isNullOrEmpty(name)) {
            name = getCollectionName();
        }
        Long value = getNextSequence(name, autoIncrement.start(), autoIncrement.step());
        if (autoIncrementInfo.isInteger()) {
            return value.intValue();
        } else {
            return value;
        }
    }

    /**
     * 获取自增ID
     *
     * @param name
     * @return
     */
    public Long getNextSequence(String name, long start, int step) {
        Document query = new Document("_id", name);
        Document increase = new Document("seq", step);
        Document updateQuery = new Document("$inc", increase);
        MongoCollection collection = getDocumentMongoCollection("counters");
        Document result = (Document) collection.findOneAndUpdate(query, updateQuery);
        if (result == null) {
            //先插入
            query.put("seq", start + step);
            collection.insertOne(query);
            return start;
        }

        return result.getLong("seq");
    }

    /**
     * entity must contain a id which is identical with the one in database if the operation is update
     * replace the old document
     *
     * @param query
     * @param entity
     * @return
     */
    public T findOneAndReplace(Bson query, T entity) {
        long t1 = System.currentTimeMillis();
        MongoCollection<T> collection = getMongoCollection();
        long t2 = System.currentTimeMillis();
        T doc = collection.findOneAndReplace(query, entity);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:findOneAndReplace(),t1:{},t2:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2);
        }
        return doc;
    }

    /**
     * entity must contain a id which is identical with the one in database if the operation is update
     *
     * @param query
     * @param entity
     * @param upsert when it is set true it will insert if there are no matches to the query filter
     * @return
     */
    public T findOneAndReplace(Bson query, T entity, boolean upsert) {
        long t1 = System.currentTimeMillis();
        MongoCollection<T> collection = getMongoCollection();
        long t2 = System.currentTimeMillis();
        FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
        options.upsert(true);
        T doc = collection.findOneAndReplace(query, entity, options);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:findOneAndReplace(),t1:{},t2:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2);
        }
        return doc;
    }

    public Pagination<T> findEntitiesWithTotal(Bson query, Bson sort, Integer skip, Integer limit, Bson projections) {
        Pagination<T> pagination = new Pagination();
        pagination.setList(findEntities(query, sort, skip, limit, projections));
        pagination.setTotal(count(query));
        return pagination;
    }

    public Pagination<T> findEntitiesWithTotal(Bson query, Bson sort, Integer skip, Integer limit) {
        return findEntitiesWithTotal(query, sort, skip, limit, null);
    }

    /**
     * 获取某个集合(使用Codec解析)
     *
     * @return
     */
    public MongoCollection<T> getMongoCollection() {
        return classRegistry(getEntityClass());
    }

    public <D> BaseEntityDao<D> getEntityDao(final Class<D> clazz) {
        String databaseName = getDatabaseName();
        String collectionName = getCollectionName();

        YMongoClient client = getMongodbClient();
        BaseEntityDao<D> entityDao = new BaseEntityDao<D>() {
            @Override
            protected Class<D> getEntityClass() {
                return clazz;
            }

            @Override
            protected String getDatabaseName() {
                return databaseName;
            }

            @Override
            protected String getCollectionName() {
                return collectionName;
            }

            @Override
            public YMongoClient getMongodbClient() {
                return client;
            }
        };
        return entityDao;
    }

    private <D> MongoCollection<D> classRegistry(final Class<D> registryClass) {
        ClassMate classMate = ReflectionUtils.getClassMate(registryClass);
        List<CodecRegistry> codecRegistryList = Lists.newArrayList();
        classMate.getReferClassList().forEach(clazz -> {
            CodecRegistry codecProvider = CodecRegistries.fromProviders(new CodecProvider() { // NOSONAR
                @Override
                public <D> Codec<D> get(Class<D> clazz, CodecRegistry registry) {
                    if (registryClass == clazz) {
                        return (Codec<D>) new YCodec(registry, clazz, BaseEntityDao.this::getNextSequence);
                    } else {
                        return null;
                    }
                }
            });
            codecRegistryList.add(codecProvider);
            codecRegistryList.add(MongoClient.getDefaultCodecRegistry());
        });
        if (codecRegistryList.isEmpty()) {
            logger.error("codecProviders must not be null or empty！Class={}", registryClass.getName());
        }
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(codecRegistryList);
        return getDocumentMongoCollection().withCodecRegistry(codecRegistry).withDocumentClass(registryClass);
    }
}
