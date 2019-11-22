package com.cn.leedane.handler.mall;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.S_PromotionUserMapper;
import com.cn.leedane.model.mall.promotion.S_PromotionUserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 推广用户信息的处理类
 * @author LeeDane
 * 2018年3月26日 下午3:26:54
 * version 1.0
 */
@Component
public class S_PromotionUserHandler {
	@Autowired
	private S_PromotionUserMapper promotionUserMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取推广用户对象
	 * @param userId
	 * @return
	 */
	public S_PromotionUserBean getPromotion(long userId){
		String key = getPromotionUserKey(userId);
		Object obj = systemCache.getCache(key);
		S_PromotionUserBean promotionUserBean = null;
		//deletePromotionUserCache(userId);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					promotionUserBean =  (S_PromotionUserBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_PromotionUserBean.class);
					if(promotionUserBean != null){
						systemCache.addCache(key, promotionUserBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						promotionUserBean = promotionUserMapper.getUser(userId, ConstantsUtil.STATUS_NORMAL);
						if(promotionUserBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(promotionUserBean));
								systemCache.addCache(key, promotionUserBean);
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
				promotionUserBean = promotionUserMapper.getUser(userId, ConstantsUtil.STATUS_NORMAL);
				if(promotionUserBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(promotionUserBean));
						systemCache.addCache(key, promotionUserBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			promotionUserBean = (S_PromotionUserBean)obj;
		}
		
		if(promotionUserBean == null || promotionUserBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return promotionUserBean;
	}
	
	/**
	 * 根据用户ID删除该用户的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deletePromotionUserCache(long userId){
		String key = getPromotionUserKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取推广用户在redis的key
	 * @param userId
	 * @return
	 */
	public static String getPromotionUserKey(long userId){
		return ConstantsUtil.PROMOTION_USER_REDIS + userId;
	}

}
