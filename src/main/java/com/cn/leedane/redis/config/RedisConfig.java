package com.cn.leedane.redis.config;

/**
 * Redis请求的配置
 * @author LeeDane
 * 2015年12月30日 下午3:10:33
 * Version 1.0
 */
public class RedisConfig {

	//获取配置文件
	private static LeedanePropertiesConfig config = LeedanePropertiesConfig.newInstance();
	
	/**
	 * 获取redis请求的IP地址
	 * @return
	 */
	public static String getIp() {
		return config.getString("redisServer");
	}
	
	/**
	 * 获取redis请求的端口
	 * @return
	 */
	public static int getPort() {
		return config.getInt("redisPort");
	}
	
	/**
	 * 获取redis的访问密码
	 * @return
	 */
	public static String getAuth() {
		return config.getString("redisAuth");
	}
	
	/**
	 * 获取redis最大的实例
	 * @return
	 */
	public static int getMaxActive() {
		return config.getInt("redisMaxActive");
	}
	
	/**
	 * 获取redis最多有多少个状态为idle(空闲的)
	 * @return
	 */
	public static int getMaxIdle() {
		return config.getInt("redisMaxIdle");
	}
	
	/**
	 * 获取redis待可用连接的最大时间
	 * @return
	 */
	public static int getMaxWait() {
		return config.getInt("redisMaxWait");
	}
	
	/**
	 * 获取redis连接超时时间
	 * @return
	 */
	public static int getTimeOut() {
		return config.getInt("redisTimeOut");
	}
}
