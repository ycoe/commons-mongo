官方文档：https://docs.mongodb.com/manual/reference/method/db.collection.deleteOne/



## 一、方法

### DeleteResult deleteOne(Bson filter)

删除一条记录

注意，_如果查询条件命中多条记录，也只会删除命中的第一条！_

如果需要删除命中的所有记录，请用[deleteMany](/chapter1/deletemany.md)


## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 需要删除的查询条件 |


## 三、DEMO

简单条件的删除

``` java
DeleteResult result = houseDao.deleteOne(Filters.eq("_id", 123456));
```

更复杂的条件删除

``` java
//注意，本筛选条件会命中多条记录，但只会删除第一条命中记录！
Document filter = new Document("location.cityId", 1337) //嵌套文档查询可以直接使用.
        .append("score", new Document("$lt", 10));
DeleteResult result = houseDao.deleteOne(filter);
System.out.println(result.getDeletedCount()); // 打印出：1
```




