查询第一条记录并替换，返回的文档，返回的值详见后面`FindOneAndReplaceOptions`说明

官方文档：https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndReplace/


## 一、方法

### TDocument findOneAndReplace(Bson filter, TDocument replacement)

查询第一条记录并替换


### TDocument findOneAndReplace(Bson filter, TDocument replacement, FindOneAndReplaceOptions options)

查询第一条记录并替换




## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件，请 |
| replacement | < TDocument > | 替换的实体 |
| options | FindOneAndReplaceOptions | 操作配置 |

`FindOneAndReplaceOptions`配置说明

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| projection | Bson | 返回的字段 |
| sort | Bson | 排序，会影响到返回的第一条记录 |
| upsert | boolean | 详见下面upsert说明，默认为false |
| maxTimeMS | long | 设定最大超时时间，执行超过这个数时会报错。单位：毫秒，默认值0，不作限制 |
| returnDocument | ReturnDocument | 返回文档类型，默认：ReturnDocument.BEFORE 返回旧文档，如果upsert为ture，则filter未命中时，返回null |


### upsert说明

非必填

当为`true`时：

-    如果filter未命中时，会插入replacement文档。如果returnDocument为ReturnDocument.BEFORE时返回null，否则返回新插入的文档。如果这时未指定\_id，MongoDB会先尝试使用filter中使用的\_id(使用“等于”操作的)，如果没有指定，则会自动生成\_id。如果replacement和filter中同时存在\_id时，两值必须相等，不然会报错！

-    如果filter命中时，直接替换第一条记录。如果returnDocument为ReturnDocument.BEFORE时返回被替换掉的旧的文档，否则返回替换后的文档

-    为避免重复插入，建议Filter使用唯一的索引！

当为`false`时：

-    如果filter未命中，则不做任何事，返回null

-    如果filter命中，替换第一条记录，按returnDocument定义的值返回旧的或新的文档



## 三、DEMO

暂无