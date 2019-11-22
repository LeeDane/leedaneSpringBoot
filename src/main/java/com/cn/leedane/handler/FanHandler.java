package com.cn.leedane.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.FanMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;

/**
 * 粉丝处理类
 * @author LeeDane
 * 2016年4月11日 上午11:50:40
 * Version 1.0
 */
@Component
public class FanHandler {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private FanMapper fanMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private CircleOfFriendsHandler circleOfFriendsHandler;
	
	/**
	 * 添加关注(即成为别人的粉丝)
	 * @param user
	 * @param toUser
	 */
	public void addAttention(UserBean user, UserBean toUser){
		logger.info("addAttention");
		String attentionIDKey = getAttentionIdsKey(user.getId());
		Map<Double, String> scoreMembers = new HashMap<Double, String>();
		long count = 1;
		//关注
		if(!redisUtil.hasKey(attentionIDKey)){
			String sql = "select id, to_user_id from "+DataTableType.粉丝.value+" f where status=? and create_user_id=? order by id";
			List<Map<String, Object>> list = fanMapper.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, user.getId());
			if(list != null && list.size() > 0){
				for(Map<String, Object> map: list){
					scoreMembers.put((double)count, String.valueOf(map.get("to_user_id")));
					count++;
				}
			}
		}else{
			count = redisUtil.getZSetCard(attentionIDKey);
		}
		
		scoreMembers.put((double)(count+1), String.valueOf(toUser.getId()));
		redisUtil.zadd(attentionIDKey, scoreMembers);
		
