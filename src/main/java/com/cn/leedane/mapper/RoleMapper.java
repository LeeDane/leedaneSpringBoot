package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.RoleBean;

/**
 * 用户角色mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:21:30
 * Version 1.0
 */
public interface RoleMapper extends BaseMapper<RoleBean>{
	public List<Map<String, Object>> users(@Param("rlid")int rlid);
}
