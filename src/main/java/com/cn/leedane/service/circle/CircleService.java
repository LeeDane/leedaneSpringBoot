package com.cn.leedane.service.circle;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> init(UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 单个圈子的初始化
	 * @param circle
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String,Object> main(CircleBean circle, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 圈子成员列表的初始化
	 * @param circle
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String,Object> memberListInit(CircleBean circle, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 创建圈子
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> create(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑圈子
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(int circleId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除圈子
	 * @param cid 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int cid, UserBean user, HttpRequestInfoBean request);

	/**
	 * 检查是否能创建圈子
	 * @param jsonFromMessage
	 * @param userFromMessage
	 * @param request
	 * @return
	 */
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> check(JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	 /**
	 * 分页获取任务列表
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 对是否能申请加入圈子的检查
	 * @param circleId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> joinCheck(int circleId, UserBean user, HttpRequestInfoBean request);

	/**
	 * 申请加入圈子
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> join(int circleId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 保存访问圈子记录
	 * @param circleId
	 * @param user
	 * @param request
	 */
	public void saveVisitLog(int circleId, UserBean user, HttpRequestInfoBean request);

	/**
	 * 退出该圈子
	 * @param circleId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> leave(int circleId, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取最新的圈子
	 * @param user
	 * @param limit 最多展示多少
	 * @param request
	 * @return
	 *//*
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<CircleBean> newest(UserBean user, int limit, HttpRequestInfoBean request);

	*//**
	 * 获取最热门的圈子
	 * @param user
	 * @param limit 最多展示多少
	 * @param request
	 * @return
	 *//*
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<CircleBean> hotest(UserBean user, int limit, HttpRequestInfoBean request);*/
	
	/**
	 * 获取已经分配的管理员列表
	 * @param cid
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> admins(int cid, UserBean user, HttpRequestInfoBean request);

	
	/**
	 * 圈子成员做分配管理员操作
	 * @param cid
	 * @param admins
	 * @param userFromMessage
	 * @param request
	 * @return
	 */
	public Map<String, Object> allot(int cid, String admins, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取圈子首页的初始化
	 * @param cid
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> initialize(int cid,
			UserBean userFromMessage, HttpRequestInfoBean request);
}
