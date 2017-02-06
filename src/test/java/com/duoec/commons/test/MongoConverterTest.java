package com.duoec.commons.test;

import com.duoec.commons.dao.HouseDao;
import com.duoec.commons.mongo.MongoConverter;
import com.duoec.commons.pojo.house.House;
import com.duoec.commons.pojo.house.HouseBasicInfo;
import com.duoec.commons.pojo.house.HouseOtherInfo;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ycoe on 17/1/23.
 */
public class MongoConverterTest extends BaseJunitTest{
    @Autowired
    private HouseDao houseDao;

    @Test
    public void toDocumentTest() {
        //测试double型，会被转换成int型
        House house = new House();
        HouseOtherInfo other = new HouseOtherInfo();
        other.setGrossBuildingArea(3.0d);
        house.setOtherInfo(other);
        Document doc = MongoConverter.toDocument(house, MongoConverter.OPTION_INSERT);
        Document otherInfoDoc = (Document) doc.get("otherInfo");
        System.out.println(doc.toJson());
        Assert.assertEquals(other.getGrossBuildingArea(), otherInfoDoc.get("grossBuildingArea"));
    }

    @Test
    public void toDocumentTest2() {
        House house = houseDao.getEntityById(10201);
        Document doc = MongoConverter.toDocument(house, MongoConverter.OPTION_INSERT);
        System.out.println(doc.toJson());
    }
}
