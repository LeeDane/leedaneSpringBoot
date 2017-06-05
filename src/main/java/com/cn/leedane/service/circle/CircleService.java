package com.cn.leedane.service.circle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 圈子的Service类
 * @author LeeDane
 * 2017年5月30日 下午8:12:53
 * version 1.0
 */
public interface CircleService <T extends IDBean>{
	
	/**
	 * 创建圈子
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> create(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 编辑圈子
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 编辑圈子
	 * @param cid 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int cid, UserBean user, HttpServletRequest request);
}
