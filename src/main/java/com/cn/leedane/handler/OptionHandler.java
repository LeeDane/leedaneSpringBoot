package com.cn.leedane.handler;

import com.cn.leedane.cache.JuheCache;
import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.juheapi.JokeApi;
import com.cn.leedane.juheapi.JuHeException;
import com.cn.leedane.model.KeyValueBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 选项辅助类处理类
 * @author LeeDane
 * 2016年7月12日 上午11:53:35
 * Version 1.0
 */
@Component
public class OptionHandler {

	private RedisUtil redisUtil = RedisUtil.getInstance();
	@Autowired
	private SystemCache systemCache;

	@Autowired
	private InitCacheData initCacheData;

	/**
	 * 获取选项数据，为空会根据需要是否重新加载一次
	 * @param key
	 * @param loadIfNull
	 * @return
	 * @throws JuHeException
	 */
	public Object getData(String key, boolean loadIfNull) {
		Object value = systemCache.getCache(key);
		if((value == null || StringUtil.isNull(String.valueOf(value))) && loadIfNull){
			initCacheData.loadOptionTable();
			value = systemCache.getCache(key);
		}
		return value;
	}

	/**
	 * 获取选项数据，为空直接返回
	 * @param key
	 * @return
	 * @throws JuHeException
	 */
	public Object getData(String key){
		return getData(key, false);
	}

	/**
	 * 移除key
	 * @return
	 */
	public boolean removeAll(){
		systemCache.removeCache("sender-email");
		systemCache.removeCache("sender-name");
		systemCache.removeCache("sender-token");
		systemCache.removeCache("sender-host");
		return true;
	}


}
