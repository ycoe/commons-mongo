package com.duoec.commons.test;

import com.alibaba.fastjson.JSONObject;
import com.duoec.commons.dao.HouseDao;
import com.duoec.commons.mongo.MongoConverter;
import com.duoec.commons.mongo.reflection.ClassUtils;
import com.duoec.commons.pojo.house.House;
import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 *
 * Created by ycoe on 17/1/23.
 */
public class MongoConverterTest extends BaseJunitTest {
    @Autowired
    private HouseDao houseDao;

    @Test
    public void configTest() {
        Assert.assertNotNull(houseDao);
        House house = houseDao.find().limit(1).first();
        Document document = MongoConverter.toDocument(house);
        document.put("id", document.getLong("_id"));
        document.remove("_id");
        JSONObject json = (JSONObject) JSONObject.toJSON(house);

        assertEquals(document, json);
    }



}
