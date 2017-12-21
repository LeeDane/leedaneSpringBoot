package com.cn.leedane.handler.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.shop.S_WishMapper;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 商品心愿单的处理类
 * @author LeeDane
 * 2017年11月13日 下午6:49:57
 * version 1.0
 */
@Component
public class S_WishHandler {
	@Autowired
	private S_WishMapper wishMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取心愿单数量对象
	 * @param userId
	 * @return
	 */
	public int getWishNumber(int userId){
		//deleteWishCache(userId);
		String key = getWishKey(userId);
		Object obj = systemCache.getCache(key);
		
		int wishNumber = 0;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				wishNumber =  StringUtil.changeObjectToInt(redisUtil.getString(key));
				systemCache.addCache(key, wishNumber);
			}else{//redis没有的处理
				wishNumber = SqlUtil.getTotalByList(wishMapper.getNumber(userId, ConstantsUtil.STATUS_NORMAL));
				redisUtil.addString(key, wishNumber +"");
				systemCache.addCache(key, wishNumber);
			}
		}else{
			wishNumber = (int) obj;
		}
		return wishNumber;
	}
	
	/**
	 * 根据用户ID删除该用户的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteWishCache(int userId){
		String key = getWishKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取心愿单在redis的key
	 * @param userId
	 * @return
	 */
	public static String getWishKey(int userId){
		return ConstantsUtil.PRODUCT_WISH_REDIS + userId;
	}

}
