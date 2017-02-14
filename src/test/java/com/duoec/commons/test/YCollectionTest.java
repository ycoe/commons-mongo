package com.duoec.commons.test;

import com.duoec.commons.dao.CmsProcessDao;
import com.duoec.commons.dao.HouseDao;
import com.duoec.commons.pojo.Counter;
import com.duoec.commons.pojo.house.House;
import com.duoec.commons.pojo.house.HouseBasicInfo;
import com.duoec.commons.pojo.house.Location;
import com.duoec.commons.pojo.house.Property;
import com.duoec.commons.pojo.process.CmsProcess;
import com.duoec.commons.pojo.process.PageProcess;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by ycoe on 17/1/23.
 */
public class YCollectionTest extends BaseJunitTest {
    @Autowired
    private HouseDao houseDao;

    @Autowired
    private CmsProcessDao cmsProcessDao;

    @Test
    public void configTest() {
        Assert.assertNotNull(houseDao);
        House house = houseDao.find().limit(1).first();
        System.out.println(house.getBasicInfo().getHouseName());
    }

    @Test
    public void exists() {
        boolean exists = houseDao.exists(Filters.eq("_id", 12345));
    }

    @Test
    public void count() {
        long count = houseDao.count();

        long count2 = houseDao.count(
                Filters.eq("location.cityId", 1337) //筛选条件
        );
        System.out.println(count2);


        CountOptions opts = new CountOptions();
        opts.skip(10); //命中结果先跳过10条记录
        opts.limit(100); //命中结果限制100条
        long count3 = houseDao.count(
                Filters.eq("location.cityId", 1337), //筛选条件
                opts
        );
        System.out.println(count3);
    }

    @Test
    public void distinct() {
//houseDao.distinct(
//        "type",  //需要获取的字段名称，嵌套使用点号(.)分隔
//        Integer.class //字段类型，必须所有此字符都是此类型，不然会报错，比如如果有个字符是double型，这里就会报错！
//).into(Lists.newArrayList());

        houseDao
                .distinct(
                        "location.cityName", //需要获取的字段名称，嵌套使用点号(.)分隔
                        Filters.gt("location.cityId", 1000), //筛选条件
                        String.class  //字段类型
                )
                .forEach( //遍历所有结果
                        (Consumer<? super String>) name -> System.out.println(name)
                )
        ;
    }

    @Test
    public void find() {
        //获取文档第1条记录
        House house = houseDao
                .find()
                .first();

        List<House> houseList = houseDao
                .find(Filters.eq("location.cityId", 1337)) //查询条件
                .projection(Projections.include("basicInfo", "location.cityId")) //指定查询哪些字段
                .skip(5) //跳过5条记录
                .limit(10) //查询10条记录
                .into(Lists.newArrayList()) //将结果塞进List内返回
                ;

        Integer pageNo = null;
        int pageSize = 20;
        List<String> houseNames = Lists.newArrayList();
        Document filter = new Document("location.cityId", 1337)
                .append("flags", "hot");
        FindIterable<House> iterable = houseDao.find(filter)
                .projection(Projections.exclude("otherInfo")) // 不包含某些字段
                .sort(Sorts.descending("updateTime")) //按updateTime倒序
                ;
        if (pageNo != null && pageNo > 0) {
            iterable.skip((pageNo - 1) * pageSize)
                    .limit(pageSize);
        }
        iterable.forEach(
                (Consumer<House>) h -> houseNames.add(h.getBasicInfo().getHouseName())
        );
    }

    @Test
    public void aggregate() {
        int count = 5;
        List<House> houseList = Lists.newArrayList();
        houseDao.aggregate(Lists.newArrayList(
                new Document("$match", new Document("location.cityId", 1337)), //筛选条件
                new Document("$sample", new Document("size", count)) //随机获取count个记录
        ))
                .forEach((Consumer<? super House>) houseList::add) //加入列表
//.into(houseList) //上面一行也可以写成这样
        ;

        Map<String, Integer> cityCounts = Maps.newHashMap();
        Document match = new Document("type", 1);
        Document project = new Document("name", "$location.cityName")
                .append("count", new Document("$sum", 1));
        houseDao
                .getDocumentMongoCollection(Document.class)
                .aggregate(
                        Lists.newArrayList(
                                new Document("$match", match),
                                new Document("$project", project)
                        )
                ).forEach(
                (Consumer<? super Document>) doc -> {
                    cityCounts.put(doc.getString("name"), doc.getInteger(count));
                }
        );


        List<Counter> counters = Lists.newArrayList();
        houseDao
                .getDocumentMongoCollection(Counter.class)//必须先声明（获取Counter的YCollection实例）
                .aggregate(
                        Lists.newArrayList(
                                new Document("$match", match),
                                new Document("$project", project)
                        )
                )
                .into(counters);


        houseDao
                .aggregate(
                        Lists.newArrayList(
                                new Document("$match", match),
                                new Document("$project", project)
                        ),
                        Counter.class
                )
                .into(counters);

    }

