# YCollection使用

全路径：com.duoec.commons.mongo.core.YCollection


此类相当于是mongodb-java-driver对Collection操作的代理类，暴露了大部分的方法，一些方法比如删表、创建索引的，就没有暴露出来了。

此类与MongoDB的连接使用了连接池，并由Spring管理，DAO的创建开销很小！在获取Collection操作实例时，会自动注册TDocument类及相关的Codec。大部分的结果，可以直接转换成指定的类



``` java
public abstract class YCollection<TDocument> {
    /**
     * 获取数据库名称
     * @return
     */
    protected abstract String getDatabaseName();

    /**
     * 获取Collection名称
     * @return
     */
    protected abstract String getCollectionName();

    /**
     * 获取Collection对应的JavaBean类
     * @return
     */
    protected abstract Class<TDocument> getDocumentClass();

    /**
     * 获取终端连接配置
     * @return
     */
    protected abstract YMongoClient getYMongoClient();
    
    // ...其它操作方法
}

```










