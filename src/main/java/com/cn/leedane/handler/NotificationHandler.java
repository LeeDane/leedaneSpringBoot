package com.cn.leedane.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.CommentMapper;
import com.cn.leedane.mapper.NotificationMapper;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.message.JPushMessageNotificationImpl;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.message.notification.MessageNotification;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.KeyValuesBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.TransmitBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleUserPostsBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.wechat.util.HttpRequestUtil;

/**
 * 通知的处理类
 * @author LeeDane
 * 2016年7月12日 下午2:38:33
 * Version 1.0
 */
@Component
public class NotificationHandler {
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private CommentMapper commentMapper;
	
	@Autowired
	private NotificationMapper notificationMapper;
	
	@Autowired
	private FriendHandler friendHandler;

	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private CommentHandler commentHandler;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Value("${constant.robot.id}")
    public int robotId;
	
	/**
	 * 给管理员发送错误异常的通知
	 * @param content
	 * @param tableName
	 * @param tableId
	 * @param objectBean ids没有机器人的Id的时候可以为空
	 */
	public void sendErrorNotification(String content, String tableName, long tableId, Object objectBean){
		Set<Long> ids = new HashSet<Long>();
		ids.add(OptionUtil.adminUser.getId());
		sendNotificationByIds(true, OptionUtil.adminUser.getId(), ids, content, NotificationType.通知, tableName, tableId, objectBean);
	}
	
	
	/**
	 * 发送通知
	 * @param notifyMySelf  是否需要通知自己
	 * @param fromUserId 当前登录用户ID
	 * @param id  单个用户
	 * @param content
	 * @param notificationType
	 * @param tableName
	 * @param tableId
	 * @param objectBean ids没有机器人的Id的时候可以为空
	 */
	public void sendNotificationById(boolean notifyMySelf, long fromUserId, long id, String content, NotificationType notificationType, String tableName, long tableId, Object objectBean){
		Set<Long> ids = new HashSet<Long>();
		ids.add(id);
		sendNotificationByIds(notifyMySelf, fromUserId, ids, content, notificationType, tableName, tableId, objectBean);
	}
	
