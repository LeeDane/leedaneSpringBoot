package com.cn.leedane.handler;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.SerializationUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.controller.RoleController;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.UserTokenBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.MD5Util;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 用户的处理类
 * @author LeeDane
 * 2016年3月19日 下午10:24:12
 * Version 1.0
 */
@Component
public class UserHandler {

	@Autowired
	private UserMapper userMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	
	/**
	 * 获取系统全部的用户
	 * @return
	 */
	public JSONArray getAllUserDetail(){
		List<Map<String, Object>> uids = userMapper.executeSQL("select id from "+DataTableType.用户.value);
		JSONArray userInfos = null;
		if(uids != null && uids.size() > 0){
			userInfos = new JSONArray();
			int userId;
			for(int i = 0; i < uids.size(); i++){
				userId = StringUtil.changeObjectToInt(uids.get(i).get("id"));
				userInfos.add(getUserDetail(userId));
			}
		}
		return userInfos;
	}
	
	/**
	 * 保存用户操作的最新的请求时间记录
	 * @param userId
	 */
	public void addLastRequestTime(int userId){
		redisUtil.addString(getRequestTimeKey(userId), DateUtil.DateToString(new Date()));
	}
	
	/**
	 * 获取用户操作的最新请求时间记录
	 * @param userId
	 * @return
	 */
	public String getLastRequestTime(int userId){
		return StringUtil.changeNotNull(redisUtil.getString(getRequestTimeKey(userId)));
	}
	
	/**
	 * 获取用户的头像路径
	 * @return
	 */
	public String getUserPicPath(int userId, String picSize){
		String userPicKey = getRedisUserPicKey(userId, picSize);
		String userPicPath = null;
		if(redisUtil.hasKey(userPicKey)){
			userPicPath = redisUtil.getString(userPicKey);
		}else{
			//查找数据库，找到用户的头像
			List<Map<String, Object>> list = userMapper.executeSQL("select qiniu_path user_pic_path from "+DataTableType.文件.value+" f where is_upload_qiniu=? and f.table_name = '"+DataTableType.用户.value+"' and f.table_uuid = ? and f.pic_order = 0 "+buildPicSizeSQL("30x30")+" order by id desc limit 1", true, userId);
			if(list != null && list.size()>0){
				userPicPath = StringUtil.changeNotNull(list.get(0).get("user_pic_path"));
				if(StringUtil.isNotNull(userPicPath))
					redisUtil.addString(userPicKey, userPicPath);
			}
			
		}
		return userPicPath;
	}
	
	/**
	 * 更新用户头像的缓存数据
	 * @param userId
	 * @param picSize
	 */
	public void updateUserPicPath(int userId, String picSize){
		String userPicKey = getRedisUserPicKey(userId, picSize);
		String userPicPath = null;
		
		//先把原先的删掉
		if(redisUtil.hasKey(userPicKey)){
			redisUtil.delete(userPicKey);
		}
		//查找数据库，找到用户的头像
		List<Map<String, Object>> list = userMapper.executeSQL("select qiniu_path user_pic_path from "+DataTableType.文件.value+" f where is_upload_qiniu=? and f.table_name = '"+DataTableType.用户.value+"' and f.table_uuid = ? and f.pic_order = 0 "+buildPicSizeSQL("30x30")+" order by id desc limit 1", true, userId);
		if(list != null && list.size()>0){
			userPicPath = StringUtil.changeNotNull(list.get(0).get("user_pic_path"));
			if(StringUtil.isNotNull(userPicPath))
				redisUtil.addString(userPicKey, userPicPath);
		}
	}
	
	/**
	 * 获取用户的信息
	 * @return
	 */
	public JSONObject getUserDetail(int userId){
		UserBean user = getUserBean(userId);
		if(user != null){
			return JSONObject.fromObject(user);
		}
		return new JSONObject();
	}
	
