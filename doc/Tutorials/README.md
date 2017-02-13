本教程中使用spring容器进行管理

##  一、引入maven依赖

``` xml
<dependency>
    <groupId>com.fangdd.traffic</groupId>
    <artifactId>common-mongodb</artifactId>
    <version>2.0-SNAPSHOT</version>
</dependency>
```

common-mongodb 2.0-SNAPSHOT会自动会引入MongoDB的java driver:

org.mongodb:mongo-java-driver:3.3.0


## 二、Spring 配置

spring-mongo.xml（需要在主配置中引入）

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd"
       default-lazy-init="false">

    <description>Spring MongoDB配置</description>

    <bean class="com.fangdd.traffic.common.mongo.core.YMongoClient">
        <property name="mongoDBConnections" value="${mongodb.connections}"/>
        <property name="connectionsPerHost">
            <value type="int">${mongodb.connectionsPerHost}</value>
        </property>
        <property name="maxWaitTime">
            <value type="int">${mongodb.maxWaitTime}</value>
        </property>
    </bean>
</beans>
```


xml中使用的变量，在配置文件中，在spring的主配置中必须添加进来才可以使用


``` xml
<bean id="localProperties" class="org.springframework.beans.factory.config.PropertiesFactoryBean">
        <property name="fileEncoding" value="UTF-8"/>
        <property name="localOverride" value="true"/>
        <property name="locations" value="classpath:/server.properties"/>
    </bean>

    <context:property-placeholder ignore-resource-not-found="true" ignore-unresolvable="true" properties-ref="localProperties"/>
```


server.properties

``` properties
#MongoDB配置
mongodb.connections=[{"host":"127.0.0.1","port":27017}]

#每台服务器连接池连接个数
mongodb.connectionsPerHost=10

#最大等待时间(毫秒)
mongodb.maxWaitTime=2000

#cms库
mongodb.database.cms=cms

```


## 三、写模块DAO

一般，可以一个database写一个基类DAO

CmsEntityDao.java
``` java
public abstract class CmsEntityDao<T> extends BaseEntityDao<T> { //继承BaseEntityDao
    /**
     * 数据库名称，可以直接从配置中注进来
     */
    @Value("${mongodb.database.cms}")
    private String cmsDatabase; 

    /**
     * MongoClient类，是在 spring-mongo.xml中声明的，自动注入
     */
    @Autowired
    private YMongoClient yMongoClient;
    
    @Override
    protected String getDatabaseName() {
        return cmsDatabase;
    }

    @Override
    protected YMongoClient getYMongoClient() {
        return yMongoClient;
    }
}
```

## 四、写POJO实体类

实体类是标准的java bean，更多实体类声明，详见[POJO类](/spring/pojo.md)

``` java
public class CmsHouse {
/**
* 自增主键
*/
@AutoIncrement(start = 10000)
private long id;
public String name;
// ...其它属性及settter/getter方法
}

```

## 五、写DAO

HouseDao.java

``` java
@Service //需要此声明，以让spring容器来管理
public class HouseDao extends CmsEntityDao<House> {
    @Override
    protected String getCollectionName() {
        return "house";
    }

    @Override
    protected Class<House> getDocumentClass() {
        return House.class;
    }
}
```

## 六、使用dao

在使用spring容易管理的类中，可以直接注入HouseDao

``` java
@Autowired
private HouseDao houseDao; 

public boolean exists(Bson filter){
    return houseDao.exists(filter);
}
```






