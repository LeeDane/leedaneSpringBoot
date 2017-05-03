package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.UserBean;

/**
 * 链接权限管理service接口类
 * @author LeeDane
 * 2017年4月10日 下午4:49:16
 * version 1.0
 */
@Transactional
public interface LinkManageService<LinkManageBean>{
	
	/**
	 * 获取所有的链接(包括正常状态和非正常状态)
	 * @param user
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<LinkManageBean> getAllLinks();
	
	/**
	 * 添加链接
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> save(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 编辑链接
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> edit(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除链接
	 * @param lnid
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int lnid, UserBean user, HttpServletRequest request);
	
	/**
	 * 分页获取链接列表
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 批量删除链接
	 * @param lnids
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deletes(String lnids, UserBean user, HttpServletRequest request);
	
	/**
	 * 根据链接ID获取权限或者角色列表
	 * @param lnid
	 * @param role
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> roleOrPermissions(int lnid, boolean role, UserBean user, HttpServletRequest request);

	/**
	 * 角色、权限的分配
	 * @param lnid
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> allot(int lnid,JSONObject json, UserBean user, HttpServletRequest request);
}
