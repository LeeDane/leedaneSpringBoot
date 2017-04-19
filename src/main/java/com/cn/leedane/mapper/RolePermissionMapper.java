package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import com.cn.leedane.model.RolePermissionBean;

/**
 * 权限角色关系管理mapper接口类
 * @author LeeDane
 * 2017年4月18日 下午3:11:28
 * version 1.0
 */
public interface RolePermissionMapper extends BaseMapper<RolePermissionBean>{
	public void insertByBatch(List<Map<String, Object>> data);
}
