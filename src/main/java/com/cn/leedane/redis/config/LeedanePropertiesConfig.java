package com.cn.leedane.redis.config;

import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * leedane.properties配置文件的缓存加载
 * @author LeeDane
 * 2019年6月28日 下午19:10:33
 * Version 1.0
 */
public class LeedanePropertiesConfig {

	private volatile static LeedanePropertiesConfig config;

	public static Map<String, Object> properties;

	private LeedanePropertiesConfig(){

	}

	public static LeedanePropertiesConfig newInstance(){
		if(config == null){
			synchronized (LeedanePropertiesConfig.class){
				if(config == null){
					config = new LeedanePropertiesConfig();
					initMapProperties();
				}
			}
		}

		return config;
	}

	/**
	 * 初始化读取文件
	 */
	private static void initMapProperties(){
		properties = new HashMap<String, Object>();
		InputStream in = CommonUtil.getResourceAsStream("leedane.properties");
		// 创建Properties实例
		Properties prop = new Properties();
		// 将Properties和流关联
		try {
			prop.load(new InputStreamReader(in,  "UTF-8"));
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
	 * 获取key
	 * @param key
	 * @return
	 */
	public Object get(String key){
		if(properties == null){
			synchronized (LeedanePropertiesConfig.class){
				if(properties == null)
					initMapProperties();
			}
		}
		return properties == null ? "" : properties.get(key);
	}

	/**
	 * 获取int类型的值
	 * @param key
	 * @return
	 */
	public int getInt(String key){
		if(properties == null){
			synchronized (LeedanePropertiesConfig.class){
				if(properties == null)
					initMapProperties();
			}
		}
		return properties == null ? 0 : StringUtil.changeObjectToInt(properties.get(key));
	}

	/**
	 * 获取String类型的值
	 * @param key
	 * @return
	 */
	public String getString(String key){
		if(properties == null){
			synchronized (LeedanePropertiesConfig.class){
				if(properties == null)
					initMapProperties();
			}
		}
		return properties == null ? "" : StringUtil.changeNotNull(properties.get(key));
	}

	/**
	 * 获取boolean类型的值, 默认是false
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key){
		if(properties == null){
			synchronized (LeedanePropertiesConfig.class){
				if(properties == null)
					initMapProperties();
			}
		}
		return properties == null ? false : StringUtil.changeObjectToBoolean(StringUtil.changeNotNull(properties.get(key)));
	}
}
