package com.duoec.commons.mongo.core;

import com.alibaba.fastjson.JSONObject;
import com.duoec.commons.mongo.annotation.AutoIncrement;
import com.duoec.commons.mongo.codec.YCodec;
import com.duoec.commons.mongo.reflection.ReflectionUtils;
import com.duoec.commons.mongo.reflection.dto.AutoIncrementInfo;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ycoe on 17/1/19.
 */
public abstract class YCollection<TDocument> {
    private static final Logger logger = LoggerFactory.getLogger(YCollection.class);

    protected static final long SLOW_QUERY_TIME = 800;

    private MongoCollection<TDocument> mongoCollection;

    protected abstract String getDatabaseName();

    protected abstract String getCollectionName();

    public abstract Class<TDocument> getDocumentClass();

    protected abstract YMongoClient getYMongoClient();

    public String getNamespace() {
        return getDatabaseName() + getCollectionName();
    }

    /**
     * 判断某个条件的记录是否存在
     *
     * @param filter 查询条件
     * @return
     */
    public boolean exists(Bson filter) {
        return count(filter, new CountOptions()) > 0;
    }

    /**
     * 查询总数
     *
     * @return the number of documents in the collection
     */
    public long count() {
        return count(new BsonDocument(), new CountOptions());
    }

    /**
     * 查询某查询条件的总数
     *
     * @param filter the query filter
     * @return the number of documents in the collection
     */
    public long count(Bson filter) {
        return count(filter, new CountOptions());
    }

    /**
     * 查询某查询条件的总数
     *
     * @param filter  the query filter
     * @param options the options describing the count
     * @return the number of documents in the collection
     */
    public long count(Bson filter, CountOptions options) {
        long t1 = System.currentTimeMillis();
        long count = getDocumentMongoCollection().count(filter, options);

        slowLog(
                System.currentTimeMillis() - t1,
                "count(filter, options), filter:{}, options:{}",
                filter,
                options
        );
        return count;
    }

