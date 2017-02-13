此方法提供了一种更高效的批量写操作，可用操作有：

| 操作 | 实现类 | 说明 |
| :-- | :--: | :-- |
| insertOne | com.mongodb.client.model.InsertOneModel | 插入一条记录 |
| updateOne | com.mongodb.client.model.UpdateOneModel | 更新一条记录 |
| updateMany | com.mongodb.client.model.UpdateManyModel | 更新多条记录 |
| deleteOne | com.mongodb.client.model.DeleteOneModel | 删除一条记录 |
| deleteMany | com.mongodb.client.model.DeleteManyModel | 删除多条记录 |
| replaceOne | com.mongodb.client.model.ReplaceOneModel | 替换一条记录 |



官方文档：https://docs.mongodb.com/manual/reference/method/db.collection.bulkWrite/

## 一、方法

### BulkWriteResult bulkWrite(List< ? extends WriteModel< ? extends TDocument > > requests)

### BulkWriteResult bulkWrite(List< ? extends WriteModel< ? extends TDocument > > requests, BulkWriteOptions options)

## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| requests | List< ? extends WriteModel< ? extends TDocument > > | 操作列表 |
| options | BulkWriteOptions | 批量操作的配置 |


## 三、DEMO


``` java
House house = new House();
// ... house的各属性set ...
InsertOneModel<House> insertOne = new InsertOneModel(house);

UpdateManyModel<House> updateMany = new UpdateManyModel(Filters.eq("location.cityId", 1337), Updates.inc("viewCount", 1));
houseDao.bulkWrite(Lists.newArrayList(
        insertOne,
        updateMany
));
```