    @Test
    public void bulkWrite() {
        House house = new House();
        HouseBasicInfo info = new HouseBasicInfo();
        info.setHouseName("测试楼盘");
        house.setBasicInfo(info);
        Location location = new Location();
        location.setCityId(1337);
        house.setLocation(location);
        // ... house的各属性set ...
        InsertOneModel<House> insertOne = new InsertOneModel(house);


        UpdateManyModel<House> updateMany = new UpdateManyModel(Filters.eq("location.cityId", 1337), Updates.inc("viewCount", 1));
        houseDao.bulkWrite(Lists.newArrayList(
                insertOne,
                updateMany
        ));
    }

    @Test
    public void insertOne() {
        CmsProcess process = new CmsProcess();
        process.setCode("tt");
        process.setCreateTime(System.currentTimeMillis());
        PageProcess page = new PageProcess();
        page.setPage(1);
        page.setStatus(0);
        process.setPages(Lists.newArrayList(page));

        cmsProcessDao.insertOne(process);

        process.setId(null);
        cmsProcessDao.insertOne(process);

//        House house = new House();
//        HouseBasicInfo info = new HouseBasicInfo();
//        info.setHouseName("测试楼盘");
//        house.setBasicInfo(info);
//        Location location = new Location();
//        location.setCityId(1337);
//        house.setLocation(location);
//
//        Property p = new Property();
//        p.setPropertyName("1");
//        p.setPropertyRights(2);
//        p.setPropertyFee(5.0);
//        house.setProperties(Lists.newArrayList(p));
//        // ... house的各属性set ...
//        houseDao.insertOne(house);

//        Document doc = new Document("basicInfo", new Document("houseName", "测试楼盘")) //注意，如果未指定_id，插入时会直接使用ObjectId()!
//                //.append("location.cityId", 1337)
//                .append("location", new Document("cityId", 1337)); //注意，上一行的写法会报错，key值不能带.
//        houseDao
//                .getDocumentMongoCollection(Document.class)
//                .insertOne(doc);
    }

    @Test
    public void insertMany() {
        House house = new House();
        HouseBasicInfo info = new HouseBasicInfo();
        info.setHouseName("测试楼盘");
        house.setBasicInfo(info);
        Location location = new Location();
        location.setCityId(1337);
        house.setLocation(location);
        // ... house的各属性set ...
        houseDao.insertMany(Lists.newArrayList(
                //可以包含更多的house...
                house
        ));

        Document doc = new Document("basicInfo", new Document("houseName", "测试楼盘")) //注意，如果未指定_id，插入时会直接使用ObjectId()!
                //.append("location.cityId", 1337)
                .append("location", new Document("cityId", 1337)); //注意，上一行的写法会报错，key值不能带.
        houseDao
                .getDocumentMongoCollection(Document.class)
                .insertMany(Lists.newArrayList(
                        doc
                        //可以包含更多的doc...
                ));
    }

    @Test
    public void deleteOne() {
        houseDao.deleteOne(Filters.eq("_id", 123456));

        //注意，本筛选条件会命中多条记录，但只会删除第一条命中记录！
        Document filter = new Document("location.cityId", 1337)
                .append("score", new Document("$lt", 10));
        DeleteResult result = houseDao.deleteOne(filter);
        System.out.println(result.getDeletedCount()); // 打印出：1
    }

    @Test
    public void deleteMany() {
        houseDao.deleteMany(Filters.eq("_id", 123456));

        //注意，本筛选条件会命中多条记录，删除所有命中记录！
        Document filter = new Document("location.cityId", 1337)
                .append("score", new Document("$lt", 10));
        DeleteResult result = houseDao.deleteMany(filter);
        System.out.println(result.getDeletedCount());
    }

