package com.cn.leedane.handler.mall;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.S_ShopMapper;
import com.cn.leedane.model.mall.S_ShopBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 商店的处理类
 * @author LeeDane
 * 2017年11月15日 上午10:18:53
 * version 1.0
 */
@Component
public class S_ShopHandler {
	@Autowired
	private S_ShopMapper shopMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取正常状态的商店对象
	 * @param shopId
	 * @return
	 */
	public S_ShopBean getNormalShopBean(long shopId){
		S_ShopBean shop = getShopBean(shopId);
		if(shop == null || shop.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return shop;
	}
	
	/**
	 * 获取商店对象(不去判断是否是正常状态的商店)
	 * @param shopId
	 * @return
	 */
	public S_ShopBean getShopBean(long shopId){
		String key = getShopKey(shopId);
		Object obj = systemCache.getCache(key);
		S_ShopBean shopBean = null;
		//deleteShopBeanCache(shopId);
		
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					shopBean =  (S_ShopBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_ShopBean.class);
					if(shopBean != null){
						systemCache.addCache(key, shopBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						shopBean = shopMapper.findById(S_ShopBean.class, shopId);
						if(shopBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(shopBean));
								systemCache.addCache(key, shopBean);
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
				shopBean = shopMapper.findById(S_ShopBean.class, shopId);
				if(shopBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(shopBean));
						systemCache.addCache(key, shopBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			shopBean = (S_ShopBean)obj;
		}
		
		if(shopBean == null || shopBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return shopBean;
	}
	
	/**
	 * 根据商店ID删除该商店的cache和redis缓存
	 * @param shopId
	 * @return
	 */
	public boolean deleteShopBeanCache(long shopId){
		String key = getShopKey(shopId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取商店在redis的key
	 * @param shopId
	 * @return
	 */
	public static String getShopKey(long shopId){
		return ConstantsUtil.SHOP_REDIS + shopId;
	}

}
