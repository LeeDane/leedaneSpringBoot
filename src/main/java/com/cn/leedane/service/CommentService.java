package com.cn.leedane.service;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
/**
 * 评论的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:32:11
 * Version 1.0
 */
@Transactional
public interface CommentService<T extends IDBean>{

	/**
	 * 获取当前用户的总评论数
	 * @param userId
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTotalComments(int userId);
	
	/**
	 * 基础的保存实体的方法
	 * @param t
	 * @return
	 */
	public boolean save(CommentBean t);
	
	/**
	 * 发表评论
	 * {\"table_name\":\"t_mood\", \"table_id\":123, \"content\":\"我同意\"
	 * ,\"level\": 1, \"pid\":23, \"cid\":1, \"from\":\"Android客户端\"}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> add(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 获取分页的评论
	 * {\"table_name\":\"t_mood\", \"table_id\":123,\"pageSize\":5
	 * , \"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getCommentsByLimit(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 获得当个评论的所有子评论列表
	 *  {\"table_name\":\"t_mood\",\"cid\":1, \"table_id\":123,\"pageSize\":5
	 * , \"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getOneCommentItemsByLimit(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 获取每个评论对象所有的评论数
	 * {\"table_name\":\"t_mood\", \"table_id\":123}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getCountByObject(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 获取每个用户的评论数量
	 *  {\"uid\":1}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getCountByUser(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 删除评论
	 * {"cid":1, "create_user_id":1}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteComment(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 更改评论编辑状态
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateCommentStatus(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 执行SQL对应字段的List<Map<String,Object>
	 * @param sql sql语句,参数直接写在语句中，存在SQL注入攻击de风险，慎用
	 * @param params ?对应的值
	 * @return
	 */
	public List<Map<String, Object>> executeSQL(String sql, Object ...params);
	
	/**
	 * 获取总数
	 * @param tableName  表名
	 * @param where where后面语句，参数需直接填写在字符串中
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTotal(String tableName, String where);
	
	
	/**
	 * 获取留言列表
	 * @param userId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getMessageBoards(int userId, JSONObject jo, UserBean user, HttpServletRequest request);
	
}
