package com.cn.leedane.service.clock;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 任务成员关系处理的Service类
 * @author LeeDane
 * 2018年10月23日 下午2:37:52
 * version 1.0
 */
@Transactional
public interface ClockDealService <T extends IDBean>{
	/**
	 * 添加任务成员关系
	 * @param clockId
	 * @param memberId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(int clockId, int memberId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑任务关系
	 * @param clockId
	 * @param memberId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(int clockId, int memberId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除任务关系
	 * @param clockId
	 * @param memberId
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int clockId, int memberId, UserBean user, HttpRequestInfoBean request);

	/**
	 * 请求加入对方的任务(必须是共享的任务，并且人数没有超过共享人数，时间不能超过报名时间)
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> requestAdd(int clockId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);

	/**
	 * 同意某人加入任务(必须是共享的任务，并且人数没有超过共享人数，时间不能超过报名时间)
	 * @param clockId
	 * @param memberId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> requestAgree(int clockId, int memberId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 邀请对方的加入任务(必须是共享的任务，并且人数没有超过共享人数，时间不能超过报名时间)
	 * @param clockId
	 * @param memberId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> inviteAdd(int clockId, int memberId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);

	/**
	 * 同意某人的邀请任务(必须是共享的任务，并且人数没有超过共享人数，时间不能超过报名时间)
	 * @param clockId
	 * @param memberId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> inviteAgree(int clockId, int memberId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 获取我的请求加入任务的列表
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addClocks(
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 获取邀请我加入的任务的列表
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> inviteClocks(
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 获取我邀请的任务的列表
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> myInviteClocks(
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 获得等待我的同意的任务列表
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> agreeClocks(
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
}	
