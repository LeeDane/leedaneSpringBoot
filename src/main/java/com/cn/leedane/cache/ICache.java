package com.cn.leedane.cache;

/**
 * 缓存类接口
 * @author LeeDane
 * 2016年7月12日 下午2:36:50
 * Version 1.0
 */
public interface ICache {
	
	/**
	 * 将value添加到缓存中(不强制覆盖已经存在的key)
	 * @param key  键名称
	 * @param value 值
	 */
	public void addCache(String key, Object value);
	
	/**
	 * 将value添加到缓存中
	 * @param key  键名称
	 * @param value 值
	 * @param isCover 对已经存在的key是否强制覆盖
	 */
	public void addCache(String key, Object value, boolean isCover);
	
	/**
	 * 根据key获得缓存
	 * @param key
	 * @return
	 */
	public Object getCache(String key);

	/**
	 * 根据key移除该缓存
	 * @param key
	 * @return
	 */
	public void removeCache(String key);

}
