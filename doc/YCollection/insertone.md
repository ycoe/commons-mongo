官方文档：https://docs.mongodb.com/v3.2/reference/method/db.collection.insertOne/


## 一、方法

### void insertOne(TDocument document)

插入一个文档


### void insertOne(TDocument document, InsertOneOptions options)

插入一个文档


## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| document | < TDocument > | 当前dao指定的类实例 |
| options | InsertOneOptions | 插入操作配置 |


## 三、DEMO


因在dao是指定了文档对应的JavaBean类型，所以可以直接插入JavaBean对应的实例

``` java
House house = new House();
// ... house的各属性set ...
houseDao.insertOne(house);
```

如果需要插入Document类型，可以这么操作: 

``` java
Document doc = new Document("basicInfo", new Document("houseName", "测试楼盘")) //注意，如果未指定_id，插入时会直接使用ObjectId()!
        //.append("location.cityId", 1337)
        .append("location", new Document("cityId", 1337)); //注意，上一行的写法会报错，key值不能带.
houseDao
        .getDocumentMongoCollection(Document.class)
        .insertOne(doc);
```




