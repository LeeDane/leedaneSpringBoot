package com.cn.leedane.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ElasticSearchTest {
	
	public static void main(String[] args) {
		//InetAddress sAddress = InetAddress.getLocalHost();
		 // 配置你的es,现在这里只配置了集群的名,默认是elasticsearch,跟服务器的相同  
	/*	Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();  
		        // 这里可以同时连接集群的服务器,可以多个,并且连接服务是可访问的  
		 Client    client = new TransportClient(settings).addTransportAddress(  
		                new InetSocketTransportAddress("192.168.9.140", 9300)).addTransportAddress(  
		                new InetSocketTransportAddress("host2", 9300));  */
		 //设置集群名字
        /*Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "search")
                .put("client.transport.sniff", true)
               . build();	 
        Client  client = new TransportClient.Builder().settings(settings).build()
        .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("127.0.0.1",9200)));
        
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
            "}";
        IndexResponse response = client.prepareIndex("twitter", "tweet")
                .setSource(json)
                .get();
        if (response.isCreated()) {
            System.out.println("创建成功!");
         }*/
	}

}
