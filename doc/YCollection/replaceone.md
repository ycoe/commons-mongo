replace与update最大的不同是，它是整个文档覆盖！

update方法只会对$set / $unset / $inc 中定义的字段进行修改，不会影响到其它未声明的字段！

replaceOne方法的 filter 如果命中多个，只会替换第一个命中的记录！

replace不能更改_id值！

慎用此方法，避免将其它字段删除了！

官方文档：https://docs.mongodb.com/manual/reference/method/db.collection.replaceOne/

## 一、方法

### UpdateResult replaceOne(Bson filter, TDocument replacement)

替换一个文档


### UpdateResult replaceOne(Bson filter, TDocument replacement, UpdateOptions updateOptions)

替换一个文档



## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |
| replacement | < TDocument > | 需要替换的文档，使用dao操作时，是doc指定的类实例！ |
| updateOptions | UpdateOptions | 替换操作的配置 |


## 三、DEMO

``` java
        House house = new House();
        house.setId(423584); //注意，replaceOne不能更改_id值，否则会出错，未设置时会自增，也会报错！
        // ... house的各属性set ...
        UpdateResult result = houseDao.replaceOne(Filters.eq("basicInfo.houseName", "测试楼盘"), house);
        //在筛选阶段，就会只查出命中的第一条记录，所以这里打印出来的更新数量是1，但实际查询条件命中的不只一条记录！
        System.out.println("更新数量：" + result.getMatchedCount() + ", 成功数量：" + result.getModifiedCount());
```

你也可以直接使用Document对象进行replace