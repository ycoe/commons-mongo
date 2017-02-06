package com.duoec.commons.mongo.core;

import com.duoec.commons.mongo.MongoConverter;
import com.duoec.commons.mongo.Pagination;
import com.duoec.commons.mongo.reflection.ReflectionUtils;
import com.duoec.commons.mongo.reflection.dto.AutoIncrementInfo;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import com.duoec.commons.mongo.reflection.dto.FieldMate;
import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

/**
 * Created by ycoe on 16/5/2.
 */
public abstract class BaseEntityDao<T> extends YCollection<T> {

    public static final String ID = "_id";

    /**
     * 通过查询条件更新一条记录，更新仅对设置有值的字段进行修改，不$unset为空的字段
     * 如果需要$unset为空字段，请使用update(T entity)方法
     *
     * @param query
     * @param entity
     */
    public void updateOne(Bson query, T entity) {
        Document doc = getDocument(entity, MongoConverter.OPTION_UPDATE);
        super.updateOne(query, new Document("$set", doc));
    }

    /**
     * 本方法仅会更新实体内定义且未被标识为@Ignore(update=true)的字段
     * 如果属性值为空，则会被删除
     * 嵌套document/list会被直接覆盖
     * 如果不需要清空，请使用updateOne()方法！
     *
     * @param entity
     */
    public void update(T entity) {
        Class clazz = entity.getClass();
        ClassMate classMate = ReflectionUtils.getClassMate(clazz);
        FieldMate fieldMate = classMate.getFieldMate("id");
        Object id = ReflectionUtils.getField(fieldMate, entity);
        Document updateData = MongoConverter.getUpdateDocument(entity);
        updateOne(Filters.eq(ID, id), updateData);
    }

    public Document getDocument(T entity, int option) {
        Document doc = MongoConverter.toDocument(entity, option);

        if ((option & MongoConverter.OPTION_INSERT) == MongoConverter.OPTION_INSERT) {
            //如果是insert
            Class clazz = entity.getClass();
            ClassMate classMate = ReflectionUtils.getClassMate(clazz);
            AutoIncrementInfo autoIncrementInfo = classMate.getAutoIncrementInfo();
            if (autoIncrementInfo != null && autoIncrementInfo.isAutoIncrement()) {
                Object entityId = doc.get(ID);
                if (entityId == null || "0".equals(entityId.toString())) {
                    //参见文档:https://docs.mongodb.org/manual/tutorial/create-an-auto-incrementing-field/
                    Object id = getNextSequence(autoIncrementInfo);
                    doc.put(ID, id);
                    FieldMate fieldMate = classMate.getFieldMate("id");
                    ReflectionUtils.setField(fieldMate, entity, id); //写入entity
                }
            }
        } else if ((option & MongoConverter.OPTION_UPDATE) == MongoConverter.OPTION_UPDATE) {
            //如果是update
            doc.remove(ID);
        }
        return doc;
    }

    public T getEntityById(Object id) {
        return getEntityById(id, null);
    }

    public T getEntityById(Object id, Bson projection) {
        return getEntity(Filters.eq(ID, id), projection);
    }

    public List<T> findEntities(Bson query, Bson sort, Integer skip, Integer limit) {
        return findEntities(query, sort, skip, limit, null);
    }

    public List<T> findEntities(Bson query, Bson sort, Integer skip, Integer limit, Bson projection) {
        if (query == null) {
            query = new Document();
        }
        FindIterable<T> iterable = find(query);
        if (sort != null) {
            iterable = iterable.sort(sort);
        }
        if (skip != null) {
            iterable = iterable.skip(skip);
        }
        if (limit != null) {
            iterable = iterable.limit(limit);
        }
        if (projection != null) {
            iterable = iterable.projection(projection);
        }
        return Lists.newArrayList(iterable);
    }

    public T getEntity(Bson query, Bson projection) {
        return find(query)
                .projection(projection)
                .first();
    }

    public T getEntity(Bson query) {
        return getEntity(query, null);
    }

    public Pagination<T> findEntitiesWithTotal(Bson query, Bson sort, Integer skip, Integer limit) {
        return findEntitiesWithTotal(query, sort, skip, limit, null);
    }

    public Pagination<T> findEntitiesWithTotal(Bson query, Bson sort, Integer skip, Integer limit, Bson projections) {
        Pagination<T> pagination = new Pagination();
        List<T> entities = findEntities(query, sort, skip, limit, projections);
        pagination.setList(entities);
        int size = entities.size();
        long total = 0;
        if (size == 0) {
            if (skip > 0) {
                //不好说，还是查询一下总数
                total = count(query);
            }
        } else {
            if (size < limit) {
                total = size + skip;
            } else {
                total = count(query);
            }
        }

        pagination.setTotal(total);
        return pagination;
    }
}
