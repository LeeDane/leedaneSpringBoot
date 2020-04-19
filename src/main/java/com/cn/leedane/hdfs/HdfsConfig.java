package com.cn.leedane.hdfs;

import java.io.Serializable;

/**
 * https://blog.csdn.net/zhouzhiwengang/article/details/94465850
 * @author LeeDane
 * 2020年03月09日 20:56
 * Version 1.0
 */
public class HdfsConfig implements Serializable {
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = -3927708731917979149L;
    // hdfs 服务器地址
    private String hostname;
    // hdfs 服务器端口
    private String port;
    // hdfs 服务器账户
    private String username;

    // 构造函数
    public HdfsConfig() {
        super();
    }

    public HdfsConfig(String hostname, String port, String username) {
        super();
        this.hostname = hostname;
        this.port = port;
        this.username = username;
    }

    //set 和 get
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
