删除第一条filter命中的记录，并返回已删除的记录

跟[deleteOne](/YCollection/deleteone.md)的差别在于：

[deleteOne](/YCollection/deleteone.md)删除后返回删除记录数

findOneAndDelete删除后返回被删除的记录


官方文档：https://docs.mongodb.com/v3.2/reference/method/db.collection.findOneAndDelete/

## 一、方法

### TDocument findOneAndDelete(Bson filter)

删除第一条filter命中的记录，并返回已删除的记录



### TDocument findOneAndDelete(Bson filter, FindOneAndDeleteOptions options)

删除第一条filter命中的记录，并返回已删除的记录



## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |
| options | FindOneAndDeleteOptions | 删除操作的配置，配置中可指定排序，以影响被删除的第一条记录 |


FindOneAndDeleteOptions属性：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| projection | Bson | 删除后返回的记录，指定返回的字段 |
| sort | Bson | 排序 |
| maxTimeMS | long | 设置删除操作允许的最大时间，单位毫秒，默认为0，表示不限制超时 |




## 三、DEMO

简单的查找并删除
``` java
House house = houseDao.findOneAndDelete(Filters.eq("_id", 123456));
```

使用FindOneAndDeleteOptions


``` java
FindOneAndDeleteOptions options = new FindOneAndDeleteOptions();
options.sort(Sorts.ascending("updateTime")); //指定排序，按更新时间顺序
options.projection(Projections.include("location", "basicInfo")); //指定删除后，返回指定的字段
House house = houseDao.findOneAndDelete(Filters.eq("location.cityId", 1337), options);
```



