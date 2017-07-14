package com.cn.leedane.service.circle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CirclePostBean;

/**
 * 帖子的Service类
 * @author LeeDane
 * 2017年5月30日 下午8:12:53
 * version 1.0
 */
@Transactional
public interface CirclePostService <T extends IDBean>{

	/**
	 * 写帖子
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> add(int circleId, JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 更新帖子
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(int circleId, JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 分页获取圈子的帖子列表
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> paging(int circleId, JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 评论帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> comment(int circleId, int postId, JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 转发帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> transmit(int circleId, int postId, JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 点赞帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> zan(int circleId, int postId, JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除帖子
	 * @param circleId
	 * @param postId
	 * @param jsonFromMessage
	 * @param userFromMessage
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int circleId, int postId, JSONObject jsonFromMessage, UserBean userFromMessage, HttpServletRequest request);
	
	/**
	 * 帖子详情初始化操作
	 * @param circle
	 * @param post
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> initDetail(CircleBean circle, CirclePostBean post, UserBean user, HttpServletRequest request);

	/**
	 * 保存帖子的访问记录
	 * @param postId
	 * @param user
	 * @param request
	 */
	public void saveVisitLog(int postId, UserBean user,
			HttpServletRequest request);
	
	/**
	 * 获取等待审核的帖子总数
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> noCheckTotal(int circleId, JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取等待审核帖子列表
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> noCheckList(int circleId, JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 审核帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> check(int circleId, int postId, JSONObject json, UserBean user, HttpServletRequest request);
}
