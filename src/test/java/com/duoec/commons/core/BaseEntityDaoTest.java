package com.duoec.commons.core;

import com.duoec.commons.dao.HouseDao;
import com.duoec.commons.mongo.MongoConverter;
import com.duoec.commons.pojo.house.House;
import com.duoec.commons.test.BaseJunitTest;
import com.google.common.collect.Lists;
import org.bson.Document;
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
    public void updateOne() throws Exception {

    }

    @Test
    public void update() throws Exception {
        House house = houseDao.getEntityById(10202);
        List<String> tags = Lists.newArrayList("推荐", "热门");
        house.setTags(tags);

        houseDao.update(house);
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
