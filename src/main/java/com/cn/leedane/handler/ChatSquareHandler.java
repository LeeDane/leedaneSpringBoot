package com.cn.leedane.handler;

import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import org.springframework.stereotype.Component;

import com.cn.leedane.redis.config.RedisConfig;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 聊天广场处理类
 * @author LeeDane
 * 2017年2月17日 下午4:29:23
 * Version 1.0
 */
@Component
public class ChatSquareHandler {
	private RedisUtil redisUtil = RedisUtil.getInstance();

	/**
	 * 添加聊天广场的链接到Redis缓存中
	 * @param userKey
	 * @return
	 */
	public boolean addChat(String userKey){
		String chatKey = getChatKey(userKey);
		redisUtil.expire(chatKey, LeedanePropertiesConfig.newInstance().getInt("chatSquareTime"));
		return true;
	}
	
	
	/**
	 * 获取赞在redis的key
	 * @param userKey
	 * @return
	 */
	public static String getChatKey(String userKey){
		//return ConstantsUtil.CHAT_SQUARE_REDIS +userKey;
		return null;
	}
}