	/**
	 * 发送通知
	 * @param notifyMySelf 是否需要通知自己
	 * @param fromUserId 当前登录用户ID
	 * @param ids
	 * @param content
	 * @param notificationType
	 * @param tableName
	 * @param tableId
	 * @param objectBean ids没有机器人的Id的时候可以为空
	 */
	public void sendNotificationByIds(boolean notifyMySelf, final long fromUserId, Set<Long> ids, String content, NotificationType notificationType, String tableName, long tableId, final Object objectBean){
		if(!notifyMySelf){
			//先把自己过滤掉(自己不需要通知)
			ids.remove(fromUserId);
		}
		
		//是否有人@机器人ID
		if(robotId > 0 && ids.contains(robotId)){
			//机器人不需要消息通知，需要自动回复
			ids.remove(robotId);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					robotReply(robotId, fromUserId, objectBean);
				}
			}).start();
			
		}
		int size = ids.size();
		if(size < 1)
			return;
		
		ExecutorService executorService = null;
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		if(size > 5){
			executorService = Executors.newFixedThreadPool(5);
		}else{
			executorService = Executors.newScheduledThreadPool(size);
		}
		
		SingleSendNotification notification;
		
		NotificationBean notificationBean;
		JSONObject friendObject = null;
		String account = null;
		for(Long id: ids){
			friendObject = friendHandler.getFromToFriends(id);
			notificationBean = new NotificationBean();
			notificationBean.setFromUserId(fromUserId);
			if(content.contains("{from_user_id}")){
				content = content.replaceAll("\\{from_user_id\\}", String.valueOf(fromUserId));
			}
			if(content.contains("{to_user_id}")){
				content = content.replaceAll("\\{to_user_id\\}", String.valueOf(id));
			}
			
			if(content.contains("{from_user_remark}")){
				account = JsonUtil.getStringValue(friendObject, "user_" + fromUserId);
				if(StringUtil.isNull(account)){
					account = userHandler.getUserName(fromUserId);
				}
				content = content.replaceAll("\\{from_user_remark\\}", account);
			}
			if(content.contains("{to_user_remark}")){
				account = JsonUtil.getStringValue(friendObject, "user_" +id);
				if(StringUtil.isNull(account)){
					account = userHandler.getUserName(id);
				}
				content = content.replaceAll("\\{to_user_remark\\}", JsonUtil.getStringValue(friendObject, "user_" +id));
			}
			notificationBean.setContent(content);
			notificationBean.setCreateTime(DateUtil.DateToString(new Date()));
			notificationBean.setToUserId(id);
			notificationBean.setType(notificationType);
			notification = new SingleSendNotification(notificationBean);
			notificationBean.setTableName(tableName);
			notificationBean.setTableId(tableId);
			notificationBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			futures.add(executorService.submit(notification));
		}
	}
	
	/**
	 * 发送聊天的自定义消息
	 * @param user
	 * @param toUserId
	 * @param chatMap
	 */
	public void sendCustomMessageById(UserBean user, long toUserId, Map<String, Object> chatMap){
		if(user.getId() == toUserId){
			logger.error("自己不能给自己发信息");
			return;
		}
		
		CustomMessage customMessage = new JpushCustomMessage();	
		customMessage.sendToAlias("leedane_user_"+toUserId, JSONObject.fromObject(chatMap).toString(), EnumUtil.CustomMessageExtraType.聊天.value);	
	}
	
	/**
	 * leedane机器人回复
	 * @param robotId
	 * @param toUserId
	 * @param objectBean
	 */
	private void robotReply(int robotId, long toUserId, Object objectBean) {
		
		String tableName = null;
		long tableId = 0;
		String robotName = userHandler.getUserName(robotId);
		//找到该机器人的名称
		if(StringUtil.isNotNull(robotName)){
			String content = "";//用户发送的内容
			String robotReply = "";//机器人回复的内容
			long pid = 0;
			if(objectBean instanceof MoodBean){
				MoodBean moodBean = (MoodBean) objectBean;
				if(moodBean != null){
					tableName = DataTableType.心情.value;
					tableId = moodBean.getId();
					content = getContent(moodBean.getContent());
				}
			}else if(objectBean instanceof BlogBean){
				BlogBean blogBean = (BlogBean) objectBean;
				if(blogBean != null){
					tableName = DataTableType.博客.value;
					tableId = blogBean.getId();
					content = getContent(blogBean.getContent());
				}
			}else if(objectBean instanceof CommentBean){
				CommentBean commentBean = (CommentBean) objectBean;
				if(commentBean != null){
					pid = commentBean.getId();
					tableName = commentBean.getTableName();
					tableId = commentBean.getTableId();
					content = getContent(commentBean.getContent());
				}
			}else if(objectBean instanceof TransmitBean){
				TransmitBean transmitBean = (TransmitBean) objectBean;
				if(transmitBean != null){
					tableName = transmitBean.getTableName();
					tableId = transmitBean.getTableId();
					content = getContent(transmitBean.getContent());
				}
			}
			if(StringUtil.isNotNull(content)){
				String r = null;
				synchronized (content) {			
					try {
						r = HttpRequestUtil.sendAndRecieveFromTuLing(content);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				logger.info("图灵机器人返回的信息:"+r);
				JSONObject json = JSONObject.fromObject(r);
				int code = json.getInt("code");	
				if(code == 100000){
					robotReply = json.getString("text");
					robotReply = robotReply.replaceAll("图灵", "");
				}else{
					robotReply = "你在说什么？没听见";
				}
				
				UserBean robotUser = userMapper.findById(UserBean.class, robotId);
				
				//保存评论记录
				CommentBean bean = new CommentBean();
				bean.setCommentLevel(0);
				bean.setContent(robotReply);
				bean.setCreateTime(new Date());
				bean.setCreateUserId(robotUser.getId());
				bean.setPid(pid);
				bean.setFroms("机器人自动回复");
				bean.setStatus(ConstantsUtil.STATUS_NORMAL);
				bean.setTableId(tableId);
				bean.setTableName(tableName);
				try {
					if(commentMapper.save(bean) > 0){
						//更新评论数
						commentHandler.addComment(tableName, tableId);
						String notificationContent = robotName +"回复您："+robotReply;
						sendNotificationById(true, robotUser.getId(), toUserId, notificationContent, NotificationType.艾特我, tableName, tableId, objectBean);
					}else{
						logger.error("失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}	
	}
	
	
	/**
	 * 从内容中提取除了@某人 后的内容
	 * @param content
	 * @return
	 */
	private static String getContent(String content){
		if(StringUtil.isNotNull(content)){
			Set<String> usernames = StringUtil.getAtUserName(content);
			if(usernames != null && usernames.size() > 0){
				for(String username: usernames){
					content = content.replaceAll("@"+username, "");
				}
			}
			return content.trim();
		}
		return "";
	}
	/**
	 * 发送通知
	 * @param notifyMySelf 是否需要通知自己
	 * @param fromUserId
	 * @param names
	 * @param content
	 * @param notificationType
	 * @param tableName
	 * @param tableId
	 * @param objectBean
	 */
	public void sendNotificationByNames(boolean notifyMySelf, long fromUserId, Set<String> names, String content, NotificationType notificationType, String tableName, long tableId, Object objectBean){
		int size = names.size();
		if(size < 1)
			return;
		Set<Long> ids = new HashSet<Long>();
		long id = 0;
		for(String name: names){
			id = userHandler.getUserIdByName(name);
			if(id > 0 )
				ids.add(id);
		}
		if(ids.size() > 0 )
			sendNotificationByIds(notifyMySelf, fromUserId, ids, content, notificationType, tableName, tableId, objectBean);
	}
	
	/**
	 * 单个发送消息通知的类
	 * @author LeeDane
	 * 2016年4月25日 下午1:51:24
	 * Version 1.0
	 */
	class SingleSendNotification implements Callable<Boolean>{
		private NotificationBean mNotificationBean;
		
		SingleSendNotification(NotificationBean notificationBean){
			mNotificationBean = notificationBean;
			if(notificationMapper == null){
				notificationMapper = (NotificationMapper) SpringUtil.getBean("notificationMapper");
			}
		}

		@Override
		public Boolean call() throws Exception {
			if(notificationMapper.save(mNotificationBean) > 0){
				MessageNotification messageNotification = new JPushMessageNotificationImpl();
				//logger.info("NotificationToUserId:"+mNotificationBean.getToUserId());
				deleteNoReadMessagesNumber(mNotificationBean.getToUserId());
				//发送消息不成功
				if(!messageNotification.sendToAlias("leedane_user_"+mNotificationBean.getToUserId(), mNotificationBean.getContent(), mNotificationBean.getType())){
					mNotificationBean.setPushError(true);
					return notificationMapper.update(mNotificationBean) > 0;

				}else{
					return true;
				}
			}
			return false;
		} 
		
	}
	
	/**
	 * 发送广播
	 * @param broadcast
	 */
	public boolean sendBroadcast(String broadcast) {
		MessageNotification messageNotification = new JPushMessageNotificationImpl();
		messageNotification.sendToAllUser(broadcast);
		return true;
	}

	/**
	 * 获取未读消息的总数
	 * @param userId
	 * @return
	 */
	public KeyValuesBean getNoReadMessagesNumber(long userId) {
		//deleteNoReadMessagesNumber(userId);
		String key = getNoReadMessageKey(userId);
		Object obj = systemCache.getCache(key);
		KeyValuesBean messages = null;
		
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					messages =  (KeyValuesBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CircleUserPostsBean.class);
					if(messages != null){
						systemCache.addCache(key, messages, true);
						return messages;
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						messages = new KeyValuesBean();
						messages.setData(notificationMapper.noReadNumber(userId, false, ConstantsUtil.STATUS_NORMAL));
						if(CollectionUtil.isNotEmpty(messages.getData())){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(messages));
								systemCache.addCache(key, messages, true);
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
				messages = new KeyValuesBean();
				messages.setData(notificationMapper.noReadNumber(userId, false, ConstantsUtil.STATUS_NORMAL));
				if(CollectionUtil.isNotEmpty(messages.getData())){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(messages));
						systemCache.addCache(key, messages, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			messages = (KeyValuesBean)obj;
		}
		return messages;
	}
	
	/**
	 * 删除未读消息的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteNoReadMessagesNumber(long userId){
		String key = getNoReadMessageKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	public static String getNoReadMessageKey(long userId){
		return ConstantsUtil.MESSAGE_REDIS + userId;
	}
}
