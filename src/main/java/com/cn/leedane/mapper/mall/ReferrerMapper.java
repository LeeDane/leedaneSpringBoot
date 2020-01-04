package com.cn.leedane.mapper.mall;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_ReferrerBean;
import org.apache.ibatis.annotations.Param;

/**
 * 推荐人mapper接口类
 * @author LeeDane
 * 2017年12月7日 下午11:33:02
 * version 1.0
 */
public interface ReferrerMapper extends BaseMapper<S_ReferrerBean>{

	/**
	 * 获取用户的推荐码
	 * @param userId
	 */
	public S_ReferrerBean findReferrerCode(@Param("userId") long userId);

	/**
	 * 根据推荐码找到用户信息
	 * @param code
	 */
	public S_ReferrerBean findReferrerByCode(@Param("code")String code);

}
