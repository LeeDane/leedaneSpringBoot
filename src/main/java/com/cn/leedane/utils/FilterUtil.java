package com.cn.leedane.utils;

import java.util.Map;
import java.util.Set;

import com.cn.leedane.utils.sensitiveWord.SensitivewordFilter;

/**
 * 内容过滤工具类
 * @author LeeDane
 * 2016年7月12日 上午10:27:56
 * Version 1.0
 */
public class FilterUtil {

	/**
	 * 过滤
	 * @param content
	 * @param message
	 * @return
	 */
	public static boolean filter(String content, Map<String, Object> message){
		//检测敏感词
		SensitivewordFilter filter = new SensitivewordFilter();
		long beginTime = System.currentTimeMillis();
		Set<String> set = filter.getSensitiveWord(content, 1);
		if(set.size() > 0){
			message.put("message", "有敏感词"+set.size()+"个："+set.toString());
			message.put("responseCode", EnumUtil.ResponseCode.系统检测到有敏感词.value);
			long endTime = System.currentTimeMillis();
			System.out.println("总共消耗时间为：" + (endTime - beginTime));
			return true;
		}
		
		//过滤掉emoji
		//content = EmojiUtil.filterEmoji(content);
		return false;
	}
	
	/**
	 * 过滤是否含有敏感词汇并且过滤emoji表情
	 * @param content
	 * @param message
	 * @return
	 */
	public static boolean filter(String content){
		//检测敏感词
		SensitivewordFilter filter = new SensitivewordFilter();
		long beginTime = System.currentTimeMillis();
		Set<String> set = filter.getSensitiveWord(content, 1);
		if(set.size() > 0){
			long endTime = System.currentTimeMillis();
			System.out.println("总共消耗时间为：" + (endTime - beginTime));
			return true;
		}
		
		//过滤掉emoji
		content = EmojiUtil.filterEmoji(content);
		return false;
	}
	
	/**
	 * 过滤是否含有敏感词汇
	 * @param content
	 * @param sensitiveWords
	 */
	public static Set<String> getFilter(String content){
		//检测敏感词
		return new SensitivewordFilter().getSensitiveWord(content, 1);
	}
}
