package com.duoec.commons.test;

import com.duoec.commons.dao.HouseDao;
import com.duoec.commons.mongo.MongoConverter;
import com.duoec.commons.pojo.house.House;
import com.duoec.commons.pojo.house.HouseBasicInfo;
import com.duoec.commons.pojo.house.HouseOtherInfo;
import com.mongodb.client.model.Filters;
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
    public void toEntity() throws Exception {
        House house = houseDao.find(Filters.eq("_id", 14607)).first();
        Document doc = MongoConverter.toDocument(house);

        House house2 = MongoConverter.toEntity(doc, House.class);
        assertEquals(house, house2);
        System.out.println(house.getBasicInfo().getHouseName());
    }

    /**
     * Document toDocument(T entity)
     * @throws Exception
     */
    @Test
    public void toDocument() throws Exception {
        //测试double型，会被转换成int型
        House house = getHouseDemo();
        Document doc = MongoConverter.toDocument(house);
        Document otherInfoDoc = (Document) doc.get("otherInfo");
        System.out.println(doc.toJson());
        Assert.assertEquals(house.getOtherInfo().getGrossBuildingArea(), otherInfoDoc.get("grossBuildingArea"));
    }

    /**
     * Document toDocument(T entity, int options)
     * @throws Exception
     */
    @Test
    public void toDocument1() throws Exception {
        //测试double型，会被转换成int型
        House house = getHouseDemo();
        Document doc = MongoConverter.toDocument(house, MongoConverter.OPTION_INSERT);
        Document otherInfoDoc = (Document) doc.get("otherInfo");
        System.out.println(doc.toJson());
        Assert.assertEquals(house.getOtherInfo().getGrossBuildingArea(), otherInfoDoc.get("grossBuildingArea"));
    }

    @Test
    public void getUpdateDocument() throws Exception {
        House house = houseDao.getEntityById(14607);
        Document updateDoc = MongoConverter.getUpdateDocument(house);
        System.out.println(updateDoc.toJson());
    }

    @Test
    public void getEntityProjections() throws Exception {

    }

    @Test
    public void toDocumentTest() {

    }

    @Test
    public void toDocumentTest2() {
        House house = houseDao.getEntityById(10201);
        Document doc = MongoConverter.toDocument(house, MongoConverter.OPTION_INSERT);
        System.out.println(doc.toJson());
    }

    private House getHouseDemo(){
        House house = new House();
        HouseOtherInfo other = new HouseOtherInfo();
        other.setGrossBuildingArea(3.0d);
        house.setOtherInfo(other);
        return house;
    }
}
