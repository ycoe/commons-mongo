聚合查询，是mongodb新操作的简单的方法，可以进行比较复杂的数据统计等操作

更多操作可以参考官方文档：https://docs.mongodb.com/manual/reference/command/aggregate/

## 一、方法

### AggregateIterable< TDocument> aggregate(List< ? extends Bson > pipeline)

聚合查询，返回当前dao指定的类iterable

### < TResult > AggregateIterable<TResult> aggregate(List< ? extends Bson > pipeline, Class< TResult > resultClass)

聚合查询，返回指定的类iterable


## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| pipeline | List< ? extends Bson > | 聚合查询的列表 |
| resultClass | Class | 此字段的数据类型 |




## 三、DEMO

直接返回dao指定的实体
``` java
int count = 5;
List<House> houseList = Lists.newArrayList();
houseDao.aggregate(Lists.newArrayList(
        new Document("$match", new Document("location.cityId", 1337)), //筛选条件，这里不能用Filters!
        new Document("$sample", new Document("size", count)) //随机获取count个记录
))
.forEach((Consumer<? super House>) houseList::add) //加入列表
//.into(houseList) //上面一行也可以写成这样
;
```

更复杂计算，返回的其它类型实体的结果

``` java
Map<String, Integer> cityCounts = Maps.newHashMap();
Document match = new Document("type", 1);
Document project = new Document("name", "$location.cityName")
        .append("count", new Document("$sum", 1));
houseDao
        .getDocumentMongoCollection(Document.class)
        .aggregate(
                Lists.newArrayList(
                        new Document("$match", match),
                        new Document("$project", project)
                )
        ).forEach(
        (Consumer<? super Document>) doc -> {
            cityCounts.put(doc.getString("name"), doc.getInteger(count));
        }
);
```

你还可以直接封装进一个对象，但必须先声明

``` java
public class Counter {
    private String name;

    private int count;
    
    //...其它getter 和 setter
}
```

``` java
List<Counter> counters = Lists.newArrayList();
houseDao
    .getDocumentMongoCollection(Counter.class) //必须先声明（获取Counter的YCollection实例）
    .aggregate(
            Lists.newArrayList(
                    new Document("$match", match),
                    new Document("$project", project)
            )
    )
    .into(counters);
```

还有一种更简单的写法：


``` java
houseDao
        .aggregate(
                Lists.newArrayList(
                        new Document("$match", match),
                        new Document("$project", project)
                ),
                Counter.class
        )
        .into(counters);
```









