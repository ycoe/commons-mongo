官方文档：https://docs.mongodb.com/manual/reference/method/db.collection.deleteMany/

## 一、方法

### DeleteResult deleteMany(Bson filter)

删除当前collection所有命中条件的记录


## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |


## 三、DEMO

简单删除

``` java
houseDao.deleteMany(Filters.eq("_id", 123456));
```

复杂查询条件的批量删除

``` java
//注意，本筛选条件会命中多条记录，删除所有命中记录！
Document filter = new Document("location.cityId", 1337)
        .append("score", new Document("$lt", 10));
DeleteResult result = houseDao.deleteMany(filter);
System.out.println(result.getDeletedCount()); //打印出删除的记录数
```



