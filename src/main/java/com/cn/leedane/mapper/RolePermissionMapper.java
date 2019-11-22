package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.RolePermissionBean;

/**
 * 权限角色关系管理mapper接口类
 * @author LeeDane
 * 2017年4月18日 下午3:11:28
 * version 1.0
 */
public interface RolePermissionMapper extends BaseMapper<RolePermissionBean>{
	/**
	 * 批量添加
	 * @param data
	 */
	public void insertByBatch(List<Map<String, Object>> data);
	
	/**
	 * 根据权限id获取其对应分配的用户id
	 * @param pmid
	 * @return
	 */
	public List<Map<String, Object>> getUsersByPermissionId(@Param("pmid")long pmid);
	
	/**
	 * 根据权限ids获取其对应分配的用户id
	 * @param pmids
	 * @return
	 */
	public List<Map<String, Object>> getUsersByPermissionIds(@Param("pmids")long[] pmids);
}
