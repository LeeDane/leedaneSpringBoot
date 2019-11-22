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
 * 自定义消息的处理类
 * @author LeeDane
 * 2018年10月16日 下午3:13:56
 * version 1.0
 */
@Component
public class CustomMessageHandler {
	
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
	 * 发送聊天的自定义消息
	 * @param user
	 * @param toUserId
	 * @param chatMap
	 */
	public void sendCustomMessageById(UserBean user, int toUserId, Map<String, Object> chatMap){
		if(user.getId() == toUserId){
			logger.error("自己不能给自己发信息");
			return;
		}
		
		CustomMessage customMessage = new JpushCustomMessage();	
		customMessage.sendToAlias("leedane_user_"+toUserId, JSONObject.fromObject(chatMap).toString(), EnumUtil.CustomMessageExtraType.聊天.value);	
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
	public KeyValuesBean getNoReadMessagesNumber(int userId) {
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
