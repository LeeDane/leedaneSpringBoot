package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import com.cn.leedane.model.UserBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.RoleBean;

/**
 * 用户角色service接口类
 * @author LeeDane
 * 2017年4月10日 上午10:29:18
 * version 1.0
 */
@Transactional
public interface RolePermissionService<T extends IDBean>{
	
	/**
	 * 获取该用户的全部角色
	 * @param userid
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<RoleBean> getUserRoleBeans(int userid);

	/**
	 * 通过角色名称获取该角色的授权用户列表
	 * @param roleId
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getUserByRoleBeans(int roleId);
}
