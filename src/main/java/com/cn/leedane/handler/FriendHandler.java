package com.cn.leedane.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.FriendBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.service.FriendService;

/**
 * 好友处理类
 * @author LeeDane
 * 2016年3月21日 上午10:09:40
 * Version 1.0
 */
@Component
public class FriendHandler {
	@Autowired
	private CommentService<CommentBean> commentService;
	
	public void setCommentService(CommentService<CommentBean> commentService) {
		this.commentService = commentService;
	}
	
	@Autowired
	private FriendService<FriendBean> friendService;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	public void setFriendService(FriendService<FriendBean> friendService) {
		this.friendService = friendService;
	}
	
	@Autowired
	private CircleOfFriendsHandler circleOfFriendsHandler;
	
	public void setCircleOfFriendsHandler(
			CircleOfFriendsHandler circleOfFriendsHandler) {
		this.circleOfFriendsHandler = circleOfFriendsHandler;
	}
	
	@Autowired
	private UserHandler userHandler;
	
	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}
	

	/**
	 * 获取好友列表的json对象
	 * @param getUserId
	 * @return
	 */
	public JSONObject getFromToFriends(int getUserId){
		String friendKey = getFriendKey(getUserId);
		JSONObject friendObject = new JSONObject();
		//评论
		if(!redisUtil.hasKey(friendKey)){
			List<Map<String, Object>> friends = friendService.getFromToFriends(getUserId);
			Set<Integer> fids = new HashSet<Integer>();
			String friendIdKey = getFriendIdsKey(getUserId);
			fids.add(getUserId);
			if(friends != null && friends.size() > 0){
				int id = 0;
				for(int i =0; i < friends.size(); i++){
					id = StringUtil.changeObjectToInt(friends.get(i).get("id"));
					fids.add(id);
					friendObject.put("user_" +id, String.valueOf(friends.get(i).get("remark")));
					friendObject.put("mobile_phone", userHandler.getUserMobilePhone(id));
				}
				redisUtil.addString(friendKey, friendObject.toString());
			}
			redisUtil.addString(friendIdKey, setToString(fids));
		}else{
			String friends = redisUtil.getString(friendKey);
			if(StringUtil.isNotNull(friends)){
				friendObject = JSONObject.fromObject(friends);
			}
		}
		return friendObject;
	}
	
	/**
	 * 删除好友关系
	 * @param fromUserId
	 * @param toUserId
	 */
	public void delete(int fromUserId, int toUserId){
		redisUtil.delete(getFriendKey(fromUserId));
		redisUtil.delete(getFriendKey(toUserId));
		redisUtil.delete(getFriendIdsKey(fromUserId));
		redisUtil.delete(getFriendIdsKey(toUserId));
	}

	
	/**
	 * 将set集合转成字符串
	 * @param set
	 * @return
	 */
	private String setToString(Set<Integer> set){
		String str = "";
		if(set != null && set.size() > 0){
			StringBuffer result = new StringBuffer();
			for(Integer s: set){
				result.append(s);
				result.append(";");
			}
			
			str = result.toString().substring(0, result.toString().length() -1);
		}
		return str;
	}
	
	/**
	 * 获取该用户的列表好友列表(包括自己)
	 * @param userId
	 * @return
	 */
	public Set<Integer> getFromToFriendIds(int userId){
		String friendIdKey = getFriendIdsKey(userId);
		JSONObject friendObject = new JSONObject();
		Set<Integer> fids = new HashSet<Integer>();
		//评论
		if(!redisUtil.hasKey(friendIdKey)){
			List<Map<String, Object>> friends = friendService.getFromToFriends(userId);
			String friendKey = getFriendKey(userId);
			fids.add(userId);
			if(friends != null && friends.size() > 0){
				int id = 0;
				for(int i =0; i < friends.size(); i++){
					id = StringUtil.changeObjectToInt(friends.get(i).get("id"));
					fids.add(id);
					friendObject.put("user_" +id, String.valueOf(friends.get(i).get("remark")));
				}
				redisUtil.addString(friendKey, friendObject.toString());
			}
			redisUtil.addString(friendIdKey, setToString(fids));
		}else{
			String friendIds = redisUtil.getString(friendIdKey);
			return stringToSet(friendIds, userId);
		}
		return fids;
	}
	
	/**
	 * 添加好友成功后，更新redis两边的数据
	 * @param toUserId
	 * @param userId
	 */
	public boolean addFriends(int toUserId, int userId, String toUserRemark, String userRemark){
		JSONObject toUserObject = getFromToFriends(toUserId);
		JSONObject userObject = getFromToFriends(userId);
		String toUserKey = getFriendKey(toUserId);
		String userKey = getFriendKey(userId);
		
		String toUserFriendIdKey = getFriendIdsKey(toUserId);
		String userFriendIdKey = getFriendIdsKey(userId);
		
		//处理好友关系的id
		//更新对方的好友列表
		Set<Integer> ids = getFromToFriendIds(toUserId);
		ids.add(userId);
		redisUtil.addString(toUserFriendIdKey, setToString(ids));
			
		//更新自己的好友列表
		ids = getFromToFriendIds(userId);
		ids.add(toUserId);
		redisUtil.addString(userFriendIdKey, setToString(ids));
		
		//更新双方的timeline
		//myCircleOfFriendsHandler.mergeTimeLine(userId, toUserId);
		
		if(toUserObject == null){//还没有好友
			toUserObject = new JSONObject();
		}else{
			redisUtil.delete(toUserKey);
		}
		toUserObject.put("user_" +userId, userRemark);
		redisUtil.addString(toUserKey, toUserObject.toString());
		
		if(userObject == null){//还没有好友
			userObject = new JSONObject();
		}
		
		userObject.put("user_" +toUserId, toUserRemark);
		return redisUtil.addString(userKey, userObject.toString());
	}
	
	/**
	 * 判断toUserId是否在userId关注列表中
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	public boolean inFriend(int userId, int toUserId){
		boolean result = false;
		Set<Integer> set = getFromToFriendIds(userId);
		if(set != null && set.size() >0){
			result = set.contains(toUserId);
		}
		return result;
	}
	
	
	/**
	 * 将字符串转成数组集合
	 * @param str
	 * @param userId
	 * @return
	 */
	private Set<Integer> stringToSet(String str, int userId){
		if(StringUtil.isNull(str)){
			return new HashSet<Integer>();
		}
		Set<Integer> ids = new HashSet<Integer>();
		Object[] objs = str.split(";");
		for(int i =0; i < objs.length; i++){
			ids.add(StringUtil.changeObjectToInt(objs[i]));
		}
		return ids;
	}
	/**
	 * 获取好友id列表在redis的key(包括自己)
	 * @param id
	 * @return
	 */
	public static String getFriendIdsKey(int userId){
		return ConstantsUtil.FRIEND_ID_REDIS +userId;
	}
	
	/**
	 * 获取评论在redis的key
	 * @param id
	 * @return
	 */
	public static String getFriendKey(int userId){
		return ConstantsUtil.FRIEND_REDIS +userId;
	}
}
