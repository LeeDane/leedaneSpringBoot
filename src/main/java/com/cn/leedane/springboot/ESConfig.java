package com.cn.leedane.springboot;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class ESConfig {

    private Logger logger  = LoggerFactory.getLogger(this.getClass());

    @Value("${elasticsearch.ip}")
    private String ip;
    @Value("${elasticsearch.port}")
    private String port;
    @Value("${elasticsearch.cluster.name}")
    private String clusterName;
    @Value("${elasticsearch.pool}")
    private String poolSize;  //线程池

    @Bean
    public TransportClient getTransportClient() {
        logger.info("ElasticSearch初始化开始。。");
        logger.info("要连接的节点1的ip是{}，端口是{}，集群名为{}" , ip , port , clusterName);
        TransportClient transportClient = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", clusterName)
//                    .put("client.transport.sniff",true)//目的是为了可以找到集群，嗅探机制开启
                    .put("thread_pool.search.size", Integer.parseInt(poolSize))//增加线程池个数，暂时设为5
                    .build();
            TransportClient client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(ip), Integer.parseInt(port)));
            logger.info("ElasticSearch初始化完成。。");
            return client;

        }catch (Exception e){
            e.printStackTrace();
            logger.error("ElasticSearch初始化失败：" +  e.getMessage(),e);
        }
        return transportClient;
    }

    /*@Bean
    public TransportClient getTransportClient() {
        logger.info("ElasticSearch初始化开始。。");
        logger.info("要连接的节点1的ip是{}，端口是{}，集群名为{}" , firstIp , firstPort , clusterName);
//        logger.info("要连接的节点2的ip是{}，端口是{}，集群名为{}" , secondIp , secondPort , clusterName);
//        logger.info("要连接的节点3的ip是{}，端口是{}，集群名为{}" , thirdIp , thirdPort , clusterName);
        TransportClient transportClient = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name",clusterName)//集群名称

                    .put("node.name","node1")//
                    .put("client.transport.sniff",true)//目的是为了可以找到集群，嗅探机制开启
                    .build();
            transportClient = new PreBuiltTransportClient(settings);
            TransportAddress firstAddress = new TransportAddress(InetAddress.getByName(firstIp),Integer.parseInt(firstPort));
//            TransportAddress secondAddress = new TransportAddress(InetAddress.getByName(secondIp),Integer.parseInt(secondPort));
//            TransportAddress thirdAddress = new TransportAddress(InetAddress.getByName(thirdIp),Integer.parseInt(thirdPort));
            transportClient.addTransportAddress(firstAddress);
//            transportClient.addTransportAddress(secondAddress);
//            transportClient.addTransportAddress(thirdAddress);
            logger.info("ElasticSearch初始化完成。。");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("ElasticSearch初始化失败：" +  e.getMessage(),e);
        }
        return transportClient;
    }*/
}
