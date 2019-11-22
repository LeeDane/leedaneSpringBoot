package com.cn.leedane.handler;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cn.leedane.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.SerializationUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.controller.RoleController;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.UserSettingBean;
import com.cn.leedane.model.UserTokenBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;

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
	
	@Autowired
	private SystemCache systemCache;
	
	@Value("${constant.default.no.pic.path}")
    public String DEFAULT_NO_PIC_PATH;
	/**
	 * 获取正常用户设置状态对象
	 * @param userId
	 * @return
	 */
	public UserSettingBean getNormalSettingBean(long userId){
		UserSettingBean settingBean = null;
		String key = getUserSettingKey(userId);
		Object obj = systemCache.getCache(key);
		//deleteSettingBeanCache(circleId);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					settingBean =  (UserSettingBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), UserSettingBean.class);
					if(settingBean != null){
						systemCache.addCache(key, settingBean, true);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<UserSettingBean> settingBeans = userMapper.getSetting(userId, ConstantsUtil.STATUS_NORMAL);
						if(CollectionUtil.isEmpty(settingBeans))
							throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
						
						settingBean = settingBeans.get(0);
						if(settingBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(settingBean));
								systemCache.addCache(key, settingBean, true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				List<UserSettingBean> settingBeans = userMapper.getSetting(userId, ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isEmpty(settingBeans))
					throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
				
				settingBean = settingBeans.get(0);
				try {
					redisUtil.addSerialize(key, SerializeUtil.serializeObject(settingBean));
					systemCache.addCache(key, settingBean, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			settingBean = (UserSettingBean)obj;
		}
		
		if(settingBean == null || settingBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return settingBean;
	}
	
	
	/**
	 * 根据用户ID删除该用户的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteSettingBeanCache(long userId){
		String key = getUserSettingKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取用户设置在redis的key
	 * @return
	 */
	public static String getUserSettingKey(long userId){
		return ConstantsUtil.USER_REDIS  + "_st_"+ userId;
	}
	
	
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
	public void addLastRequestTime(long userId){
		redisUtil.addString(getRequestTimeKey(userId), DateUtil.DateToString(new Date()));
	}
	
	/**
	 * 获取用户操作的最新请求时间记录
	 * @param userId
	 * @return
	 */
	public String getLastRequestTime(long userId){
		return StringUtil.changeNotNull(redisUtil.getString(getRequestTimeKey(userId)));
	}
	
	/**
	 * 获取用户的头像路径
	 * @return
	 */
	public String getUserPicPath(long userId, String picSize){
		String userPicKey = getRedisUserPicKey(userId, picSize);

		//先从ehCache中读取缓存信息
		Object obj = systemCache.getCache(userPicKey);

		String userPicPath = null;
		if(obj == null || obj == ""){
			if(redisUtil.hasKey(userPicKey)){
				userPicPath = redisUtil.getString(userPicKey);
				if(StringUtil.isNotNull(userPicPath)){
					systemCache.addCache(userPicKey, userPicPath, true);
				}else {
					//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
					redisUtil.delete(userPicKey);
					//查找数据库，找到用户的头像
					List<Map<String, Object>> list = userMapper.executeSQL("select qiniu_path user_pic_path from "+DataTableType.文件.value+" f where is_upload_qiniu=? and f.table_name = '"+DataTableType.用户.value+"' and f.table_uuid = ? and f.pic_order = 0 "+buildPicSizeSQL("30x30")+" order by id desc limit 1", true, userId);
					if(CollectionUtil.isNotEmpty(list)){
						userPicPath = StringUtil.changeNotNull(list.get(0).get("user_pic_path"));
						if(StringUtil.isNotNull(userPicPath)){
							redisUtil.addString(userPicKey, userPicPath);
							systemCache.addCache(userPicKey, userPicPath, true);
						}
					}
				}
			}else{
				//查找数据库，找到用户的头像
				List<Map<String, Object>> list = userMapper.executeSQL("select qiniu_path user_pic_path from "+DataTableType.文件.value+" f where is_upload_qiniu=? and f.table_name = '"+DataTableType.用户.value+"' and f.table_uuid = ? and f.pic_order = 0 "+buildPicSizeSQL("30x30")+" order by id desc limit 1", true, userId);
				if(CollectionUtil.isNotEmpty(list)){
					userPicPath = StringUtil.changeNotNull(list.get(0).get("user_pic_path"));
					if(StringUtil.isNotNull(userPicPath))
						redisUtil.addString(userPicKey, userPicPath);
						systemCache.addCache(userPicKey, userPicPath, true);
				}
			}
		}else{
			userPicPath = StringUtil.changeNotNull(obj);
		}

		if(StringUtil.isNull(userPicPath))
			userPicPath = DEFAULT_NO_PIC_PATH;
		return userPicPath;
	}
	
	/**
	 * 更新用户头像的缓存数据
	 * @param userId
	 * @param picSize
	 */
	public void updateUserPicPath(long userId, String picSize){
		String userPicKey = getRedisUserPicKey(userId, picSize);
		String userPicPath = null;
		
		//先把原先的删掉
		if(redisUtil.hasKey(userPicKey)){
			redisUtil.delete(userPicKey);
			systemCache.removeCache(userPicKey);
		}
		//查找数据库，找到用户的头像
		List<Map<String, Object>> list = userMapper.executeSQL("select qiniu_path user_pic_path from "+DataTableType.文件.value+" f where is_upload_qiniu=? and f.table_name = '"+DataTableType.用户.value+"' and f.table_uuid = ? and f.pic_order = 0 "+buildPicSizeSQL("30x30")+" order by id desc limit 1", true, userId);
		if(CollectionUtil.isNotEmpty(list)){
			userPicPath = StringUtil.changeNotNull(list.get(0).get("user_pic_path"));
			if(StringUtil.isNotNull(userPicPath)){
				redisUtil.addString(userPicKey, userPicPath);
				systemCache.addCache(userPicKey, userPicPath, true);
			}
		}
	}
	
	/**
	 * 获取用户的信息
	 * @return
	 */
	public JSONObject getUserDetail(long userId){
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
	public UserBean getUserBean(long userId){
		String userInfoKey = getRedisUserInfoKey(userId);
		UserBean user = null;
		//先从ehCache中读取缓存信息
		Object obj = systemCache.getCache(userInfoKey);
		try {
			if(obj == null || obj == ""){
				if(redisUtil.hasKey(userInfoKey)){
						//从redi缓存中读取
						user = (UserBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(userInfoKey.getBytes()), UserBean.class);
						if(user != null){
							systemCache.addCache(userInfoKey, user, true);
						}else{
							//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
							redisUtil.delete(userInfoKey);
							user = userMapper.findById(UserBean.class, userId);
							if(user != null){
								redisUtil.addSerialize(userInfoKey, SerializationUtils.serialize(user));
								systemCache.addCache(userInfoKey, user, true);
							}
						}
				}else{
					user = userMapper.findById(UserBean.class, userId);
					if(user != null){
						redisUtil.addSerialize(userInfoKey, SerializationUtils.serialize(user));
						systemCache.addCache(userInfoKey, user, true);
					}
				}
			}else{
				user = (UserBean) obj;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	/**
	 * 获取用户Bean
	 * @param username 用户名
	 * @param username 密码
	 * @return
	 */
	public UserBean getUserBean(String username, String pwd){
		UserBean user = userMapper.loginUser(username, MD5Util.compute(pwd));
		
		if(user != null){
			String userInfoKey = getRedisUserInfoKey(user.getId());
			//登录成功用户信息加入缓存
			redisUtil.addSerialize(userInfoKey, SerializationUtils.serialize(user));
			systemCache.addCache(userInfoKey, user);
			return user;
		}
		return null;
	}
	
	/**
	 * 删除用户的基本信息
	 * @return
	 */
	public void deleteUserDetail(long userId){
		String userInfoKey = getRedisUserInfoKey(userId);
		redisUtil.delete(userInfoKey);
		systemCache.removeCache(userInfoKey);
	}
	
	/**
	 * 获取用户的名称
	 * @return
	 */
	public String getUserName(long userId){
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
	public String getUserMobilePhone(long userId){
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
			Subject currentUser = SecurityUtils.getSubject();
			boolean isAdmin = currentUser.hasRole(RoleController.ADMIN_ROLE_CODE);
			infos.put("id", user2.getId());
			infos.put("account", user2.getAccount());
			if(StringUtil.isNotNull(user2.getEmail()))
				infos.put("email",  (!isAdmin && !isSelf ? (SecurityMessageUtil.left(user2.getEmail(), SecurityMessageUtil.TWO) + "*****"+ SecurityMessageUtil.right(user2.getEmail(), SecurityMessageUtil.THREE)): StringUtil.changeNotNull(user2.getEmail())));
			else
				infos.put("email", "");

			infos.put("age", user2.getAge());
			Date birthDay = user2.getBirthDay();
			if(birthDay != null){
				infos.put("birth_day", DateUtil.DateToString(birthDay, "yyyy-MM-dd"));
			}else{
				infos.put("birth_day", "");
			}

			if(StringUtil.isNotNull(user2.getMobilePhone()))
				infos.put("mobile_phone", (!isAdmin && !isSelf ? SecurityMessageUtil.left(user2.getMobilePhone(), SecurityMessageUtil.THREE) + "*****" + SecurityMessageUtil.right(user2.getMobilePhone(), SecurityMessageUtil.FOUR) : StringUtil.changeNotNull(user2.getMobilePhone())));
			else
				infos.put("mobile_phone", "");

			//infos.put("pic_path", user2.getPicPath());
			if(StringUtil.isNotNull(user2.getQq()))
				infos.put("qq", (!isAdmin && !isSelf ? SecurityMessageUtil.left(user2.getQq(), SecurityMessageUtil.TWO) + "*****" + SecurityMessageUtil.right(user2.getQq(), SecurityMessageUtil.TWO) : StringUtil.changeNotNull(user2.getQq())));
			else
				infos.put("qq", "");

			infos.put("sex", StringUtil.changeNotNull(user2.getSex()));
			infos.put("is_admin", isAdmin);
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
			
			if(isAdmin || isSelf){
				//最后操作状态
				infos.put("last_request_time", getLastRequestTime(user2.getId()));//最近操作记录
				infos.put("personal_introduction", StringUtil.changeNotNull(user2.getPersonalIntroduction()));
			}

		}
		return infos;
	}
	/**
	 * 获取提供调用使用的用户信息
	 * @param user2
	 * @param isSelf  是否是自己，自己的话可以不加载一些信息
	 * @return
	 */
	public Map<String, Object> getUserInfo(Map<String, Object> user2, boolean isSelf) {
		HashMap<String, Object> infos = new HashMap<String, Object>();
		if (user2 != null) {
			Subject currentUser = SecurityUtils.getSubject();
			boolean isAdmin = currentUser.hasRole(RoleController.ADMIN_ROLE_CODE);
			String id = StringUtil.changeNotNull(user2.get("id"));
			infos.put("id", id);
			infos.put("account", user2.get("account"));
			String email = StringUtil.changeNotNull(user2.get("email"));
			if (StringUtil.isNotNull(email))
				infos.put("email", (!isAdmin && !isSelf ? (SecurityMessageUtil.left(email, SecurityMessageUtil.TWO) + "*****" + SecurityMessageUtil.right(email, SecurityMessageUtil.THREE)) : StringUtil.changeNotNull(email)));
			else
				infos.put("email", "");

			infos.put("age", user2.get("age"));
			String birthDay = StringUtil.changeNotNull(user2.get("birth_day"));
			if (StringUtil.isNotNull(birthDay)) {
				infos.put("birth_day", DateUtil.DateToString(DateUtil.stringToDate(birthDay), "yyyy-MM-dd"));
			} else {
				infos.put("birth_day", "");
			}

			String mobilePhone = StringUtil.changeNotNull(user2.get("mobile_phone"));
			if (StringUtil.isNotNull(mobilePhone))
				infos.put("mobile_phone", (!isAdmin && !isSelf ? SecurityMessageUtil.left(mobilePhone, SecurityMessageUtil.THREE) + "*****" + SecurityMessageUtil.right(mobilePhone, SecurityMessageUtil.FOUR) : StringUtil.changeNotNull(mobilePhone)));
			else
				infos.put("mobile_phone", "");

			//infos.put("pic_path", user2.getPicPath());

			String qq = StringUtil.changeNotNull(user2.get("qq"));
			if (StringUtil.isNotNull(qq))
				infos.put("qq", (!isAdmin && !isSelf ? SecurityMessageUtil.left(qq, SecurityMessageUtil.TWO) + "*****" + SecurityMessageUtil.right(qq, SecurityMessageUtil.TWO) : StringUtil.changeNotNull(qq)));
			else
				infos.put("qq", "");

			infos.put("sex", StringUtil.changeNotNull(user2.get("sex")));
			infos.put("is_admin", isAdmin);
			infos.put("education_background", StringUtil.changeNotNull(user2.get("education_background")));
			infos.put("user_pic_path", getUserPicPath(StringUtil.changeObjectToInt(id), "30x30"));
			infos.put("register_time", StringUtil.isNull(StringUtil.changeNotNull(user2.get("register_time"))) ? "" : DateUtil.DateToString(DateUtil.stringToDate(StringUtil.changeNotNull(user2.get("register_time"))), "yyyy-MM-dd"));


			if (isAdmin || isSelf) {
				//最后操作状态
				infos.put("last_request_time", getLastRequestTime(StringUtil.changeObjectToInt(id)));//最近操作记录
			}
			infos.put("personal_introduction", StringUtil.changeNotNull(user2.get("personal_introduction")));
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
	 * @param toUserId
	 * @return
	 */
	public Map<String, Object> getBaseUserInfo(long toUserId){
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
	 * @return
	 */
	public String getSession(UserBean user){
		String key = getRedisSessionKey(user.getId());
		return redisUtil.getString(key);
	}
	
	/**
	 * 从redis中清除session
	 * @param user
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
	private Set<String> getTokens(long userid){
		String key = getRedisUserTokenKey(userid, 0);
		return redisUtil.keys(key);
	}
	
	/**
	 * 该用户是否有免登录码
	 * @param token
	 * @return
	 */
	public boolean hasToken(long userid, String token){
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
	
	public static String getRedisUserPicKey(long userId, String picSize){
		return "user_pic_"+userId +"_" +picSize;
	}
	
	public static String getRedisUserInfoKey(long userId){
		return "user_info_"+userId;
	}
	
	/**
	 * 获取最新的请求Redis键
	 * @param userId
	 * @return
	 */
	public static String getRequestTimeKey(long userId){
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
	 * @param userId
	 * @param userTokenId
	 * @return
	 */
	public static String getRedisUserTokenKey(long userId, long userTokenId){
		return "user_token_a_"+userId +"_" + (userTokenId > 0? userTokenId : "*");
	}
	
	/**
	 * 缓存免登录码信息
	 * @param userid
	 * @return
	 */
	public static String getRedisSessionKey(long userid){
		return "session_mapper_"+userid;
	}
	/*private String getRedisUserAccountKey(int userId){
		return "t_user_account_"+userId;
	}*/
}
