package com.duoec.commons.mongo.core;

import com.alibaba.fastjson.JSONObject;
import com.duoec.commons.mongo.exceptions.YMongoException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.List;
import java.util.Map;

/**
 * @author 徐文振
 * @date 15/8/27
 */
public class YMongoClient {
    private static Map<String, MongoClient> CLIENTS = Maps.newConcurrentMap();

    private String mongoDBConnections;

    private int connectionsPerHost;

    private int maxWaitTime;

    /**
     * 获取某个数据库的连接
     *
     * @param database 数据库名称
     * @return
     */
    public MongoClient getClient(String database) {
        if (CLIENTS.containsKey(database)) {
            return CLIENTS.get(database);
        }

        if (Strings.isNullOrEmpty(mongoDBConnections)) {
            throw new YMongoException("Mongodb Configure Error, config is null!");
        }
        List<MongoConnectConf> connectConfigs = JSONObject.parseArray(mongoDBConnections, MongoConnectConf.class);

        if (connectConfigs == null || connectConfigs.isEmpty()) {
            throw new YMongoException("Mongodb Configure Error, can't parse Class MongoConnectConf!");
        }
        List<ServerAddress> serverAddresses = Lists.newArrayList();
        List<MongoCredential> credentials = Lists.newArrayList();
        for (MongoConnectConf config : connectConfigs) {
            serverAddresses.add(new ServerAddress(config.getHost(), config.getPort()));
            if (!Strings.isNullOrEmpty(config.getUser()) && !Strings.isNullOrEmpty(config.getPassword())) {
                credentials.add(MongoCredential.createScramSha1Credential(config.getUser(), database, config.getPassword().toCharArray()));
            }
        }
        MongoClientOptions clientOptions = new MongoClientOptions
                .Builder()
                .connectionsPerHost(connectionsPerHost)
                .maxWaitTime(maxWaitTime)
                .build();

        MongoClient client;
        if (credentials.isEmpty()) {
            client = new MongoClient(serverAddresses, clientOptions);
        } else {
            client = new MongoClient(serverAddresses, credentials, clientOptions);
        }
        CLIENTS.put(database, client);
        return client;
    }

    public String getMongoDBConnections() {
        return mongoDBConnections;
    }

    public void setMongoDBConnections(String mongoDBConnections) {
        this.mongoDBConnections = mongoDBConnections;
    }

    public int getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(int connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
}
