package com.duoec.commons.mongo.core;

/**
 * @author 徐文振
 * @date 15/8/31
 */
public class MongoConnectConf {
    /**
     * 服务器
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 授权用户
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
