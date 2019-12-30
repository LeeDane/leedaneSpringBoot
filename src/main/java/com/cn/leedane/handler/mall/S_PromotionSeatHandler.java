package com.cn.leedane.handler.mall;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.PromotionSeatApplyMapper;
import com.cn.leedane.mapper.mall.PromotionSeatMapper;
import com.cn.leedane.mapper.mall.S_PromotionUserMapper;
import com.cn.leedane.model.mall.S_PromotionSeatApplyBean;
import com.cn.leedane.model.mall.S_PromotionSeatBean;
import com.cn.leedane.model.mall.promotion.S_PromotionUserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 推广用户信息的处理类
 * @author LeeDane
 * 2018年3月26日 下午3:26:54
 * version 1.0
 */
@Component
public class S_PromotionSeatHandler {
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;

	@Autowired
	private PromotionSeatMapper promotionSeatMapper;

	@Autowired
	private PromotionSeatApplyMapper promotionSeatApplyMapper;

	/**
	 * 获取推广位，S_PromotionSeatBean的status = 55 表示已经申请了, 56表示还未申请
	 * @param userId
	 * @return
	 */
	public List<S_PromotionSeatBean> getMySeats(long userId){
		//获取已经获得的推广位
		List<S_PromotionSeatBean> promotionSeatBeans = promotionSeatMapper.myPromotions(userId);
		//获取已经申请的推广位
		List<S_PromotionSeatApplyBean> applyBeans  = promotionSeatApplyMapper.applyList(userId);
		EnumUtil.ProductPlatformType[] types = EnumUtil.ProductPlatformType.values();
		List<S_PromotionSeatBean> seats = new ArrayList<>(); //构建最终输出页面的列表
		for(EnumUtil.ProductPlatformType type: types){
			//自营在这里不需要处理
			if(type == EnumUtil.ProductPlatformType.系统自营)
				continue;

			//获取当前需要处理的平台
			String platform = type.value;
			S_PromotionSeatBean seat = new S_PromotionSeatBean();
			if(CollectionUtil.isNotEmpty(promotionSeatBeans)) {
				boolean hasOauth = false;//标记是否已经授权绑定啦
				int index = 0;
				for (S_PromotionSeatBean seatBean : promotionSeatBeans) {
					if(seatBean.getPlatform().equalsIgnoreCase(platform)){
						hasOauth = true;
						break;
					}
					index = index + 1;
				}
				if(hasOauth){
					seat = promotionSeatBeans.get(index);
				}else{
					//遍历申请列表
					if(CollectionUtil.isNotEmpty(applyBeans)){
						boolean in = false;
						for(S_PromotionSeatApplyBean applyBean: applyBeans){
							if(applyBean.getPlatform().equalsIgnoreCase(platform)){
								in = true;
								break;
							}
						}
						if(in){
							seat.setStatus(55);
						}else{
							seat.setStatus(56);
						}
					}else{
						seat.setStatus(56);
					}
				}
			}else{
				//不存在的情况下
				//遍历申请列表
				if(CollectionUtil.isNotEmpty(applyBeans)){
					boolean in = false;
					for(S_PromotionSeatApplyBean applyBean: applyBeans){
						if(applyBean.getPlatform().equalsIgnoreCase(platform)){
							in = true;
							break;
						}
					}
					if(in){
						seat.setStatus(55);
					}else{
						seat.setStatus(56);
					}
				}else{
					seat.setStatus(56);
				}
			}
			seat.setPlatform(platform);
			seats.add(seat);
		}
		return seats;

	}

}
