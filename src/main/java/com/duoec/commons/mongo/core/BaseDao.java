package com.duoec.commons.mongo.core;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by ycoe on 16/5/2.
 */
public abstract class BaseDao {
    private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

    protected static final long SLOW_QUERY_TIME = 800;

    protected abstract String getDatabaseName();

    protected abstract String getCollectionName();

    /**
     * 插入记录多条记录
     *
     * @param docs
     */
    public void insertMany(List<Document> docs) {
        MongoCollection<Document> collection = getDocumentMongoCollection();
        long t1 = System.currentTimeMillis();
        collection.insertMany(docs);
        long t2 = System.currentTimeMillis();
        if (t2 - t1 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:批量插入记录,t:{},size:{}", getDatabaseName(), getCollectionName(), t2 - t1, docs.size());
        }
    }

    /**
     * 插入一条记录
     *
     * @param doc
     */
    public void insertOne(Document doc) {
        MongoCollection<Document> collection = getDocumentMongoCollection();
        long t1 = System.currentTimeMillis();
        collection.insertOne(doc);
        long t2 = System.currentTimeMillis();
        if (t2 - t1 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:插入记录,t:{},doc:{}", getDatabaseName(), getCollectionName(), t2 - t1, doc);
        }
    }

    /**
     * 删除-条记录
     *
     * @param query
     */
    public DeleteResult deleteOne(Bson query) {
        MongoCollection<Document> collection = getDocumentMongoCollection();
        long t1 = System.currentTimeMillis();
        DeleteResult result = collection.deleteOne(query);
        long t2 = System.currentTimeMillis();
        if (t2 - t1 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:删除记录,t:{},query:{}", getDatabaseName(), getCollectionName(), t2 - t1, query);
        }
        return result;
    }