    @Test
    public void replaceOne() {
        House house = new House();
        house.setId(423584); //注意，replaceOne不能更改_id值，否则会出错，未设置时会自增，也会报错！
        HouseBasicInfo info = new HouseBasicInfo();
        info.setHouseName("测试楼盘");
        house.setBasicInfo(info);
        Location location = new Location();
        location.setCityId(1337);
        house.setLocation(location);
        house.setTags(Lists.newArrayList("测试", "推荐"));
        // ... house的各属性set ...
        UpdateResult result = houseDao.replaceOne(Filters.eq("basicInfo.houseName", "测试楼盘"), house);
        //在筛选阶段，就会只查出命中的第一条记录，所以这里打印出来的更新数量是1，但实际查询条件命中的不只一条记录！
        System.out.println("更新数量：" + result.getMatchedCount() + ", 成功数量：" + result.getModifiedCount());
    }

    @Test
    public void updateOne() {
        UpdateResult result = houseDao.updateOne(Filters.eq("_id", 423584), Updates.set("tags", Lists.newArrayList("测试", "热门")));

        Document set = new Document("tags", Lists.newArrayList("测试", "热门"))
                .append("updateTime", System.currentTimeMillis());
        Document unset = new Document("otherInfo.aliasName", true);
        Document inc = new Document("viewCount", 1);
        Document updateDoc = new Document("$set", set) //$set: 设置值
                .append("$unset", unset)         //$unset: 删除字段
                .append("$inc", inc);           //$inc: 累加
        UpdateResult result2 = houseDao.updateOne(
                Filters.eq("_id", 423584), //筛选条件，仅会更新命中的第一条记录！
                updateDoc
        );
    }

    @Test
    public void updateMany() {
        UpdateResult result = houseDao.updateMany(new Document("basicInfo.houseName", "测试楼盘"),
                Updates.set("tt", "TEST"));

//        Document set = new Document("tags", Lists.newArrayList("测试", "热门"))
//                .append("updateTime", System.currentTimeMillis());
//        Document unset = new Document("otherInfo.aliasName", true);
//        Document inc = new Document("viewCount", 1);
//        Document updateDoc = new Document("$set", set) //$set: 设置值
//                .append("$unset", unset)         //$unset: 删除字段
//                .append("$inc", inc);           //$inc: 累加
//        UpdateResult result2 = houseDao.updateMany(
//                Filters.eq("_id", 423584), //筛选条件，仅会更新命中的第一条记录！
//                updateDoc
//        );
    }

    @Test
    public void findOneAndDelete() {
        FindOneAndDeleteOptions options = new FindOneAndDeleteOptions();
        options.sort(Sorts.ascending("updateTime")); //指定排序，按更新时间顺序
        options.projection(Projections.include("location", "basicInfo")); //指定删除后，返回指定的字段
        House house = houseDao.findOneAndDelete(Filters.eq("location.cityId", 1337), options);

        //注意，本筛选条件会命中多条记录，但只会删除第一条命中记录！
        Document filter = new Document("location.cityId", 1337)
                .append("score", new Document("$lt", 10));
        DeleteResult result = houseDao.deleteOne(filter);
        System.out.println(result.getDeletedCount()); // 打印出：1
    }

    @Test
    public void findOneAndReplace(){
        House house = new House();
        house.setId(423584); //注意，replaceOne不能更改_id值，否则会出错，未设置时会自增，也会报错！
        HouseBasicInfo info = new HouseBasicInfo();
        info.setHouseName("测试楼盘");
        house.setBasicInfo(info);
        Location location = new Location();
        location.setCityId(1337);
        house.setLocation(location);
        house.setTags(Lists.newArrayList("测试", "推荐"));
        // ... house的各属性set ...
        House house2 = houseDao.findOneAndReplace(Filters.eq("basicInfo.houseName", "测试楼盘"), house);
        //在筛选阶段，就会只查出命中的第一条记录，所以这里打印出来的更新数量是1，但实际查询条件命中的不只一条记录！
    }

    @Test
    public void findOneAndUpdate(){
        Document set = new Document("location.cityId", 1337);
        Document updateData = new Document("$set", set)
                .append("$inc", new Document("viewCount", 1));
        House house2 = houseDao.findOneAndUpdate(Filters.eq("basicInfo.houseName", "测试楼盘"), updateData);
        //在筛选阶段，就会只查出命中的第一条记录，所以这里打印出来的更新数量是1，但实际查询条件命中的不只一条记录！
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
