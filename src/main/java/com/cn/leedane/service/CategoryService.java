package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
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
	 */
	public ResponseModel add(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取直接下一级的全部节点(注意，只获取直接一级)
	 * @param isAdmin
	 * @param pid
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel children(boolean isAdmin, long pid, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取该节点以及其上所有直接节点
	 * @param pid
	 * @return
	 */
	public ResponseModel mallCategory(int pid);
	
	/**
	 * 修改节点
	 * @param isAdmin 当前登录的用户是否是管理员
	 * @param cid
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel update(boolean isAdmin, long cid, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除节点以及其全部子节点
	 * @param isAdmin 当前登录的用户是否是管理员
	 * @param cid 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public ResponseModel delete(boolean isAdmin, long cid, UserBean user, HttpRequestInfoBean request);
}
