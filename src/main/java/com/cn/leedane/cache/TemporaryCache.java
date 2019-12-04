package com.cn.leedane.cache;
import com.cn.leedane.utils.StringUtil;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 临时缓存类
 * @author LeeDane
 * 2016年7月12日 下午2:37:05
 * Version 1.0
 */
public class TemporaryCache implements com.cn.leedane.cache.ICache{

	//定义缓存对象
	private static Cache temporaryEhCache;
	
	/**
	 * 获取MyEhcache实体
	 * @return
	 */
	/*public static synchronized MyEhcache getInstance(){
		if(myEhcache == null ) myEhcache = new MyEhcache();
		return myEhcache;
	}*/
	
	public static void setTemporaryEhCache(Cache temporaryEhCache) {
		TemporaryCache.temporaryEhCache = temporaryEhCache;
	}
	
	public static Cache getTemporaryEhCache() {
		return temporaryEhCache;
	}
	
	public void addCache(String key, Object value){		
		addCache(key, value, false);
	}
	
	public void addCache(String key, Object value, boolean isCover){		
		//不覆盖，就是对已经存在key的数据，不需要再保存
		if(!isCover){
			//该key已经有缓存
			if(!getCache(key).equals("")) return;
		}
		
		//空的键名称将直接返回
		if(StringUtil.isNull(key)) return;
		
		//把键和值存放在节点中
		Element element = new Element(key, value);	
		
		//将节点加载进缓存中
		temporaryEhCache.put(element);
	}
	
	/**
	 * 根据key获得缓存
	 * @param key
	 * @return
	 */
	public Object getCache(String key){
		return  temporaryEhCache.get(key) == null ? "" : temporaryEhCache.get(key).getObjectValue();
	}

	/**
	 * 根据key移除该缓存
	 * @param key
	 * @return
	 */
	public void removeCache(String key){
		if(temporaryEhCache.get(key) != null){
			temporaryEhCache.remove(key);
		}
	}

}