	/**
	 * 获取用户Bean
	 * @param userId 用户Id
	 * @return
	 */
	public UserBean getUserBean(int userId){
		String userInfoKey = getRedisUserInfoKey(userId);
		if(redisUtil.hasKey(userInfoKey)){
			try {
				return (UserBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(userInfoKey.getBytes()), UserBean.class);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		UserBean user = userMapper.findById(UserBean.class, userId);
		if(user != null){
			redisUtil.addSerialize(userInfoKey, SerializationUtils.serialize(user));
			return user;
		}
		return null;
	}
	
	/**
	 * 获取用户Bean
	 * @param userId 用户Id
	 * @return
	 */
	public UserBean getUserBean(String username, String pwd){
		UserBean user = userMapper.loginUser(username, MD5Util.compute(pwd));		
		if(user != null){
			String userInfoKey = getRedisUserInfoKey(user.getId());
			redisUtil.addSerialize(userInfoKey, SerializationUtils.serialize(user));
			return user;
		}
		return null;
	}
	
	/**
	 * 删除用户的基本信息
	 * @return
	 */
	public void deleteUserDetail(int userId){
		String userInfoKey = getRedisUserInfoKey(userId);
		redisUtil.delete(userInfoKey);
	}
	
	/**
	 * 获取用户的名称
	 * @return
	 */
	public String getUserName(int userId){
		JSONObject userInfo = getUserDetail(userId);
		return userInfo != null ? JsonUtil.getStringValue(userInfo, "account") : null;
	}
	
	/**
	 * 通过用户名获取用户的ID
	 * @return
	 */
	public int getUserIdByName(String username){
		String usernameKey = getRedisUserNameKey(username);
		int userId = 0;
		if(redisUtil.hasKey(usernameKey)){
			userId = Integer.parseInt(redisUtil.getString(usernameKey));
		}else{
			List<Map<String, Object>> list = userMapper.executeSQL("select id from "+DataTableType.用户.value+" where status=? and account=? limit 1", ConstantsUtil.STATUS_NORMAL, username);
			userId = list != null && list.size() == 1? StringUtil.changeObjectToInt(list.get(0).get("id")) : 0;
			if(userId > 0 ){
				redisUtil.addString(usernameKey, String.valueOf(userId));
			}
		}
		return userId;
	}
	
	/**
	 * 获取用户的绑定的手机号码
	 * @return
	 */
	public String getUserMobilePhone(int userId){
		JSONObject userInfo = getUserDetail(userId);
		return JsonUtil.getStringValue(userInfo, "mobile_phone");
	}
	
	/**
	 * 获取提供调用使用的用户信息
	 * @param user2
	 * @param isSelf  是否是自己，自己的话可以不加载一些信息
	 * @return
	 */
	public Map<String, Object> getUserInfo(UserBean user2, boolean isSelf) {
		HashMap<String, Object> infos = new HashMap<String, Object>();
		if(user2 != null){
			infos.put("id", user2.getId());
			infos.put("account", user2.getAccount());
			infos.put("email", StringUtil.changeNotNull(user2.getEmail()));
			infos.put("age", user2.getAge());
			Date birthDay = user2.getBirthDay();
			if(birthDay != null){
				infos.put("birth_day", DateUtil.DateToString(birthDay, "yyyy-MM-dd"));
			}else{
				infos.put("birth_day", "");
			}
			
			Subject currentUser = SecurityUtils.getSubject();
			
			infos.put("mobile_phone", StringUtil.changeNotNull(user2.getMobilePhone()));
			//infos.put("pic_path", user2.getPicPath());
			infos.put("qq", StringUtil.changeNotNull(user2.getQq()));
			infos.put("sex", StringUtil.changeNotNull(user2.getSex()));
			infos.put("is_admin", currentUser.hasRole(RoleController.ADMIN_ROLE_CODE));
			infos.put("education_background", StringUtil.changeNotNull(user2.getEducationBackground()));
			infos.put("user_pic_path", getUserPicPath(user2.getId(), "30x30"));
			infos.put("register_time", user2.getRegisterTime() == null ? "" : DateUtil.DateToString(user2.getRegisterTime(), "yyyy-MM-dd"));
			/*String str = "{\"uid\":"+user2.getId()+", \"pic_size\":\"60x60\"}";
			JSONObject jo = JSONObject.fromObject(str);
			try {
				infos.put("head_path", userService.getHeadFilePathStrById(jo, user2, request));
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			
			if(!isSelf){/*
				infos.put("no_login_code", user2.getNoLoginCode());
			}else{*/
				infos.put("last_request_time", getLastRequestTime(user2.getId()));//最近操作记录
			}
			infos.put("personal_introduction", StringUtil.changeNotNull(user2.getPersonalIntroduction()));
		}
		return infos;
	}
	
	/**
	 * 获取用户的用户名和头像(30x30)
	 * 返回{"user_pic_path":"","account":""}集合
	 * @param createUserId
	 * @param user
	 * @param friendObject
	 * @return
	 */
	public Map<String, Object> getBaseUserInfo(int createUserId, UserBean user, JSONObject friendObject){
		Map<String, Object> infoMap = new HashMap<String, Object>();
		if(createUserId> 0){
			String account = null;
			//JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			infoMap.put("user_pic_path", getUserPicPath(createUserId, "30x30"));
			if(createUserId != user.getId()){
				if(friendObject != null){
					account = JsonUtil.getStringValue(friendObject, "user_" +createUserId);
					if(StringUtil.isNotNull(account))
						//替换好友称呼的名称
						infoMap.put("account", account);
					else{
						infoMap.put("account", getUserName(createUserId));
					}
				}else{
					infoMap.put("account", getUserName(createUserId));
				}
			}else{
				infoMap.put("account", "本人");
			}
			
		}
		
		return infoMap;
	}
	
	/**
	 * 获取用户的用户名和头像(30x30)
	 * 返回{"user_pic_path":"","account":""}集合
	 * @param createUserId
	 * @param user
	 * @param friendObject
	 * @return
	 */
	public Map<String, Object> getBaseUserInfo(int toUserId){
		Map<String, Object> infoMap = new HashMap<String, Object>();
		if(toUserId> 0){
			//JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			infoMap.put("user_pic_path", getUserPicPath(toUserId, "30x30"));
			infoMap.put("account", getUserName(toUserId));
		}
		return infoMap;
	}
	
	
	/**
	 * 返回该用户登录失败次数
	 * @param account
	 * @return
	 */
	public synchronized int addLoginErrorNumber(String account){
		int number = 1;
		String key = getRedisUserNameLoginErrorKey(account);
		number = getLoginErrorNumber(account) + number;
		//redisUtil.addString(key, redisUtil.getString(key).substring(0, 14) + number);
		
		Calendar calendar = Calendar.getInstance();
		//分钟加5分钟
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 5);
		//以最新的系统时间作为错误时间
		redisUtil.addString(key, DateUtil.DateToString(calendar.getTime(), "yyyyMMddHHmmss") +number);
		return number;
	}
	
