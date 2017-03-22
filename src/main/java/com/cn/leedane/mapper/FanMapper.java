package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.FanBean;

/**
 * 粉丝mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:10:24
 * Version 1.0
 */
public interface FanMapper extends BaseMapper<FanBean>{

	/**
	 * 判断两人是否是互粉关系
	 * @param id  当前用户的id
	 * @param to_user_id  对方用户的id
	 * @return
	 */
	public List<Map<String, Object>> isFanEachOther(@Param("id")int id, @Param("toUserId")int to_user_id, @Param("status")int status);
	
}
