package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 权限管理service接口类
 * @author LeeDane
 * 2017年4月17日 下午2:24:08
 * version 1.0
 */
@Transactional
public interface PermissionService <T extends IDBean>{
	
	/**
	 * 添加权限
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> save(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑权限
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> edit(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除权限
	 * @param pmid
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int pmid, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 分页获取权限列表
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 批量删除权限
	 * @param pmids
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deletes(String pmids, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 根据权限ID获取角色列表
	 * @param pmid
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> roles(int pmid, UserBean user, HttpRequestInfoBean request);

	/**
	 * 给角色分配权限
	 * @param pmid
	 * @param roles
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> allot(int pmid,String roles, UserBean user, HttpRequestInfoBean request);
}
