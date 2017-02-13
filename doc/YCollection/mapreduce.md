mapReduce方法是比较早期提供的统计方法，可以实现很灵活的统计。后期版本出了[aggregate](/YCollection/aggregate.md)，大部分的功能可以直接使用[aggregate](/YCollection/aggregate.md)实现，这里不再解释此方法的。

更多信息详见官方文档：https://docs.mongodb.com/manual/core/map-reduce/


## 一、方法

### MapReduceIterable< TDocument > mapReduce(String mapFunction, String reduceFunction)


### < TResult > MapReduceIterable< TResult > mapReduce(String mapFunction, String reduceFunction, Class< TResult > resultClass)


## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| mapFunction | String |  |
| reduceFunction | String |  |
| resultClass | Class |  |


## 三、DEMO

暂无