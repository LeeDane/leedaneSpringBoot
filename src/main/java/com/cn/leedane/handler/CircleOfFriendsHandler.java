package com.cn.leedane.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.model.TimeLineBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.JsonUtil;

/**
 * 我的朋友圈列表
 * 注：通过叠加的方式统计我的朋友圈列表
 * @author LeeDane
 * 2016年7月12日 上午11:53:56
 * Version 1.0
 */
@Component
public class CircleOfFriendsHandler {
	
	/**
	 * 没有数据的时候添加的记录的标记
	 */
	private static final String NO_TIME_LINE = "notimeline";
	
	@Autowired
	private FanHandler fanHandler;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	/**
	 * 把我发表的动态更新我所有粉丝(包括自己)的TimeLine
	 * @param mood
	 * @param userId
	 * @return
	 */
	public boolean upDateMyAndFansTimeLine(TimeLineBean timeLineBean){
		//找到该用户(粉丝列表)
		Set<String> ids = fanHandler.getMyFans(timeLineBean.getCreateUserId());
		
		//先更新个人的时间线
		addTimeLine(timeLineBean.getCreateUserId(), timeLineBean);
		if(ids != null && ids.size() > 0)
			for(String id: ids){
				//循环更新其他粉丝的时间线
				addTimeLine(Integer.parseInt(id), timeLineBean);
			}
		return true;
	}
	
	/**
	 * 删除我和我的粉丝关于该TimeLine的记录
	 * @param timeLineBean
	 * @return
	 */
	public boolean deleteMyAndFansTimeLine(UserBean user, String tableName, int tableId){
		//找到该用户(粉丝列表)
		Set<String> ids = fanHandler.getMyFans(user.getId());
		
		//先删除自己的时间线
		deleteTimeLine(user.getId(), user.getId(), tableName, tableId);
		
		if(ids != null && ids.size() > 0)
			for(String id: ids){
				//循环删除其他粉丝的时间线
				deleteTimeLine(Integer.parseInt(id), user.getId(), tableName, tableId);
			}
		return true;
	}
	
	/**
	 * 删除单人的时间线的某条记录
	 * @param toUserId
	 * @param createUserId
	 * @param tableName
	 * @param tableId
	 */
	private void deleteTimeLine(int toUserId, int createUserId, String tableName, int tableId){
		String timeLineKey = getTimeLineKey(toUserId);
		if(redisUtil.hasKey(timeLineKey)){
			Set<String> set = redisUtil.getLimit(false, timeLineKey, 0, -1);
			if(set !=null && set.size() >0){
				if(set.contains(NO_TIME_LINE)){
					set.remove(NO_TIME_LINE);
				}
				Map<Double, String> scoreMembers = new HashMap<Double, String>();
				JSONObject jsonObject = null;
				long count = 1;
				for(String str: set){
					//找不到该条记录
					jsonObject = JSONObject.fromObject(str);
					if(JsonUtil.getStringValue(jsonObject, "tableName").equalsIgnoreCase(tableName)
							&& JsonUtil.getIntValue(jsonObject, "tableId") == tableId
							&& JsonUtil.getIntValue(jsonObject, "createUserId") == createUserId){
						continue;
					}else{
						scoreMembers.put((double)count, str);
						count++;
					}
				}
				
				if(scoreMembers.isEmpty()){
					scoreMembers.put((double)1, NO_TIME_LINE);
				}
				redisUtil.delete(timeLineKey);
				redisUtil.zadd(timeLineKey, scoreMembers);
			}
		}
	}
	
	/**
	 * 更新单人的时间线
	 * @param toUserId
	 * @param timeLineBean
	 */
	private void addTimeLine(int toUserId, TimeLineBean timeLineBean){
		Map<Double, String> scoreMembers = new HashMap<Double, String>();
		long count = 1;
		String timeLineKey = getTimeLineKey(toUserId);
		if(redisUtil.hasKey(timeLineKey)){
			Set<String> set = redisUtil.getLimit(false, timeLineKey, 0, -1);
			if(set !=null && set.size() >0){
				if(set.contains(NO_TIME_LINE)){
					set.remove(NO_TIME_LINE);
				}
				for(String str: set){
					scoreMembers.put((double)count, str);
					count++;
				}
				if(scoreMembers.isEmpty()){
					scoreMembers.put((double)1, NO_TIME_LINE);
				}
				redisUtil.delete(timeLineKey);
				scoreMembers.put((double)(count+1), JSONObject.fromObject(timeLineBean).toString());
				redisUtil.zadd(timeLineKey, scoreMembers);
			}
		}else{
			System.out.println("该用户还没有缓存时间线信息");
			scoreMembers.put((double)count, JSONObject.fromObject(timeLineBean).toString());
			redisUtil.zadd(timeLineKey, scoreMembers);
		}
		
	}
	
