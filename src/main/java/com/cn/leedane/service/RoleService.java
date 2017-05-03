package com.cn.leedane.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 角色service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:34:59
 * Version 1.0
 */
@Transactional
public interface RoleService <T extends IDBean>{
	/**
	 * 添加角色
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> save(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 编辑角色
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> edit(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除角色
	 * @param rlid
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int rlid, UserBean user, HttpServletRequest request);
	
	/**
	 * 分页获取角色列表
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 批量删除角色
	 * @param rlids
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deletes(String rlids, UserBean user, HttpServletRequest request);
	
	/**
	 * 根据角色ID获取用户列表
	 * @param rlid
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> users(int rlid, UserBean user, HttpServletRequest request);

	/**
	 * 给用户分配角色
	 * @param rlid
	 * @param users
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> allot(int rlid,String users, UserBean user, HttpServletRequest request);
}
