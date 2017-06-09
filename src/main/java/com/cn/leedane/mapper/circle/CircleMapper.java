package com.cn.leedane.mapper.circle;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CircleBean;

/**
 * 圈子mapper接口类
 * @author LeeDane
 * 2017年5月30日 下午8:15:58
 * version 1.0
 */
public interface CircleMapper extends BaseMapper<CircleBean>{
	/**
	 * 获取用户左右的圈子
	 * @param userId
	 * @return
	 */
	public List<CircleBean> getAllCircles(@Param("createUserId") int userId, @Param("status") int status);
}
