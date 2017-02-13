官方文档：https://docs.mongodb.com/v3.2/reference/method/db.collection.find/


## 一、方法

### FindIterable< TDocument > find()

查找当前collection所有的文档，返回的iterable包含的对象为当前getDocumentClass()返回的对象实例


### FindIterable< TDocument > find(Bson filter)

查找当前collection满足筛选条件(filter)的所有文档


### < TResult > FindIterable< TResult > find(Class< TResult > resultClass)

查找当前collection所有文档。并返回指定的对象


### < TResult > FindIterable< TResult > find(Bson filter, Class< TResult > resultClass)

查找当前collection满足筛选条件(filter)的所有文档。并返回指定的对象



## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |
| resultClass | Class | 此字段的数据类型 |


## 三、DEMO

``` java
House house = houseDao
        .find()   //查询
        .first(); //将第一条数据返回来
```

```java
List<House> houseList = houseDao
        .find(Filters.eq("location.cityId", 1337)) //查询条件
        .projection(Projections.include("basicInfo", "location.cityId")) //指定查询哪些字段
        .skip(5) //跳过5条记录
        .limit(10) //查询10条记录
        .into(Lists.newArrayList()) //将结果塞进List内返回
        ;
```

```java
Integer pageNo = null;
int pageSize = 20;
List<String> houseNames = Lists.newArrayList();
Document filter = new Document("location.cityId", 1337)
        .append("flags", "hot");
FindIterable<House> iterable = houseDao.find(filter)
        .projection(Projections.exclude("otherInfo")) // 不包含某些字段
        .sort(Sorts.descending("updateTime")) //按updateTime倒序
        ;
if(pageNo != null && pageNo > 0) {
    iterable.skip((pageNo - 1) * pageSize)
            .limit(pageSize);
}
iterable.forEach(
        (Consumer<House>) h -> houseNames.add(h.getBasicInfo().getHouseName())
);
```
