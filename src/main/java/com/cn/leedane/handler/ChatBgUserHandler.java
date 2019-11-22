package com.cn.leedane.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.ChatBgUserMapper;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 聊天背景与用户下载关系处理类
 * @author LeeDane
 * 2016年7月12日 上午11:53:46
 * Version 1.0
 */
@Component
public class ChatBgUserHandler {
	@Autowired
	private ChatBgUserMapper chatBgUserMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private UserHandler userHandler;
	
	/**
	 * 用户是否已经下载过该聊天背景资源
	 * @param userId 用户Id
	 * @param chatBgTableId  聊天背景资源的ID
	 * @return
	 */
	public boolean isDownload(long userId, int chatBgTableId){
		String chatBgUserKey = getChatBgUserKey(userId, chatBgTableId);
		boolean result = false;
		//还没有缓存记录
		if(!redisUtil.hasKey(chatBgUserKey)){
			result = chatBgUserMapper.executeSQL("select id from "+DataTableType.聊天背景与用户.value+" where create_user_id=? and chat_bg_table_id=?", userId, chatBgTableId).size() > 0;			
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
	public static String getChatBgUserKey(long userId, long tableId){
		return ConstantsUtil.CHAT_BG_USER +userId +"_" + tableId;
	}
}
