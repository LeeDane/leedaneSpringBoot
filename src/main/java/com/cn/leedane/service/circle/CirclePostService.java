package com.cn.leedane.service.circle;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	public ResponseModel add(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 更新帖子
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel update(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 分页获取圈子的帖子列表
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel paging(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 评论帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel comment(long circleId, long postId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 转发帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel transmit(long circleId, long postId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 点赞帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel zan(long circleId, long postId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除帖子
	 * @param circleId
	 * @param postId
	 * @param jsonFromMessage
	 * @param userFromMessage
	 * @param request
	 * @return
	 */
	public ResponseModel delete(long circleId, long postId, JSONObject jsonFromMessage, UserBean userFromMessage, HttpRequestInfoBean request);
	
	/**
	 * 帖子详情初始化操作
	 * @param circle
	 * @param post
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> initDetail(CircleBean circle, CirclePostBean post, UserBean user, HttpRequestInfoBean request);

	/**
	 * 保存帖子的访问记录
	 * @param postId
	 * @param user
	 * @param request
	 */
	public void saveVisitLog(long postId, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 获取等待审核的帖子总数
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel noCheckTotal(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取等待审核帖子列表
	 * @param circleId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel noCheckList(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 审核帖子
	 * @param circleId
	 * @param postId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel check(long circleId, long postId, JSONObject json, UserBean user, HttpRequestInfoBean request);
}
