package com.cn.leedane.service;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
/**
 * 分类管理的Service类
 * @author LeeDane
 * 2017年6月28日 下午4:51:27
 * version 1.0
 */
@Transactional
public interface CategoryService<T extends IDBean>{

	/**
	 * 添加分类
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> add(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取直接下一级的全部节点(注意，只获取直接一级)
	 * @param isAdmin
	 * @param pid
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> children(boolean isAdmin, int pid, UserBean user, HttpServletRequest request);

	/**
	 * 修改节点
	 * @param isAdmin 当前登录的用户是否是管理员
	 * @param cid
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(boolean isAdmin, int cid, JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除节点以及其全部子节点
	 * @param isAdmin 当前登录的用户是否是管理员
	 * @param cid 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(boolean isAdmin, int cid, UserBean user, HttpServletRequest request);
}
