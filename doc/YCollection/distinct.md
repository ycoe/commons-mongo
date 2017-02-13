官方文档：https://docs.mongodb.com/v3.2/reference/method/db.collection.distinct/

## 一、方法

### < TResult > DistinctIterable< TResult > distinct(String fieldName, Class< TResult > resultClass)

获取当前collection中某个字段的所有去重值


### < TResult > DistinctIterable< TResult > distinct(String fieldName, Bson filter, Class< TResult > resultClass)

获取当前collection中满足某些筛选条件(filter)的字段，返回去重后的结果

## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| fieldName | String | 字段名称，嵌套字段使用点号(.)分隔 |
| filter | Bson | 筛选条件 |
| resultClass | Class | 此字段的数据类型 |


## 三、DEMO

``` java
houseDao.distinct(
        "type",  //需要获取的字段名称，嵌套使用点号(.)分隔 
        Integer.class //字段类型，必须所有此字符都是此类型，不然会报错，比如如果有个字符是double型，这里就会报错！
).into(Lists.newArrayList());
```

``` java
houseDao
        .distinct(
                "location.cityName", //需要获取的字段名称，嵌套使用点号(.)分隔
                Filters.gt("location.cityId", 1000), //筛选条件
                String.class  //字段类型
        )
        .forEach( //遍历所有结果
                (Consumer<? super String>) name -> System.out.println(name)
        )
        ;
    }
```