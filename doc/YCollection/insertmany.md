批量插入多条记录，底层也会调用[bulkWrite](/YCollection/bulkwrite.md)方法

官方文档：https://docs.mongodb.com/v3.2/reference/method/db.collection.insertMany/

## 一、方法

### void insertMany(List< ? extends TDocument > documents)

批量插入多条记录


### void insertMany(List< ? extends TDocument > documents, InsertManyOptions options)

批量插入多条记录


## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| documents | List< TDocument > | 需要插入的文档列表 |
| options | InsertManyOptions | 批量插入的操作配置 |


## 三、DEMO

因在dao是指定了文档对应的JavaBean类型，所以可以直接插入JavaBean对应的实例

``` java
House house = new House();
// ... house的各属性set ...
House house2 = new House();
// ... house2的各属性set ...
houseDao.insertMany(Lists.newArrayList(
        house,
        house2
        //可以包含更多的house...
));
```

直接插入使用Document对象列表：

``` java
Document doc = new Document("basicInfo", new Document("houseName", "测试楼盘")) //注意，如果未指定_id，插入时会直接使用ObjectId()!
        //.append("location.cityId", 1337)
        .append("location", new Document("cityId", 1337)); //注意，上一行的写法会报错，key值不能带.
houseDao
        .getDocumentMongoCollection(Document.class)
        .insertMany(Lists.newArrayList(
                doc
                //可以包含更多的doc...
        ));

```



