package com.cn.leedane.service.circle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;

/**
 * 圈子的Service类
 * @author LeeDane
 * 2017年5月30日 下午8:12:53
 * version 1.0
 */
@Transactional
public interface CircleService <T extends IDBean>{
	
	/**
	 * 初始化操作
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> init(UserBean user, HttpServletRequest request);
	
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
	 * 删除圈子
	 * @param cid 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int cid, UserBean user, HttpServletRequest request);

	/**
	 * 检查是否能创建圈子
	 * @param jsonFromMessage
	 * @param userFromMessage
	 * @param request
	 * @return
	 */
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> check(JSONObject json, UserBean user, HttpServletRequest request);
	
	 /**
	 * 分页获取任务列表
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 查找圈子
	 * @param cid
	 * @return
	 */
	public CircleBean findById(int cid);
}