	/**
	 * 获取用户的时间线
	 * @param toUserId
	 * @param start
	 * @param end
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMyTimeLines(int toUserId, int start, int end){
		List<Map<String, Object>> timeLineBeans = new ArrayList<Map<String,Object>>();
		String timeLineKey = getTimeLineKey(toUserId);
		Set<String> set;
		if(redisUtil.hasKey(timeLineKey)){
			set = redisUtil.getLimit(true, timeLineKey, start, end);
			if(set != null){
				if(set.contains(NO_TIME_LINE)){
					set.remove(NO_TIME_LINE);
				}
				for(String str: set){
					JSONObject jsonObject = JSONObject.fromObject(str);
					timeLineBeans.add((Map<String, Object>)jsonObject);
				}
			}
		}else{
			System.out.println("我的时间线还没有在redis中缓存,现在给它加入一个空的test");
		}
		
		return timeLineBeans;
	}
	/**
	 * 把我发表的心情更新我个人的TimeLine
	 * @param mood
	 * @param userId
	 * @return
	 */
	/*public boolean upDateMySelfTimeLine(TimeLineBean timeLineBean, int userId){
		List<String> list;
		String timeLineSelfKey = getTimeLineSelfKey(userId);
		if(redisUtil.hasKey(timeLineSelfKey)){
			list = redisUtil.getList(timeLineSelfKey);
		}else{
			list = new ArrayList<String>();
			//List<Str>
			List<Map<String, Object>> ls;
			//查找数据库中个人的数据
			String hql = null;
			//1.先查心情表
			hql = "select * from "+DataTableType.心情.value +" where create_user_id="+userId+" and status="+ConstantsUtil.STATUS_NORMAL+" order by id desc";
			//ls = moodService.executeHQL("BlogBean", hql);
			//2.查博客表
			
			//3.查评论表
			
			//4.查转发表
			
			//5.查赞表
		}
		//list.add(mood.toString());
		redisUtil.addList(timeLineSelfKey, list);
		return true;
	}*/
	
	/**
	 * 分别合并双方的timeline
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	/*public boolean mergeTimeLine(int userId, int toUserId){
		String myTimeLineKey = getTimeLineKey(userId);
		String toTimeLineKey = getTimeLineKey(toUserId);
		List<String> myList = new ArrayList<String>();
		List<String> toList = new ArrayList<String>();
		//更新我的时间线
		if(redisUtil.hasKey(myTimeLineKey)){
			myList = redisUtil.getList(myTimeLineKey);
		}
		
		//更新对方的时间线
		if(redisUtil.hasKey(toTimeLineKey)){
			toList = redisUtil.getList(toTimeLineKey);
		}
		
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		if(myList == null || myList.size() ==0){
			StringBuffer sql = new StringBuffer();
			sql.append("select m.id, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img,");
			sql.append(" m.read_number, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
			sql.append(" m.create_user_id = ?");
			sql.append(" order by m.id desc");
			//rs = moodService.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, userId);
			merge(rs, toList, toTimeLineKey);
		}
		
		return true;
	}*/

	/**
	 * 将List和数据进行合并
	 * @param rs
	 * @param toList
	 * @param timeLineKey
	 */
	/*private void merge(List<Map<String, Object>> rs, List<String> toList, String timeLineKey) {
		if(rs == null || rs.size() == 0){
			return;
		}
		
		if(toList == null || toList.size() == 0){
			toList = new ArrayList<String>();
			for(Map<String, Object> map: rs){
				toList.add(JSONObject.fromObject(map).toString());
			}
		}else{
			
		}
		
		redisUtil.addList(timeLineKey, toList);
	}*/

	/**
	 * 获取个人时间线的key(包括好友)
	 * @param userId
	 * @return
	 */
	public static String getTimeLineKey(Integer userId) {
		return ConstantsUtil.TIME_LINE +userId;
	}
	
	/**
	 * 获取个人时间线的key(只有个人)
	 * @param userId
	 * @return
	 */
	public static String getTimeLineSelfKey(Integer userId) {
		return ConstantsUtil.TIME_LINE_SELF +userId;
	}
}