		addFan(user, toUser);
	}
	
	/**
	 * 别人关注(即成为别人关注我)
	 * @param user
	 * @param toUser
	 */
	public void addFan(UserBean user, UserBean toUser){
		logger.info("addFan");
		Map<Double, String> scoreMembers = new HashMap<Double, String>();
		long count = 1;
		String fanIDKey = getFanIdsKey(user.getId());
		scoreMembers.clear();
		//处理对方的粉丝列表
		if(!redisUtil.hasKey(fanIDKey)){
			String sql = "select id, create_user_id from "+DataTableType.粉丝.value+" f where status=? and to_user_id=? order by id";
			List<Map<String, Object>> list = fanMapper.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, user.getId());
			if(list != null && list.size() > 0){
				for(Map<String, Object> map: list){
					scoreMembers.put((double)count, String.valueOf(map.get("to_user_id")));
					count++;
				}
			}
		}else{
			count = redisUtil.getZSetCard(fanIDKey);
			scoreMembers.put((double)count, String.valueOf(toUser.getId()));
		}
		redisUtil.zadd(fanIDKey, scoreMembers);
		
		//addAttention(user, toUser);
	}
	
	/**
	 * 获取我全部关注的对象的列表
	 * @param toUserId
	 * @return
	 */
	public Set<String> getMyAttentions(long toUserId){
		return getMyAttentionsLimit(toUserId, 0, -1);
	}
	/**
	 * 获取我关注的对象的列表
	 * @param toUserId
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> getMyAttentionsLimit(long toUserId, int start, int end){
		String attentionIDKey = getAttentionIdsKey(toUserId);
		
		//关注列表不存在，先获取该对象的列表放在redis缓存中
		if(!redisUtil.hasKey(attentionIDKey)){
			Map<Double, String> scoreMembers = new HashMap<Double, String>();
			long count = 1;
			String sql = "select id, to_user_id from "+DataTableType.粉丝.value+" f where status=? and create_user_id=? order by id";
			List<Map<String, Object>> list = fanMapper.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, toUserId);
			if(list != null && list.size() > 0){
				for(Map<String, Object> map: list){
					scoreMembers.put((double)count, String.valueOf(map.get("to_user_id")));
					count++;
				}
			}	
			if(scoreMembers.isEmpty()){
				return new HashSet<String>();
			}
			redisUtil.zadd(attentionIDKey, scoreMembers);
		}
		
		return redisUtil.getLimit(true, attentionIDKey, start, end);
	}
	
	/**
	 * 取消关注(更新我的关注列表)
	 * @param userId
	 * @param toUserId
	 */
	public void cancelAttention(long userId, long toUserId){
		Map<Double, String> scoreMembers = new HashMap<Double, String>();
		long count = 1;
		String attentionIDKey = getAttentionIdsKey(userId);
		scoreMembers.clear();
		//更新我的关注列表
		if(redisUtil.hasKey(attentionIDKey)){
			count = redisUtil.getZSetCard(attentionIDKey);
			//从最新时间大到小查我的关注列表
			Set<String> set = getMyAttentions(userId);
			if(set !=  null && set.size() >0){
				if(!set.contains(String.valueOf(toUserId))){
					logger.error("竟然在关注列表中没有数据");
					return;
				}
					
				
				//先把该用户移出我的关注列表
				set.remove(String.valueOf(toUserId));
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext()){
					scoreMembers.put((double)count, iterator.next());
					count--;
				}
				//把我的关注列表缓存情况
				redisUtil.delete(attentionIDKey);
				if(!scoreMembers.isEmpty()){
					redisUtil.zadd(attentionIDKey, scoreMembers);
				}
			}
		}
		cancelFan(userId, toUserId);
	}
	
	/**
	 * 取消关注(更新TA的粉丝列表)
	 * @param userId
	 * @param toUserId
	 */
	public void cancelFan(long userId, long toUserId){
		Map<Double, String> scoreMembers = new HashMap<Double, String>();
		long count = 1;
		//获取TA的粉丝列表
		String fanIDKey = getFanIdsKey(toUserId);
		scoreMembers.clear();
		//从最新时间大到小查我的关注列表
		Set<String> set = getMyFans(toUserId);
		//处理对方的粉丝列表
		if(set !=  null && set.size() >0){
			if(!set.contains(String.valueOf(userId))){
				logger.error("竟然在粉丝列表中没有数据");
				return;
			}
				
			//先把我从TA的粉丝列表移出
			set.remove(String.valueOf(userId));
			Iterator<String> iterator = set.iterator();
			while(iterator.hasNext()){
				scoreMembers.put((double)count, iterator.next());
				count--;
			}
			
			//移出我后没有粉丝列表数据，则将TA的粉丝列表移出缓存
			redisUtil.delete(fanIDKey);
			if(!scoreMembers.isEmpty()){
				redisUtil.zadd(fanIDKey, scoreMembers);
			}
		}	
		//cancelAttention(userId, toUserId);
	}
	
	/**
	 * 获取我全部粉丝的对象的列表
	 * @param toUserId
	 * @return
	 */
	public Set<String> getMyFans(long toUserId){
		return getMyFansLimit(toUserId, 0, -1);
	}
	
	/**
	 * 获取我粉丝的对象的列表
	 * @param toUserId
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> getMyFansLimit(long toUserId, int start, int end){
		String fanIDKey = getFanIdsKey(toUserId);
		//redisUtil.delete(fanIDKey);
		//关注列表不存在，先获取该对象的列表放在redis缓存中
		if(!redisUtil.hasKey(fanIDKey)){
			Map<Double, String> scoreMembers = new HashMap<Double, String>();
			long count = 1;
			String sql = "select id, create_user_id from "+DataTableType.粉丝.value+" f where status=? and to_user_id=? order by id";
			List<Map<String, Object>> list = fanMapper.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, toUserId);
			if(list != null && list.size() > 0){
				for(Map<String, Object> map: list){
					scoreMembers.put((double)count, String.valueOf(map.get("create_user_id")));
					count++;
				}
			}	
			if(scoreMembers.isEmpty()){
				return null;
			}
			redisUtil.zadd(fanIDKey, scoreMembers);
		}
		
		return redisUtil.getLimit(true, fanIDKey, start, end);
	}
	
	/**
	 * 是否已经换成了用户的ID的粉丝列表
	 * @param fanIdKey
	 * @return
	 */
	/*public boolean loadFanId(String fanIdKey){
		String fanIDKey = getAttentionIdsKey(user.getId());
		Map<Double, String> scoreMembers = new HashMap<Double, String>();
		long count = 1;
		//关注
		if(!redisUtil.hasKey(fanIDKey)){
			String sql = "select id, to_user_id from "+DataTableType.粉丝.value+" f where status=? and create_user_id=? order by id";
			List<Map<String, Object>> list = fanService.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, user.getId());
			if(list != null && list.size() > 0){
				for(Map<String, Object> map: list){
					scoreMembers.put((double)count, String.valueOf(map.get("to_user_id")));
					count++;
				}
			}
		}else{
			count = redisUtil.getZSetCard(fanIDKey);
			scoreMembers.put((double)count, String.valueOf(toUser.getId()));
		}
	}*/
	
	/**
	 * 判断toUserId是否在userId关注列表中
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	public boolean inAttention(long userId, long toUserId){
		boolean result = false;
		if(userId == toUserId || userId < 1 || toUserId < 1)
			return result;
		
		Set<String> set = getMyAttentions(userId);
		if(set != null && set.size() >0){
			result = set.contains(String.valueOf(toUserId));
		}
		return result;
	}
	
	
	/**
	 * 获取我所关注的人列表在redis的key
	 * @param userId
	 * @return
	 */
	public static String getAttentionIdsKey(long userId){
		return "attention_id_" +userId;
	}
	
	/**
	 * 获取关注我的人列表在redis的key
	 * @param userId
	 * @return
	 */
	public static String getFanIdsKey(long userId){
		return "fan_id_" +userId;
	}
}
