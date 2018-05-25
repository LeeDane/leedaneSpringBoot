package com.cn.leedane.handler.mall;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.S_PromotionAuditMapper;
import com.cn.leedane.model.mall.promotion.S_PromotionAuditBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 推广用户提交审核信息的处理类
 * @author LeeDane
 * 2018年3月26日 下午5:16:38
 * version 1.0
 */
@Component
public class S_PromotionAuditHandler {
	@Autowired
	private S_PromotionAuditMapper promotionAuditMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取推广用户对象
	 * @param userId
	 * @return
	 */
	public S_PromotionAuditBean getPromotion(int userId){
		String key = getPromotionAuditKey(userId);
		Object obj = systemCache.getCache(key);
		S_PromotionAuditBean promotionAuditBean = null;
		//deletePromotionAuditCache(userId);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					promotionAuditBean =  (S_PromotionAuditBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_PromotionAuditBean.class);
					if(promotionAuditBean != null){
						systemCache.addCache(key, promotionAuditBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						promotionAuditBean = promotionAuditMapper.getAudit(userId, ConstantsUtil.STATUS_NORMAL);
						if(promotionAuditBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(promotionAuditBean));
								systemCache.addCache(key, promotionAuditBean);
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
				promotionAuditBean = promotionAuditMapper.getAudit(userId, ConstantsUtil.STATUS_NORMAL);
				if(promotionAuditBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(promotionAuditBean));
						systemCache.addCache(key, promotionAuditBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			promotionAuditBean = (S_PromotionAuditBean)obj;
		}
		
		if(promotionAuditBean == null || promotionAuditBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return promotionAuditBean;
	}
	
	/**
	 * 根据用户ID删除该用户的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deletePromotionAuditCache(int userId){
		String key = getPromotionAuditKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取推广审核在redis的key
	 * @param userId
	 * @return
	 */
	public static String getPromotionAuditKey(int userId){
		return ConstantsUtil.PROMOTION_AUDIT_REDIS + userId;
	}

}
