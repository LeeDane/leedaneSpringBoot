package com.cn.leedane.handler.mall;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.S_HomeShopMapper;
import com.cn.leedane.model.mall.S_HomeShopBean;
import com.cn.leedane.model.mall.S_HomeShopBeans;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 首页商店的处理类
 * @author LeeDane
 * 2018年1月11日 下午2:25:52
 * version 1.0
 */
@Component
public class S_HomeShopHandler {
	@Autowired
	private S_HomeShopMapper homeShopMapper;
	
	@Autowired
	private S_ShopHandler shopHandler;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;

	/**
	 * 获取商店对象
	 * @return
	 */
	public S_HomeShopBeans getShopBeans(){
		String key = getShopKey();
		Object obj = systemCache.getCache(key);
		S_HomeShopBeans shopBeans = null;
		//deleteCarouselBeansCache();
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					shopBeans =  (S_HomeShopBeans) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_HomeShopBeans.class);
					if(shopBeans != null){
						systemCache.addCache(key, shopBeans);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						shopBeans = getShopBeansByDatabase();
						if(CollectionUtil.isNotEmpty(shopBeans.getHomeShopBeans())){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(shopBeans));
								systemCache.addCache(key, shopBeans);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				shopBeans = getShopBeansByDatabase();
				if(CollectionUtil.isNotEmpty(shopBeans.getHomeShopBeans())){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(shopBeans));
						systemCache.addCache(key, shopBeans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			shopBeans = (S_HomeShopBeans)obj;
		}
		return shopBeans;
	}
	
	private S_HomeShopBeans getShopBeansByDatabase(){
		S_HomeShopBeans homeShopBeans = new S_HomeShopBeans();
		List<S_HomeShopBean> list = homeShopMapper.shops(ConstantsUtil.STATUS_NORMAL, 5);
		if(CollectionUtil.isNotEmpty(list)){
			if(list.size() > 0){
				homeShopBeans.setHomeShopBeans(list);
				homeShopBeans.setTotal(list.size());
			}
		}
		return homeShopBeans;
	}
	
	/**
	 * 删除首页商店的cache和redis缓存
	 * @return
	 */
	public boolean deleteShopBeansCache(){
		String key = getShopKey();
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取首页商店在redis的key
	 * @return
	 */
	public static String getShopKey(){
		return ConstantsUtil.MALL_HOME_SHOP_REDIS;
	}

}
