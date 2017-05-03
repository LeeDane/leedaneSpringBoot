package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.RoleBean;
import com.cn.leedane.model.UserRoleBean;

/**
 * 用户角色关系的mapper接口类
 * @author LeeDane
 * 2017年4月10日 上午10:34:09
 * version 1.0
 */
public interface UserRoleMapper  extends BaseMapper<UserRoleBean>{
	
	public List<RoleBean> getUserRoleBeans(
				@Param("userId") int userId, @Param("status") int status);
	
	/**
	 * 批量添加
	 * @param data
	 */
	public void insertByBatch(List<Map<String, Object>> data);
	
	/**
	 * 根据角色id获取其对应分配的用户id
	 * @param pmid
	 * @return
	 */
	public List<Map<String, Object>> getUsersByRoleId(@Param("rlid")int rlid);
	
	/**
	 * 根据角色ids获取其对应分配的用户id
	 * @param pmids
	 * @return
	 */
	public List<Map<String, Object>> getUsersByRoleIds(@Param("rlids")int[] rlids);
}
