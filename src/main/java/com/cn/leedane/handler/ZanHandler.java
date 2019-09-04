package com.cn.leedane.handler;

import com.cn.leedane.mapper.ZanMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 赞的处理类
 * @author LeeDane
 * 2016年3月19日 下午11:26:25
 * Version 1.0
 */
@Component
public class ZanHandler {
	
	@Autowired
	private ZanMapper zanMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	/**
	 * 获取赞数量
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public int getZanNumber(String tableName, int tableId){
		String zanKey = getZanKey(tableName, tableId);
		int zanNumber;
		//赞
		if(!redisUtil.hasKey(zanKey)){
			//获取数据库中所有赞的数量
			zanNumber = getCurrentZanNumber(tableId, tableName);
			redisUtil.addString(zanKey, String.valueOf(zanNumber));
		}else{
			zanNumber = Integer.parseInt(redisUtil.getString(zanKey));
		}
		return zanNumber < 1 ? 0 : zanNumber;
	}
	
	/**
	 * 添加赞数量
	 * @param tableId
	 * @param tableName
	 * @return
	 */
	public boolean addZanNumber(int tableId, String tableName){
		String zanKey = getZanKey(tableName, tableId);
		boolean result = false;
		//赞
		if(!redisUtil.hasKey(zanKey)){
			//获取数据库中所有赞的数量
			int zanNumber = getCurrentZanNumber(tableId, tableName);
			redisUtil.addString(zanKey, String.valueOf(zanNumber));
		}else{
			int zanNumber = Integer.parseInt(redisUtil.getString(zanKey));
			redisUtil.addString(zanKey, String.valueOf(zanNumber +1));
		}
		result = true;
		return result;
	}
	
	/**
	 * 取消赞(同时取消赞的数量和用户)
	 * @param tableId
	 * @param tableName
	 * @param user
	 * @return
	 */
	public boolean cancelZan(int tableId, String tableName, UserBean user){
		String zanKey = getZanKey(tableName, tableId);
		String zanUserKey = getZanUserKey(tableName, tableId);
		boolean result = false;
		int zanNumber ;
		//赞的数量
		if(redisUtil.hasKey(zanKey)){
			zanNumber = Integer.parseInt(redisUtil.getString(zanKey)) -1;
			redisUtil.addString(zanKey, String.valueOf(zanNumber));
		}else{
			//获取数据库中所有赞的数量
			zanNumber = getCurrentZanNumber(tableId, tableName);
			redisUtil.addString(zanKey, String.valueOf(zanNumber));
		}
		
		//赞的用户
		if(redisUtil.hasKey(zanUserKey)){
			Set<String> sets = redisUtil.getSet(zanUserKey);
			String delete = user.getId() +"," +user.getAccount();
			if(sets.contains(delete)){
				sets.remove(delete);
			}
			redisUtil.addSet(zanUserKey, setToArray(sets));
		}else{
			String[] array;
			List<Map<String, Object>> results = zanMapper.executeSQL("select u.id, u.account from "+DataTableType.赞.value+" z inner join "+DataTableType.用户.value+" u on z.create_user_id = u.id where z.table_name=? and z.table_id = ?", tableName, tableId);
			
			if(results != null && results.size() > 0){
				int size = results.size();
				array = new String[size];
				for(int i = 0; i< size; i++){
					array[i] = StringUtil.changeNotNull(results.get(i).get("id"))+ ","+ StringUtil.changeNotNull(results.get(i).get("account"));
				}
			}else{
				array = new String[1];
				array[0] = user.getId() +"," +user.getAccount();
			}
			redisUtil.addSet(zanUserKey, array);
		}
		result = true;
		return result;
	}
	
	/**
	 * 添加点赞的用户
	 * @param tableId
	 * @param tableName
	 * @param user
	 * @return
	 */
	public boolean addZanUser(int tableId, String tableName, UserBean user){
		String zanUserKey = getZanUserKey(tableName, tableId);
		boolean result = false;
		//赞
		if(!redisUtil.hasKey(zanUserKey)){
			String[] array;
			List<Map<String, Object>> results = zanMapper.executeSQL("select u.id, u.account from "+DataTableType.赞.value+" z inner join "+DataTableType.用户.value+" u on z.create_user_id = u.id where z.table_name=? and z.table_id = ?", tableName, tableId);
			
			if(results != null && results.size() > 0){
				int size = results.size();
				array = new String[size];
				for(int i = 0; i< size; i++){
					array[i] = StringUtil.changeNotNull(results.get(i).get("id"))+ ","+ StringUtil.changeNotNull(results.get(i).get("account"));
				}
			}else{
				array = new String[1];
				array[0] = "test";
			}
			redisUtil.addSet(zanUserKey, array);
		}else{
			Set<String> sets = redisUtil.getSet(zanUserKey);
			if(sets != null && sets.contains("test")){
				sets.remove("test");
			}
			sets.add(user.getId() +"," +user.getAccount());
			
			//先删除后添加，不然以前的没有过滤掉
			redisUtil.delete(zanUserKey);
			redisUtil.addSet(zanUserKey, setToArray(sets));
		}
		result = true;
		return result;
	}
	