	/**
	 * 获取用户登录失败数量
	 * @param account
	 * @return
	 */
	public int getLoginErrorNumber(String account){
		int number = 0;
		String key = getRedisUserNameLoginErrorKey(account);
		if(redisUtil.hasKey(key)){
			String string = redisUtil.getString(key);
			//截取14位是因为前面14位被第一次错我的时间格式字符串
			number = StringUtil.changeObjectToInt(string.substring(14, string.length()));
		}
		return number;
	}
	
	/**
	 * 获取用户登录第一次失败的时间
	 * @param account
	 * @return
	 */
	public Date getLoginErrorTime(String account){
		Date date = null;
		String key = getRedisUserNameLoginErrorKey(account);
		if(redisUtil.hasKey(key)){
			String string = redisUtil.getString(key);
			//截取14位是因为前面14位被第一次错的时间格式字符串
			date = DateUtil.stringToDate(string.substring(0, 14), "yyyyMMddHHmmss");
		}
		return date;
	}
	
	/**
	 * 添加session到redis中
	 * @param user
	 * @param sessionId
	 * @return
	 */
	public boolean addSession(UserBean user, String sessionId){
		String key = getRedisSessionKey(user.getId());
		redisUtil.addString(key, sessionId);
		return true;
	}
	
	/**
	 * 从redis中获取session
	 * @param user
	 * @param sessionId
	 * @return
	 */
	public String getSession(UserBean user){
		String key = getRedisSessionKey(user.getId());
		return redisUtil.getString(key);
	}
	
