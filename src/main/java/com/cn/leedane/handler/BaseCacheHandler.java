package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.AllReadBean;
import com.cn.leedane.model.EventAllBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基础处理类
 */
public abstract class BaseCacheHandler<T extends Serializable> implements BaseHandler{
	protected  RedisUtil redisUtil = RedisUtil.getInstance();
	@Autowired
	protected SystemCache systemCache;

	@Override
	public boolean addCache(String key, Object value) throws IOException {
		if(value == null)
			return false;
		systemCache.addCache(key, value, true);
		redisUtil.addSerialize(key, SerializeUtil.serializeObject(value));
		return true;
	}

	/**
	 * 获取缓存所需要的对象
	 * @param params
	 * @return
	 */
	protected abstract T getT(Object... params);

	/**
	 * 获取实体对象的对象
	 * @return
	 */
	protected abstract T getBean();

	/**
	 * 重写了get方法，基本的操作可以通过这里实现就行了
	 * 重载的方法的参数必须一致，返回值不一致也可以。不然重载的方法是无效，不会被执行的
	 * @param params
	 * @return
	 */
	public Object get(Object... params) {
		T datas = null;
		String key = getKey(params);
		Object obj = systemCache.getCache(key);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					datas =  (T) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), getBean().getClass());
					if(datas != null){
						systemCache.addCache(key, datas, true);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						datas = getT(params);
						if(datas != null){
							try {
								addCache(key, datas);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				datas = getT(params);
				if(datas != null){
					try {
						addCache(key, datas);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			datas = (T) obj;
		}
		return datas;
	}

	/**
	 * 这里重写删除方法，目的是对于简单逻辑的删除，在这里就可以统一做了
	 * 在实现类中可以不用实现操作就能获取该删除实现
	 * @param params
	 * @return
	 */
	public boolean delete(Object... params) {
		redisUtil.delete(getKey(params));
		systemCache.removeCache(getKey(params));
		return true;
	}
}
