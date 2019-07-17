package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 好友关系service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:33:20
 * Version 1.0
 */
@Transactional
public interface FriendService <T extends IDBean>{
	
	/**
	 * 添加朋友的关系(单方面添加，需要等待对方确认)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addFriend(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 同意对方的好友申请
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addAgree(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	
	/**
	 * 根据关系id删除好友关系信息
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteFriends(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 判断两人是否是朋友的关系（判断两人的正式好友记录）
	 * @param id  当前用户的id
	 * @param to_user_id  对方用户的id
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean isFriend(int id, int to_user_id);
	
	/**
	 * 判断两人是否是朋友的关系（包括一方申请还没有同意）
	 * @param id  当前用户的id
	 * @param to_user_id  对方用户的id
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean isFriendRecord(int id, int to_user_id);

	/**
	 * 获取已经跟我成为好友关系的分页列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> friendsAlreadyPaging(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取还未跟我成为好友关系的用户(我发起对方未答应或者对方发起我还未答应的)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> friendsNotyetPaging(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取我发送的好友请求列表(我加别人的)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> requestPaging(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取等待我同意的好友关系列表(别人加我的)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> responsePaging(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 用户本地联系人跟服务器上的好友进行匹配后返回
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> matchContact(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取全部已经跟我成为好友关系的列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> friends(JSONObject jo, UserBean user, HttpRequestInfoBean request);
}