	private String[] setToArray(Set<String> sets){
		String[] array = new String[sets.size()];
		int i = 0;
		for(String set: sets){
			if(StringUtil.isNotNull(set)){
				array[i] = set;
			}else{
				return new String[]{"test"};
			}
			i++;
		}
		
		return array;
	}
	
	/**
	 * 获取当前的赞数量
	 * @param tableId
	 * @param tableName
	 * @return
	 */
	private int getCurrentZanNumber(int tableId, String tableName){
		//获取数据库中所有赞的数量
		return SqlUtil.getTotalByList(zanMapper.executeSQL("select count(*) ct from "+DataTableType.赞.value+" where table_name=? and table_id = ?", tableName, tableId));
	}
	
	
	/**
	 * 获取赞的用户列表（set集合存储，value为：ID+"," +用户账号名）
	 * @param tableId
	 * @param tableName
	 * @param user 用户，将排列在第一位返回
	 * @param limit 限制数量，当赞人数多的时候，限制展示的人数，默认只展示6个
	 * @return key是用户名， value是用户ID
	 */
	public String getZanUser(int tableId, String tableName, UserBean user, int limit){
		
		String zanUserKey = getZanUserKey(tableName, tableId);
		limit = limit == 0 ? 6 : limit;
		StringBuffer returnValue = new StringBuffer();
		//赞用户
		if(!redisUtil.hasKey(zanUserKey)){
			List<Map<String, Object>> rs = zanMapper.executeSQL("select u.id, u.account from "+DataTableType.赞.value+" z inner join "+DataTableType.用户.value+" u on z.create_user_id = u.id where z.table_name=? and z.table_id = ?", tableName, tableId);
			if(rs != null && rs.size()> 0){
				String[] userArray = new String[rs.size()];
				for(int i = 0; i < rs.size(); i++){
					userArray[i] = StringUtil.changeNotNull(rs.get(i).get("id"))+ ","+ StringUtil.changeNotNull(rs.get(i).get("account"));
					returnValue.append(rs.get(i).get("id"));
					returnValue.append(",");
					returnValue.append(rs.get(i).get("account"));
					returnValue.append(";");
				}
				redisUtil.addSet(zanUserKey, userArray);
			}else{
				//为了没有赞的情况下不必每次还要去查数据库，直接添加一个空的数据
				redisUtil.addSet(zanUserKey, "test");
			}
			String value = returnValue.toString();
			if(StringUtil.isNotNull(value)){
				value = value.substring(0, value.length() -1);
			}
			return value;
		}else{
			return setToString(redisUtil.getSet(zanUserKey), user, limit);
		}
	}
	
	private String setToString(Set<String> users, UserBean userBean, int limit){
		
		if(users != null && users.contains("test")){
			users.remove("test");
		}
		int i = 1;
		StringBuffer returnValue = new StringBuffer();
		//int型key必须转化成字符串
		if(userBean != null && users.contains(userBean.getId() +","+ userBean.getAccount())){
			returnValue.append(userBean.getId());
			returnValue.append(",");
			returnValue.append(userBean.getAccount());
			returnValue.append(";");
			users.remove(userBean.getId() +","+ userBean.getAccount());
			i = i+1;
		}
		String[] us;
		
		for(String user: users){
			//数据为空(有可能是一开始还没有赞的时候填充的空数据)或者已经超过限制数
			if(StringUtil.isNotNull(user) && i <= limit){
				us = user.split(",");
				if(us.length == 2){
					returnValue.append(us[0]);
					returnValue.append(",");
					returnValue.append(us[1]);
					returnValue.append(";");
					i++;
				}
			}else{
				break;
			}
		}
		
		String value = returnValue.toString();
		if(StringUtil.isNotNull(value)){
			value = value.substring(0, value.length() -1);
		}
		return value;
	}

	/**
	 * 删除对应的赞用户列表在redis
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public boolean deleteZanUsers(String tableName, int tableId){
		return redisUtil.delete(getZanUserKey(tableName, tableId));
	}

	/**
	 * 删除对应的赞列表在redis
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public boolean deleteZan(String tableName, int tableId){
		return redisUtil.delete(getZanKey(tableName, tableId));
	}
	
	/**
	 * 获取赞用户在redis的key
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public static String getZanUserKey(String tableName, int tableId){
		return ConstantsUtil.ZAN_USER_REDIS +tableName+"_"+tableId;
	}
	
	/**
	 * 获取赞在redis的key
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public static String getZanKey(String tableName, int tableId){
		return ConstantsUtil.ZAN_REDIS +tableName+"_"+tableId;
	}
}