	/**
	 * 从redis中清除session
	 * @param user
	 * @param sessionId
	 * @return
	 */
	public boolean deleteSession(UserBean user){
		String key = getRedisSessionKey(user.getId());
		return redisUtil.delete(key);
	}
	
	/**
	 * 添加免登录码
	 * @param userTokenBean
	 * @return
	 */
	public boolean addTokenCode(UserTokenBean userTokenBean){
		String key = getRedisUserTokenKey(userTokenBean.getCreateUserId(), userTokenBean.getId());
		if(redisUtil.addString(key, userTokenBean.getToken()) && userTokenBean.getOverdue() != null){
			int overdueTime = (int)(userTokenBean.getOverdue().getTime() - DateUtil.getCurrentTime().getTime()) / 1000;
			if(overdueTime > 10)
				return redisUtil.expire(key, userTokenBean.getToken(), overdueTime);
				
			return true;
		}
		return false;
	}
	
	/**
	 * 获取免登录码
	 * @param userid
	 * @return
	 */
	private Set<String> getTokens(int userid){
		String key = getRedisUserTokenKey(userid, 0);
		return redisUtil.keys(key);
	}
	
	/**
	 * 该用户是否有免登录码
	 * @param token
	 * @return
	 */
	public boolean hasToken(int userid, String token){
		Set<String> set = getTokens(userid);
		if(set != null && set.size() > 0){
			for(String str: set){
				if(token.equals(redisUtil.getString(str)))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 移除用户的登录失败次数
	 * @param account
	 * @return
	 */
	public boolean removeLoginErrorNumber(String account){
		String key = getRedisUserNameLoginErrorKey(account);
		redisUtil.delete(key);
		return true;
	}
	
	
	/**
	 * 构建获取图像大小的SQL
	 * @param picSize
	 * @return
	 */
	private String buildPicSizeSQL(String picSize){
		if(StringUtil.isNull(picSize))
			return "";
		
		return " and (f.pic_size = '" + picSize +"' or f.pic_size = 'source') ";
	}
	
	public static String getRedisUserPicKey(int userId, String picSize){
		return "user_pic_"+userId +"_" +picSize;
	}
	
	public static String getRedisUserInfoKey(int userId){
		return "user_info_"+userId;
	}
	
	/**
	 * 获取最新的请求Redis键
	 * @param userId
	 * @return
	 */
	public static String getRequestTimeKey(int userId){
		return "last_request_time_"+userId;
	}
	
	/**
	 * 缓存用户名信息
	 * @param username
	 * @return
	 */
	public static String getRedisUserNameKey(String username){
		return "user_name_"+username;
	}
	
	/**
	 * 缓存用户名登录失败信息
	 * @param username
	 * @return
	 */
	public static String getRedisUserNameLoginErrorKey(String username){
		return "user_login_error_"+username;
	}
	
	/**
	 * 缓存token信息
	 * @param token
	 * @return
	 */
	public static String getRedisUserTokenKey(int userId, int userTokenId){
		return "user_token_a_"+userId +"_" + (userTokenId > 0? userTokenId : "*");
	}
	
	/**
	 * 缓存免登录码信息
	 * @param username
	 * @return
	 */
	public static String getRedisSessionKey(int userid){
		return "session_mapper_"+userid;
	}
	/*private String getRedisUserAccountKey(int userId){
		return "t_user_account_"+userId;
	}*/
}
