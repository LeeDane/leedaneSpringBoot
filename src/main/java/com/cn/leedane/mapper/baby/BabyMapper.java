package com.cn.leedane.mapper.baby;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.baby.BabyBean;

/**
 * 宝宝mapper接口类
 * @author LeeDane
 * 2018年6月5日 下午4:03:42
 * version 1.0
 */
public interface BabyMapper extends BaseMapper<BabyBean>{
	/**
	 * 获取用户所有的宝宝
	 * @param userId
	 * @return
	 */
	public List<BabyBean> getMyBabys(@Param("createUserId") long userId, @Param("status") int status);
}
