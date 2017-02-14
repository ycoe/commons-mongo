package com.duoec.commons.core;

import com.duoec.commons.dao.HouseDao;
import com.duoec.commons.mongo.MongoConverter;
import com.duoec.commons.pojo.house.House;
import com.duoec.commons.test.BaseJunitTest;
import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by ycoe on 17/2/6.
 */
public class BaseEntityDaoTest extends BaseJunitTest {
    @Autowired
    private HouseDao houseDao;

    @Test
    public void getById() {
        House house = houseDao.getEntityById(423584.0);
        System.out.println(house.getBasicInfo().getHouseName());

        House house2 = houseDao.getEntityById(423584, Projections.include("basicInfo", "location"));
        System.out.println(house2.getBasicInfo().getHouseName());
    }

    @Test
    public void updateById() {

        houseDao.updateById(423584, Updates.set("location.cityId", 1337));

        Document set = new Document("location.cityId", 1337)
                .append("basicInfo.houseName", "测试楼盘");
        Document inc = new Document("viewCount", 1);
        Document updateDate = new Document("$set", set)
                .append("$inc", inc);
        houseDao.updateById(423584, updateDate);
    }


    @Test
    public void updateOne() throws Exception {
        House house = new House();
        house.setTags(Lists.newArrayList("推荐", "测试2"));
        house.setRecommendReason("测试RecommendReason");
        house.setRecommendReason(null);
        UpdateResult result = houseDao.updateOne(Filters.eq("_id", 423584), house);
        System.out.println(result.getModifiedCount());
    }

    @Test
    public void updateEntitySafe(){
        House house = new House();
        //house.setId(423584); //会根据这个字段组装一个filter: Filters.eq("_id", house.getId());
        house.setTags(Lists.newArrayList("推荐", "测试2"));
        //house.setRecommendReason(null); //如果把这个字段设置为null，它不会被更新，不会将线上的recommendReason字段删除或置空！
        //house.setRecommendReason(null); //如果把这个字段设置为空字符，则会将对应的字段设置为空字符！
        //如果com.duoec.commons.pojo.house.House#recommendReason添加了@Ignore注解，则无论怎样设置，都不会对此字段进行设置！
        UpdateResult result = houseDao.updateEntitySafe(house);
        System.out.println(result.getModifiedCount());
    }

    @Test
    public void updateEntity() throws Exception {
        House house = houseDao.getEntityById(10202);
        List<String> tags = Lists.newArrayList("推荐", "热门");
        house.setTags(tags);

        houseDao.updateEntity(house);
    }

    @Test
    public void getDocument() throws Exception {
House house = houseDao.getEntityById(10202);
Document doc = houseDao.getDocument(house, MongoConverter.OPTION_UPDATE);
System.out.println(doc.toJson());
    }

    @Test
    public void getEntityById() throws Exception {

    }

    @Test
    public void getEntityById1() throws Exception {

    }

    @Test
    public void findEntities() throws Exception {

    }

    @Test
    public void findEntities1() throws Exception {

    }

    @Test
    public void getEntity() throws Exception {

    }

    @Test
    public void getEntity1() throws Exception {

    }

    @Test
    public void findEntitiesWithTotal() throws Exception {

    }

    @Test
    public void findEntitiesWithTotal1() throws Exception {

    }
}
