package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.FriendBean;

/**
 * 好友关系mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:11:33
 * Version 1.0
 */
public interface FriendMapper extends BaseMapper<FriendBean>{
	/**
	 * 根据用户id删除指定好友信息
	 * @param uid
	 * @param friends
	 * @return
	 */
	//public boolean deleteFriends(int uid, int ... friends);

	/**
	 * 判断两人是否是朋友的关系（判断两人的正式好友记录）
	 * @param id  当前用户的id
	 * @param to_user_id  对方用户的id
	 * @return
	 */
	public List<Map<String, Object>> isFriend(@Param("id")long id, @Param("toUserId")long to_user_id, @Param("status")int status);
	
	/**
	 * 判断两人是否是朋友的关系（包括一方申请还没有同意）
	 * @param id  当前用户的id
	 * @param to_user_id  对方用户的id
	 * @return
	 */
	public List<Map<String, Object>> isFriendRecord(@Param("id")long id, @Param("toUserId")long to_user_id);
	
}
