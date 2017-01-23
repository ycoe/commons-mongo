package com.duoec.commons.dao;

import com.duoec.commons.mongo.core.YCollection;
import com.duoec.commons.mongo.core.YMongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by ycoe on 17/1/23.
 */
public abstract class BaseEntityDao<T> extends YCollection<T>{
    @Autowired
    private YMongoClient yMongoClient;

    @Value("${mongodb.database.waterfall}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected YMongoClient getYMongoClient(){
        return yMongoClient;
    }
}
