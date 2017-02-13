更新命中记录的第一条记录

注意，如果筛选条件命中多条，只会取出第一条记录进行更新，如果需要更新多条，请使用[updateMany](/YCollection/updatemany.md)


如果是嵌套字段更新，需要使用点号连接：

```javascript
{
        "location.cityId": 1337
}
```

如果使用下面的结构更新，则会将location内的文档覆盖掉！如果原来有`location.cityName`，则将被覆盖！

```javascript
{
        "location": {
                "cityId": 1337
        }
}
```

update与replace的区别在于，update只更新指定的字段！replace是替换整个document

官方文档：https://docs.mongodb.com/manual/reference/method/db.collection.updateOne/

## 一、方法

### UpdateResult updateOne(Bson filter, Bson update)

更新一条记录


### UpdateResult updateOne(Bson filter, Bson update, UpdateOptions updateOptions)

更新一条记录




## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |
| update | Bson | 更新的数据 |
| updateOptions | UpdateOptions | 更新操作的配置 |


## 三、DEMO

简单的更新

``` java
UpdateResult result = houseDao.updateOne(
    Filters.eq("_id", 423584),  //查询条件，如果命中多条记录，只会更新第一条！
    Updates.set("tags", Lists.newArrayList("测试", "热门")) //更新内容
);
```

更复杂一点的更新

``` java
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
```

如果第二个参数想使用collection中定义的实体类，则可以使用YCollection的扩展类内的方法，
详见[updateOne](/BaseEntityDao/updateone.md)

``` java
public abstract class BaseEntityDao<T> extends YCollection<T> {
        public UpdateResult updateOne(Bson query, T entity){...}
}
```

