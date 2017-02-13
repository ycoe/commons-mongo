更新命中的所有记录

注意，如果筛选条件命中多条，会直接更新命中的所有记录，如果只需要更新命中的第一条，请使用[updateOne](/YCollection/updateone.md)

如果筛选条件只命中一条记录，效果跟updateOne是一样的

如果是嵌套字段更新，需要使用点号连接：

```javascript
{
        "location.cityId": 1337
}
```

如果使用下面的结构更新，则会将`location`内的文档覆盖掉！如果原来有`location.cityName`，则将被覆盖！

```javascript
{
        "location": {
                "cityId": 1337
        }
}
```

update与replace的区别在于，update只更新指定的字段！replace是替换整个document

官方文档：https://docs.mongodb.com/v3.2/reference/method/db.collection.updateMany/



## 一、方法

### UpdateResult updateMany(Bson filter, Bson update)

更新命中的所有文档


### UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions)

更新命中的所有文档



## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |
| update | Bson | 更新的数据 |
| updateOptions | UpdateOptions | 更新操作的配置 |




## 三、DEMO

简单更新，如果筛选条件只命中一条记录时，效果与updateOne是一样的

``` java
UpdateResult result = houseDao.updateMany(Filters.eq("_id", 423584), Updates.set("tags", Lists.newArrayList("测试", "热门")));
```

更复杂的更新操作


``` java
Document set = new Document("tags", Lists.newArrayList("测试", "热门"))
        .append("updateTime", System.currentTimeMillis());
Document unset = new Document("otherInfo.aliasName", true);
Document inc = new Document("viewCount", 1);
Document updateDoc = new Document("$set", set) //$set: 设置值
        .append("$unset", unset)         //$unset: 删除字段
        .append("$inc", inc);           //$inc: 累加
UpdateResult result2 = houseDao.updateMany(
        Filters.eq("_id", 423584), //筛选条件，会更新所有命中的记录！
        updateDoc
);
```


