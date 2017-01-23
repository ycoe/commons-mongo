package com.duoec.commons.test;

import com.duoec.commons.dao.HouseDao;
import com.duoec.commons.pojo.house.House;
import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by ycoe on 17/1/23.
 */
public class YCollectionCountTest extends BaseJunitTest {
    @Autowired
    private HouseDao houseDao;

    @Test
    public void configTest() {
        Assert.assertNotNull(houseDao);
        House house = houseDao.find().limit(1).first();
        System.out.println(house.getBasicInfo().getHouseName());
    }

    @Test
    public void encode() {
        List<Long> houseIds = Lists.newArrayList();
        houseDao.find(new Document())
                .skip(new Random().nextInt(10000))
                .limit(100)
                .projection(Projections.include("_id"))
                .forEach((Consumer<? super House>) house -> houseIds.add(house.getId()));

        House house = houseDao.find(Filters.eq("_id", 11077)).first();
        System.out.println(house.getBasicInfo().getHouseName());

        final long[] timer = {0, 0, 100};
        houseIds.forEach(houseId -> {
            long t1 = System.currentTimeMillis();
            House h = houseDao.find(Filters.eq("_id", houseId)).limit(1).first();
            long t2 = System.currentTimeMillis();
            long l = t2 - t1;
            timer[0] += l;
            if (timer[1] < l) {
                timer[1] = l;
            }
            if (timer[2] > l) {
                timer[2] = l;
            }
        });
        System.out.println("平均耗时:" + (timer[0] / houseIds.size()) + "ms,最大耗时:" + timer[1] + ",最小耗时:" + timer[2] + "ms. total:" + houseIds.size());
        System.out.println("平均耗时:3ms,最大耗时:28,最小耗时：1ms.（优化前最好成绩）");


//        batchQueryTest(dao);
    }
}
