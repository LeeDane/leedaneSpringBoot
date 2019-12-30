package com.cn.leedane.mapper.mall;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_PromotionSeatApplyBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推广位申请记录mapper接口类
 * @author LeeDane
 * 2019年12月7日 下午11:33:02
 * version 1.0
 */
public interface PromotionSeatApplyMapper extends BaseMapper<S_PromotionSeatApplyBean>{
	/**
	 * 用户推广位的申请列表
	 * @param userId
	 * @return
	 */
	public List<S_PromotionSeatApplyBean> applyList(@Param("userId") long userId);

	/**
	 * 获取申请记录
	 * @param userId
	 * @param platform
	 * @return
	 */
	public S_PromotionSeatApplyBean getApply(@Param("userId")long userId, @Param("platform")String platform);
}
