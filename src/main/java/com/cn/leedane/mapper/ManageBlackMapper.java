package com.cn.leedane.mapper;

import com.cn.leedane.model.ManageBlackBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 黑名单记录mapper接口类
 * @author LeeDane
 * 2020年4月11日 下午9:39:24
 * Version 1.0
 */
public interface ManageBlackMapper extends BaseMapper<ManageBlackBean>{
	/**
	 * 获取单条黑名单记录
	 * @param userId 用户ID
	 * @return
	 */
	public ManageBlackBean get(@Param("userId") long userId);


	/**
	 * 分页获取黑名单记录列表
	 * @param userId 用户ID
	 * @return
	 */
	public List<Map<String, Object>> blacks(@Param("userId")long userId, @Param("start")int start, @Param("limit")int rows);
}
