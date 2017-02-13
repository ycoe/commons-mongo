官方文档：https://docs.mongodb.com/v3.2/reference/method/db.collection.count/

## 一、方法

### long count()

获取当前collection的总记录数



### long count(Bson filter)

获取当前collection中，满足某个查询条件filter的总记录数



### long count(Bson filter, CountOptions options)

获取当前collection中，满足某个查询条件filter的总记录数


## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |
| options | CountOptions | 配置，参考：com.mongodb.client.model.CountOptions |

## 三、DEMO

```java
long count = houseDao.count();
```

```java
long count2 = houseDao.count(
        Filters.eq("location.cityId", 1337) //筛选条件
);
```

``` java
CountOptions opts = new CountOptions();
opts.skip(10); //命中结果先跳过10条记录
opts.limit(100); //命中结果限制100条
long count3 = houseDao.count(
        Filters.eq("location.cityId", 1337), //筛选条件
        opts //计数配置
);
```

