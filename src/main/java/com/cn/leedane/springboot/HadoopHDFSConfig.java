package com.cn.leedane.springboot;

import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * hadoop hdfs 参数配置
 * @author LeeDane
 * 2020年03月11日 14:40
 * Version 1.0
 */
@Configuration
public class HadoopHDFSConfig {

    // 日志记录
    private static final Logger logger = LoggerFactory.getLogger(HadoopHDFSConfig.class);

    @Value("${hdfs.hdfsPath}")
    private String hdfsPath;
    @Value("${hdfs.hdfsName}")
    private String hdfsName;

    /**
     * hadoop hdfs 配置参数对象
     * @return
     */
    @Bean
    public org.apache.hadoop.conf.Configuration  getConfiguration(){
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        configuration.set("fs.defaultFS", hdfsPath);
        configuration.set("dfs.client.use.datanode.hostname", "true");
        return configuration;
    }
    /**
     * hadoop filesystem 文件系统
     * @return
     */
    @Bean(name = "fileSystem")
    public FileSystem getFileSystem(){
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(new URI(hdfsPath), getConfiguration(), hdfsName);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return fileSystem;
    }
}
