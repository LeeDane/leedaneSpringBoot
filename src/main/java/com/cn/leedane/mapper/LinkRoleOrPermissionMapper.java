package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import com.cn.leedane.model.LinkRoleOrPermissionBean;

/**
 * 链接角色获取权限管理mapper接口类
 * @author LeeDane
 * 2017年4月20日 下午5:51:45
 * Version 1.0
 */
public interface LinkRoleOrPermissionMapper extends BaseMapper<LinkRoleOrPermissionBean>{
	public void insertByBatch(List<Map<String, Object>> data);
}
