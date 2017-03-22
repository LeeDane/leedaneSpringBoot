package com.cn.leedane.redis.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * Redis请求的配置
 * @author LeeDane
 * 2015年12月30日 下午3:10:33
 * Version 1.0
 */
public class RedisConfig {
	
	public static Map<String, Object> properties;
	
	static{
		/*SystemCache systemCache = (SystemCache)SpringUtils.getBean("systemCache");
		Object obj = systemCache.getCache("leedaneProperties");
		properties = (Map<String, Object>) obj;*/
		
		//测试环境需要手动加载该配置文件
		properties = new HashMap<String, Object>();
		InputStream in = CommonUtil.getResourceAsStream("leedane.properties");  
		 // 创建Properties实例
		Properties prop = new Properties();
		// 将Properties和流关联
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<Object> set = prop.keySet();
	    Iterator<Object> it = set.iterator();
	    while(it.hasNext()) {
	    	String key = String.valueOf(it.next());
	   	 	properties.put(key, prop.getProperty(key));
	    }
	    
	}
	
	/**
	 * 获取redis请求的IP地址
	 * @return
	 */
	public static String getIp() {
		return StringUtil.changeNotNull(properties.get("redisServer"));
	}
	
	/**
	 * 获取redis请求的端口
	 * @return
	 */
	public static int getPort() {
		return StringUtil.changeObjectToInt(properties.get("redisPort"));
	}
	
	/**
	 * 获取redis的访问密码
	 * @return
	 */
	public static String getAuth() {
		return StringUtil.changeNotNull(properties.get("redisAuth"));
	}
	
	/**
	 * 获取redis最大的实例
	 * @return
	 */
	public static int getMaxActive() {
		return StringUtil.changeObjectToInt(properties.get("redisMaxActive"));
	}
	
	/**
	 * 获取redis最多有多少个状态为idle(空闲的)
	 * @return
	 */
	public static int getMaxIdle() {
		return StringUtil.changeObjectToInt(properties.get("redisMaxIdle"));
	}
	
	/**
	 * 获取redis待可用连接的最大时间
	 * @return
	 */
	public static int getMaxWait() {
		return StringUtil.changeObjectToInt(properties.get("redisMaxWait"));
	}
	
	/**
	 * 获取redis连接超时时间
	 * @return
	 */
	public static int getTimeOut() {
		return StringUtil.changeObjectToInt(properties.get("redisTimeOut"));
	}
}
