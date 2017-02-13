## 一、方法

### boolean exists(Bson filter)

判断当前collection中某些条件的数据是否存在



## 二、参数说明：

| 名称 | 类型 | 说明 |
| :-- | :--: | :-- |
| filter | Bson | 筛选条件 |

## 三、DEMO


``` java
boolean exists = houseDao.exists(Filters.eq("_id", 12345));
```



