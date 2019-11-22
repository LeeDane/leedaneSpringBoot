package com.cn.leedane.service.circle;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 推荐/取消推荐(必须是圈主或者圈子管理员才能操作)
	 * @param circleId
	 * @param memberId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> recommend(long circleId, long memberId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除某个成员(必须是圈主或者圈子管理员才能操作)，圈主不能删除
	 * @param circleId
	 * @param memberId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(long circleId, long memberId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
}
