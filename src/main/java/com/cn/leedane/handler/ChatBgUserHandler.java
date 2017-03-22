package com.cn.leedane.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.model.ChatBgUserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.ChatBgUserService;

/**
 * 聊天背景与用户下载关系处理类
 * @author LeeDane
 * 2016年7月12日 上午11:53:46
 * Version 1.0
 */
@Component
public class ChatBgUserHandler {
	@Autowired
	private ChatBgUserService<ChatBgUserBean> chatBgUserService;
	
	public void setChatBgUserService(
			ChatBgUserService<ChatBgUserBean> chatBgUserService) {
		this.chatBgUserService = chatBgUserService;
	}
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private UserHandler userHandler;
	
	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}
	

	/**
	 * 用户是否已经下载过该聊天背景资源
	 * @param userId 用户Id
	 * @param chatBgTableId  聊天背景资源的ID
	 * @return
	 */
	public boolean isDownload(int userId, int chatBgTableId){
		String chatBgUserKey = getChatBgUserKey(userId, chatBgTableId);
		boolean result = false;
		//还没有缓存记录
		if(!redisUtil.hasKey(chatBgUserKey)){
			result = chatBgUserService.exists(userId, chatBgTableId);
			if(result)
				redisUtil.addString(chatBgUserKey, "true");
			else
				redisUtil.addString(chatBgUserKey, "false");
		}else{
			String val = redisUtil.getString(chatBgUserKey);
			if(StringUtil.isNotNull(val)){
				result = StringUtil.changeObjectToBoolean(val);
			}
		}
		return result;
	}
	
	/**
	 * 保存下载记录
	 * @param userId
	 * @param chatBgTableId
	 */
	/*public void addDownload(int userId, int chatBgTableId){
		String chatBgUserKey = getChatBgUserKey(userId, chatBgTableId);
		//还没有缓存记录
		if(!redisUtil.hasKey(chatBgUserKey)){
			boolean result = chatBgUserService.exists(userId, chatBgTableId);
			if(!result){//还没有下载记录
				redisUtil.addString(chatBgUserKey, "true");
				chatBgUserService.save(t);
			}
		}else{
			String val = redisUtil.getString(chatBgUserKey);
			if(StringUtil.isNotNull(val)){
				if(!StringUtil.changeObjectToBoolean(val)){
					chatBgUserService.save(t);
					redisUtil.addString(chatBgUserKey, "true");
				}
			}
		}
	}*/
	
	/**
	 * 获取用户与聊天背景资源下载在redis的key
	 * @param userId
	 * @param tableId
	 * @return
	 */
	public static String getChatBgUserKey(int userId, int tableId){
		return ConstantsUtil.CHAT_BG_USER +userId +"_" + tableId;
	}
}