    /**
     * 获取某个字段的所有去重值
     * Gets the distinct values of the specified field name.
     *
     * @param fieldName   the field name
     * @param resultClass the class to cast any distinct items into.
     * @param <TResult>   the target type of the iterable.
     * @return an iterable of distinct values
     * @mongodb.driver.manual reference/command/distinct/ Distinct
     */
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Class<TResult> resultClass) {
        return distinct(fieldName, new BsonDocument(), resultClass);
    }

    /**
     * 获取某个字段的所有去重值
     * Gets the distinct values of the specified field name.
     *
     * @param fieldName   the field name
     * @param filter      the query filter
     * @param resultClass the class to cast any distinct items into.
     * @param <TResult>   the target type of the iterable.
     * @return an iterable of distinct values
     * @mongodb.driver.manual reference/command/distinct/ Distinct
     */
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Bson filter, Class<TResult> resultClass) {
        long t1 = System.currentTimeMillis();
        DistinctIterable result = getDocumentMongoCollection().distinct(fieldName, filter, resultClass);

        slowLog(
                System.currentTimeMillis() - t1,
                "distinct(fieldName, filter, resultClass), fieldName:{}, filter:{}, resultClass:{}",
                fieldName,
                filter,
                resultClass.getName()
        );
        return result;
    }

    /**
     * 查找所有的文档
     * Finds all documents in the collection.
     *
     * @return the find iterable interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    public FindIterable<TDocument> find() {
        return find(new BsonDocument(), getDocumentClass());
    }

    /**
     * 查找所有的文档
     * Finds all documents in the collection.
     *
     * @param resultClass the class to decode each document into
     * @param <TResult>   the target document type of the iterable.
     * @return the find iterable interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    public <TResult> FindIterable<TResult> find(Class<TResult> resultClass) {
        return find(new BsonDocument(), resultClass);
    }

    /**
     * 查找所有满足条件的文档
     * Finds all documents in the collection.
     *
     * @param filter the query filter
     * @return the find iterable interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    public FindIterable<TDocument> find(Bson filter) {
        return find(filter, getDocumentClass());
    }

    /**
     * 查找所有满足条件的文档
     * Finds all documents in the collection.
     *
     * @param filter      the query filter
     * @param resultClass the class to decode each document into
     * @param <TResult>   the target document type of the iterable.
     * @return the find iterable interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    public <TResult> FindIterable<TResult> find(Bson filter, Class<TResult> resultClass) {
        long t1 = System.currentTimeMillis();
        FindIterable iterable = getDocumentMongoCollection().find(filter, resultClass);

        slowLog(
                System.currentTimeMillis() - t1,
                "find(filter, resultClass), filter:{}, resultClass:{}",
                filter,
                resultClass.getName()
        );
        return iterable;
    }

    /**
     * 聚合
     * Aggregates documents according to the specified aggregation pipeline.
     *
     * @param pipeline the aggregate pipeline
     * @return an iterable containing the result of the aggregation operation
     * @mongodb.driver.manual aggregation/ Aggregation
     * @mongodb.server.release 2.2
     */
    public AggregateIterable<TDocument> aggregate(List<? extends Bson> pipeline) {
        return aggregate(pipeline, getDocumentClass());
    }

    /**
     * 聚合
     * Aggregates documents according to the specified aggregation pipeline.
     *
     * @param pipeline    the aggregate pipeline
     * @param resultClass the class to decode each document into
     * @param <TResult>   the target document type of the iterable.
     * @return an iterable containing the result of the aggregation operation
     * @mongodb.driver.manual aggregation/ Aggregation
     * @mongodb.server.release 2.2
     */
    public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> resultClass) {
        long t1 = System.currentTimeMillis();
        AggregateIterable iterable = getDocumentMongoCollection().aggregate(pipeline, resultClass);

        slowLog(
                System.currentTimeMillis() - t1,
                "aggregate(pipeline, resultClass), pipeline:{}, resultClass:{}",
                pipeline,
                resultClass.getName()
        );
        return iterable;
    }

    /**
     * Aggregates documents according to the specified map-reduce function.
     *
     * @param mapFunction    A JavaScript function that associates or "maps" a value with a key and emits the key and value pair.
     * @param reduceFunction A JavaScript function that "reduces" to a single object all the values associated with a particular key.
     * @return an iterable containing the result of the map-reduce operation
     * @mongodb.driver.manual reference/command/mapReduce/ map-reduce
     */
    public MapReduceIterable<TDocument> mapReduce(String mapFunction, String reduceFunction) {
        return mapReduce(mapFunction, reduceFunction, getDocumentClass());
    }

    /**
     * Aggregates documents according to the specified map-reduce function.
     *
     * @param mapFunction    A JavaScript function that associates or "maps" a value with a key and emits the key and value pair.
     * @param reduceFunction A JavaScript function that "reduces" to a single object all the values associated with a particular key.
     * @param resultClass    the class to decode each resulting document into.
     * @param <TResult>      the target document type of the iterable.
     * @return an iterable containing the result of the map-reduce operation
     * @mongodb.driver.manual reference/command/mapReduce/ map-reduce
     */
    public <TResult> MapReduceIterable<TResult> mapReduce(String mapFunction, String reduceFunction, Class<TResult> resultClass) {
        long t1 = System.currentTimeMillis();
        MapReduceIterable iterable = getDocumentMongoCollection().mapReduce(mapFunction, reduceFunction, resultClass);

        slowLog(
                System.currentTimeMillis() - t1,
                "mapReduce(mapFunction, reduceFunction, resultClass), mapFunction:{}, reduceFunction:{}, resultClass:{}",
                mapFunction,
                reduceFunction,
                resultClass.getName()
        );
        return iterable;
    }

    /**
     * Executes a mix of inserts, updates, replaces, and deletes.
     *
     * @param requests the writes to execute
     * @return the result of the bulk write
     * @throws com.mongodb.MongoBulkWriteException if there's an exception in the bulk write operation
     * @throws com.mongodb.MongoException          if there's an exception running the operation
     */
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends TDocument>> requests) {
        return bulkWrite(requests, new BulkWriteOptions());
    }

    /**
     * Executes a mix of inserts, updates, replaces, and deletes.
     *
     * @param requests the writes to execute
     * @param options  the options to apply to the bulk write operation
     * @return the result of the bulk write
     * @throws com.mongodb.MongoBulkWriteException if there's an exception in the bulk write operation
     * @throws com.mongodb.MongoException          if there's an exception running the operation
     */
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options) {
        long t1 = System.currentTimeMillis();
        BulkWriteResult result = getDocumentMongoCollection().bulkWrite(requests, options);

        slowLog(
                System.currentTimeMillis() - t1,
                "bulkWrite(requests, options), options:{}",
                options
        );
        return result;
    }

    /**
     * 插入单条记录
     * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
     *
     * @param document the document to insert
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the insert command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     */
    public void insertOne(TDocument document) {
        insertOne(document, new InsertOneOptions());
    }

    /**
     * 插入单条记录
     * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
     *
     * @param document the document to insert
     * @param options  the options to apply to the operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the insert command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoCommandException      if the write failed due to document validation reasons
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     * @since 3.2
     */
    public void insertOne(TDocument document, InsertOneOptions options) {
        long t1 = System.currentTimeMillis();
        getDocumentMongoCollection().insertOne(document, options);

        slowLog(
                System.currentTimeMillis() - t1,
                "insertOne(documents, options), document:{}, options:{}",
                document,
                options
        );
    }

    /**
     * 插入多条记录
     * Inserts one or more documents.  A call to this method is equivalent to a call to the {@code bulkWrite} method
     *
     * @param documents the documents to insert
     * @throws com.mongodb.MongoBulkWriteException if there's an exception in the bulk write operation
     * @throws com.mongodb.MongoException          if the write failed due some other failure
     * @see com.mongodb.client.MongoCollection#bulkWrite
     */
    public void insertMany(List<? extends TDocument> documents) {
        insertMany(documents, new InsertManyOptions());
    }

    /**
     * 插入多条记录
     * Inserts one or more documents.  A call to this method is equivalent to a call to the {@code bulkWrite} method
     *
     * @param documents the documents to insert
     * @param options   the options to apply to the operation
     * @throws com.mongodb.DuplicateKeyException if the write failed to a duplicate unique key
     * @throws com.mongodb.WriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException        if the write failed due some other failure
     */
    public void insertMany(List<? extends TDocument> documents, InsertManyOptions options) {
        long t1 = System.currentTimeMillis();
        getDocumentMongoCollection().insertMany(documents, options);

        slowLog(
                System.currentTimeMillis() - t1,
                "insertMany(documents, options), options:{}",
                options
        );
    }

    /**
     * 通过查询条件，删除第一条数据
     * Removes at most one document from the collection that matches the given filter.  If no documents match, the collection is not
     * modified.
     *
     * @param filter the query filter to apply the the delete operation
     * @return the result of the remove one operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the delete command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     */
    public DeleteResult deleteOne(Bson filter) {
        long t1 = System.currentTimeMillis();
        DeleteResult result = getDocumentMongoCollection().deleteOne(filter);

        slowLog(
                System.currentTimeMillis() - t1,
                "deleteOne(filter), filter:{}",
                filter
        );
        return result;
    }

    /**
     * 删除所有命中查询条件的记录
     * Removes all documents from the collection that match the given query filter.  If no documents match, the collection is not modified.
     *
     * @param filter the query filter to apply the the delete operation
     * @return the result of the remove many operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the delete command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     */
    public DeleteResult deleteMany(Bson filter) {
        long t1 = System.currentTimeMillis();
        DeleteResult result = getDocumentMongoCollection().deleteMany(filter);

        slowLog(
                System.currentTimeMillis() - t1,
                "deleteMany(filter), filter:{}",
                filter
        );
        return result;
    }

    /**
     * Replace a document in the collection according to the specified arguments.
     *
     * @param filter      the query filter to apply the the replace operation
     * @param replacement the replacement document
     * @return the result of the replace one operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the replace command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     * @mongodb.driver.manual tutorial/modify-documents/#replace-the-document Replace
     */
    public UpdateResult replaceOne(Bson filter, TDocument replacement) {
        return replaceOne(filter, replacement, new UpdateOptions());
    }

    /**
     * Replace a document in the collection according to the specified arguments.
     *
     * @param filter        the query filter to apply the the replace operation
     * @param replacement   the replacement document
     * @param updateOptions the options to apply to the replace operation
     * @return the result of the replace one operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the replace command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     * @mongodb.driver.manual tutorial/modify-documents/#replace-the-document Replace
     */
    public UpdateResult replaceOne(Bson filter, TDocument replacement, UpdateOptions updateOptions) {
        long t1 = System.currentTimeMillis();
        UpdateResult result = getDocumentMongoCollection().replaceOne(filter, replacement, updateOptions);

        slowLog(
                System.currentTimeMillis() - t1,
                "replaceOne(filter, replacement, options), filter:{}, replacement:{}, options:{}",
                filter,
                replacement,
                updateOptions
        );
        return result;
    }

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * @param filter a document describing the query filter, which may not be null.
     * @param update a document describing the update, which may not be null. The update to apply must include only update operators.
     * @return the result of the update one operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the update command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    public UpdateResult updateOne(Bson filter, Bson update) {
        return updateOne(filter, update, new UpdateOptions());
    }

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * @param filter        a document describing the query filter, which may not be null.
     * @param update        a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param updateOptions the options to apply to the update operation
     * @return the result of the update one operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the update command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    public UpdateResult updateOne(Bson filter, Bson update, UpdateOptions updateOptions) {
        long t1 = System.currentTimeMillis();
        UpdateResult result = getDocumentMongoCollection().updateOne(filter, update, updateOptions);

        slowLog(
                System.currentTimeMillis() - t1,
                "updateOne(filter, update, options), filter:{}, update:{}, options:{}",
                filter,
                update,
                updateOptions
        );
        return result;
    }

    /**
     * Update all documents in the collection according to the specified arguments.
     *
     * @param filter a document describing the query filter, which may not be null.
     * @param update a document describing the update, which may not be null. The update to apply must include only update operators.
     * @return the result of the update many operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the update command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    public UpdateResult updateMany(Bson filter, Bson update) {
        return updateMany(filter, update, new UpdateOptions());
    }

    /**
     * Update all documents in the collection according to the specified arguments.
     *
     * @param filter        a document describing the query filter, which may not be null.
     * @param update        a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param updateOptions the options to apply to the update operation
     * @return the result of the update many operation
     * @throws com.mongodb.MongoWriteException        if the write failed due some other failure specific to the update command
     * @throws com.mongodb.MongoWriteConcernException if the write failed due being unable to fulfil the write concern
     * @throws com.mongodb.MongoException             if the write failed due some other failure
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
        long t1 = System.currentTimeMillis();
        UpdateResult result = getDocumentMongoCollection().updateOne(filter, update, updateOptions);

        slowLog(
                System.currentTimeMillis() - t1,
                "updateMany(filter, update, options), filter:{}, update:{}, options:{}",
                filter,
                update,
                updateOptions
        );
        return result;
    }

    /**
     * Atomically find a document and remove it.
     *
     * @param filter the query filter to find the document with
     * @return the document that was removed.  If no documents matched the query filter, then null will be returned
     */
    public TDocument findOneAndDelete(Bson filter) {
        return findOneAndDelete(filter, new FindOneAndDeleteOptions());
    }

    /**
     * Atomically find a document and remove it.
     *
     * @param filter  the query filter to find the document with
     * @param options the options to apply to the operation
     * @return the document that was removed.  If no documents matched the query filter, then null will be returned
     */
    public TDocument findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
        long t1 = System.currentTimeMillis();
        TDocument result = (TDocument) getDocumentMongoCollection().findOneAndDelete(filter, options);

        slowLog(
                System.currentTimeMillis() - t1,
                "findOneAndDelete(filter, options), filter:{}, options:{}",
                filter,
                options
        );
        return result;
    }

    /**
     * Atomically find a document and replace it.
     *
     * @param filter      the query filter to apply the the replace operation
     * @param replacement the replacement document
     * @return the document that was replaced.  Depending on the value of the {@code returnOriginal} property, this will either be the
     * document as it was before the update or as it is after the update.  If no documents matched the query filter, then null will be
     * returned
     */
    public TDocument findOneAndReplace(Bson filter, TDocument replacement) {
        return findOneAndReplace(filter, replacement, new FindOneAndReplaceOptions());
    }

    /**
     * Atomically find a document and replace it.
     *
     * @param filter      the query filter to apply the the replace operation
     * @param replacement the replacement document
     * @param options     the options to apply to the operation
     * @return the document that was replaced.  Depending on the value of the {@code returnOriginal} property, this will either be the
     * document as it was before the update or as it is after the update.  If no documents matched the query filter, then null will be
     * returned
     */
    public TDocument findOneAndReplace(Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
        long t1 = System.currentTimeMillis();
        TDocument result = (TDocument) getDocumentMongoCollection().findOneAndReplace(filter, replacement, options);

        slowLog(
                System.currentTimeMillis() - t1,
                "findOneAndReplace(filter, replacement, options), filter:{}, replacement:{}, options:{}",
                filter,
                replacement,
                options
        );
        return result;
    }

    /**
     * Atomically find a document and update it.
     *
     * @param filter a document describing the query filter, which may not be null.
     * @param update a document describing the update, which may not be null. The update to apply must include only update operators.
     * @return the document that was updated before the update was applied.  If no documents matched the query filter, then null will be
     * returned
     */
    public TDocument findOneAndUpdate(Bson filter, Bson update) {
        return findOneAndUpdate(filter, update, new FindOneAndUpdateOptions());
    }

    /**
     * Atomically find a document and update it.
     *
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param options the options to apply to the operation
     * @return the document that was updated.  Depending on the value of the {@code returnOriginal} property, this will either be the
     * document as it was before the update or as it is after the update.  If no documents matched the query filter, then null will be
     * returned
     */
    public TDocument findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        long t1 = System.currentTimeMillis();
        TDocument result = (TDocument) getDocumentMongoCollection().findOneAndUpdate(filter, update, options);

        slowLog(
                System.currentTimeMillis() - t1,
                "findOneAndUpdate(filter, replacement, options), filter:{}, update:{}, options:{}",
                filter,
                update,
                options
        );
        return result;
    }

    protected void slowLog(long timeSpan, String message, Object... params) {
        if (timeSpan > SLOW_QUERY_TIME) {
            List<String> paramList = Lists.newArrayList(
                    getNamespace(),
                    String.valueOf(timeSpan)
            );
            if (params != null) {
                for (Object param : params) {
                    paramList.add(JSONObject.toJSONString(param));
                }
            }

            logger.info("[{}]慢查询:{}ms, " + message, paramList.toArray());
        }
    }

    private CodecRegistry getCodecRegistry(final Class registryClass) {
        ClassMate classMate = ReflectionUtils.getClassMate(registryClass);
        List<CodecRegistry> codecRegistryList = Lists.newArrayList();
        classMate.getReferClassList().forEach(clazz -> {
            CodecRegistry codecProvider = CodecRegistries.fromProviders(new CodecProvider() { // NOSONAR
                @Override
                public <D> Codec<D> get(Class<D> clazz, CodecRegistry registry) {
                    if (registryClass == clazz) {
                        return (Codec<D>) new YCodec(registry, clazz, YCollection.this::getNextSequence);
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
        return codecRegistry;
    }

    /**
     * 获取一个MongoDB的Collection连接
     *
     * @return
     */
    public MongoCollection getDocumentMongoCollection() {
        if (mongoCollection == null) {
            mongoCollection = getDocumentMongoCollection(getDatabaseName(), getCollectionName(), getDocumentClass());
        }
        return mongoCollection;
    }

    public <NewDocument> YCollection<NewDocument> getYCollection(final String collectionName, Class<NewDocument> clazz) {
        final String databaseName = getDatabaseName();
        final YMongoClient client = getYMongoClient();
        return new YCollection<NewDocument>() {
            @Override
            protected String getDatabaseName() {
                return databaseName;
            }

            @Override
            protected String getCollectionName() {
                return collectionName;
            }

            @Override
            public Class<NewDocument> getDocumentClass() {
                return clazz;
            }

            @Override
            protected YMongoClient getYMongoClient() {
                return client;
            }
        };
    }

    public <NewDocument> MongoCollection<NewDocument> getDocumentMongoCollection(String databaseName, String collectionName, Class<NewDocument> clazz) {
        MongoClient client = getYMongoClient().getClient(databaseName);
        MongoCollection collection = client.getDatabase(databaseName).getCollection(collectionName);
        if (clazz != null) {
            return collection.withDocumentClass(clazz).withCodecRegistry(getCodecRegistry(clazz));
        }
        return collection;
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
        MongoCollection collection = getDocumentMongoCollection(getDatabaseName(), "counters", null);
        Document result = (Document) collection.findOneAndUpdate(query, updateQuery);
        if (result == null) {
            //先插入
            query.put("seq", start + step);
            collection.insertOne(query);
            return start;
        }

        return result.getLong("seq");
    }
}
