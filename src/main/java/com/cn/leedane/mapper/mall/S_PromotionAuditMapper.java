package com.cn.leedane.mapper.mall;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_WishBean;
import com.cn.leedane.model.mall.promotion.S_PromotionAuditBean;

/**
 * 推广审核信息mapper接口类
 * @author LeeDane
 * 2018年3月26日 下午5:04:15
 * version 1.0
 */
public interface S_PromotionAuditMapper extends BaseMapper<S_WishBean>{
	/**
	 * 获取推广用户的信息
	 * @param userId
	 * @return
	 */
	public S_PromotionAuditBean getAudit(@Param("userId") long userId, @Param("status") int status);
	
}
