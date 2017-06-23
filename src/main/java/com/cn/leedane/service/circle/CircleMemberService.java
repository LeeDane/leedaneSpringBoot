package com.cn.leedane.service.circle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 圈子成员的Service类
 * @author LeeDane
 * 2017年6月20日 下午2:43:48
 * version 1.0
 */
@Transactional
public interface CircleMemberService <T extends IDBean>{
	 /**
	 * 分页获取成员列表
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(int circleId, JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 推荐/取消推荐(必须是圈主或者圈子管理员才能操作)
	 * @param circleId
	 * @param memberId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> recommend(int circleId, int memberId, JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除某个成员(必须是圈主或者圈子管理员才能操作)，圈主不能删除
	 * @param circleId
	 * @param memberId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int circleId, int memberId, JSONObject json, UserBean user, HttpServletRequest request);
	
}