    public void deleteMany(Bson query) {
        long t1 = System.currentTimeMillis();
        MongoCollection<Document> collection = getDocumentMongoCollection();
        long t2 = System.currentTimeMillis();
        DeleteResult result = collection.deleteMany(query);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:批量删除记录,t1:{},t2:{}:query{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, query);
        }
    }

    public void deleteById(Object id) {
        long t1 = System.currentTimeMillis();
        BasicDBObject query = new BasicDBObject("_id", id);
        long t2 = System.currentTimeMillis();
        MongoCollection<Document> collection = getDocumentMongoCollection();
        long t3 = System.currentTimeMillis();
        DeleteResult result = collection.deleteOne(query);
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:通过ID删除记录,t1:{},t2:{},id:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, id);
        }
    }

    /**
     * 通过_id获取doc
     *
     * @param id
     * @return
     */
    public Document getById(Object id) {
        long t1 = System.currentTimeMillis();
        MongoCollection<Document> collection = getDocumentMongoCollection();
        long t2 = System.currentTimeMillis();
        if (collection == null)
            return null;
        FindIterable<Document> result = collection.find(new BasicDBObject("_id", id)).limit(1).skip(0);
        long t3 = System.currentTimeMillis();
        MongoCursor<Document> it = result.iterator();
        Document item = null;
        if (it.hasNext()) {
            item = it.next();
        }
        it.close();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:通过ID获取记录,t1:{},t2:{}, id:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, id);
        }
        return item;
    }

    /**
     * 查询
     *
     * @param query
     * @return
     */
    public FindIterable find(Bson query, Bson sort, Integer skip, Integer limit) {
        MongoCollection collection = getDocumentMongoCollection();
        FindIterable it = collection.find(query);
        if (sort != null) {
            it.sort(sort);
        }
        if (skip != null) {
            it.skip(skip);
        }
        if (limit != null) {
            it.limit(limit);
        }
        return it;
    }

    public FindIterable find(Bson sort, Integer skip, Integer limit) {
        MongoCollection collection = getDocumentMongoCollection();
        FindIterable it = collection.find();
        if (sort != null) {
            it.sort(sort);
        }
        if (skip != null) {
            it.skip(skip);
        }
        if (limit != null) {
            it.limit(limit);
        }
        return it;
    }

    public void updateMany(Bson query, Document updateData) {
        long t1 = System.currentTimeMillis();
        MongoCollection collection = getDocumentMongoCollection();
        long t2 = System.currentTimeMillis();
        Document data = formatUpdateData(updateData);
        collection.updateMany(query, data);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:updateMany,t1:{},t2:{},query:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, query);
        }
    }

    private Document formatUpdateData(Document updateData) {
        Document delDoc = getUpdateDocument(updateData, "$unset");
        Document setDoc = getUpdateDocument(updateData, "$set");
        Set<String> keys = updateData.keySet();
        Document data = new Document();
        for (String key : keys) {
            Object v = updateData.get(key);
            if (!key.startsWith("$")) {
                if (v == null) {
                    delDoc.put(key, "");
                } else {
                    setDoc.put(key, v);
                }
            } else {
                data.put(key, v);
            }
        }
        if (!setDoc.isEmpty()) {
            data.put("$set", setDoc);
        }
        if (!delDoc.isEmpty()) {
            data.put("$unset", delDoc);
        }
        return data;
    }

    /**
     * 更新
     *
     * @param query
     * @param updateData
     */
    public void updateOne(Bson query, Document updateData) {
        long t1 = System.currentTimeMillis();
        MongoCollection collection = getDocumentMongoCollection();
        long t2 = System.currentTimeMillis();
        Document data = formatUpdateData(updateData);
        collection.updateOne(query, data);
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:updateOne,t1:{},t2:{},query:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, query);
        }
    }

    public AggregateIterable aggregate(List<? extends Bson> pipeline) {
        return getDocumentMongoCollection().aggregate(pipeline);
    }

    public <D> AggregateIterable aggregate(List<? extends Bson> pipeline, Class<D> returnClass) {
        return getDocumentMongoCollection().aggregate(pipeline, returnClass);
    }

    private Document getUpdateDocument(Document updateData, String opt) {
        Document doc;
        if (updateData.containsKey(opt)) {
            doc = (Document) updateData.get(opt);
        } else {
            doc = new Document();
        }
        return doc;
    }

    public void updateById(Object id, Document updateData) {
        Document query = new Document("_id", id);
        updateOne(query, updateData);
    }

    /**
     * 查找并更新
     *
     * @param query
     * @param update
     */
    public Document findOneAndUpdate(Bson query, Bson update) {
        MongoCollection collection = getDocumentMongoCollection();
        return (Document) collection.findOneAndUpdate(query, update);
    }

    /**
     * 判断某个条件的记录是否存在
     *
     * @param query
     * @return
     */
    public boolean exists(Bson query) {
        long t1 = System.currentTimeMillis();
        MongoCollection collection = getDocumentMongoCollection();
        long t2 = System.currentTimeMillis();
        MongoCursor it = collection.find(query).skip(0).limit(1).iterator();
        long t3 = System.currentTimeMillis();
        if (t3 - t2 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:判断是否存在,t1:{},t2:{},query:{}", getDatabaseName(), getCollectionName(), t2 - t1, t3 - t2, query);
        }
        boolean exists = it.hasNext();
        it.close();
        return exists;
    }

    /**
     * 统计
     *
     * @param query
     * @return
     */
    public long count(Bson query) {
        MongoCollection collection = getDocumentMongoCollection();
        long t1 = System.currentTimeMillis();
        long count = collection.count(query);
        long t2 = System.currentTimeMillis();
        if (t2 - t1 > SLOW_QUERY_TIME) {
            logger.info("[{}.{}]慢查询:count(),t1:{},query:{}", getDatabaseName(), getCollectionName(), t2 - t1, JSONObject.toJSONString(query));
        }
        return count;
    }

    /**
     * 获取某个集合(使用Document解析)
     *
     * @return
     */
    public MongoCollection getDocumentMongoCollection() {
        String databaseName = getDatabaseName();
        MongoClient client = getMongodbClient().getClient(databaseName);
        return client.getDatabase(databaseName).getCollection(getCollectionName());
    }

    public MongoCollection getDocumentMongoCollection(String collectionName) {
        String databaseName = getDatabaseName();
        MongoClient client = getMongodbClient().getClient(databaseName);
        return client.getDatabase(databaseName).getCollection(collectionName);
    }

    public abstract YMongoClient getMongodbClient();
}
