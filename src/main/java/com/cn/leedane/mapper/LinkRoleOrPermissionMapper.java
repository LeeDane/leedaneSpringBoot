package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.LinkRoleOrPermissionBean;

/**
 * 链接角色获取权限管理mapper接口类
 * @author LeeDane
 * 2017年4月20日 下午5:51:45
 * Version 1.0
 */
public interface LinkRoleOrPermissionMapper extends BaseMapper<LinkRoleOrPermissionBean>{
	/**
	 * 批量添加
	 * @param data
	 */
	public void insertByBatch(List<Map<String, Object>> data);
	
	/**
	 * 根据链接id获取其对应分配的用户id
	 * @return
	 */
	public List<Map<String, Object>> getUsersByLinkId(@Param("lnid")long lnid);
	
	/**
	 * 根据权限ids获取其对应分配的用户id
	 * @return
	 */
	public List<Map<String, Object>> getUsersByLinkIds(@Param("lnids")long[] lnids);
}
