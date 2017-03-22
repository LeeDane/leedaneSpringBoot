package com.cn.leedane.message.notification;

import java.util.Set;

/**
 * 自定义信息发送的接口
 * @author LeeDane
 * 2016年7月12日 下午2:21:31
 * Version 1.0
 */
public interface CustomMessage {
	
	/**
	 * 把自定义小消息发送给指定的设备
	 * @param registrationIds 设备注册ID
	 * @param content  发送的内容
	 * @param key  extra的键
	 * @param value extra的值
	 * @return
	 */
	public boolean sendToRegistrationIDs(Set<String> registrationIds, String content, String key, String value);
	
	/**
	 * 把自定义小消息发送给指定的用户
	 * @param alias 用户的别名
	 * @param content 发送的内容
	 * @param key  extra的键
	 * @param value  extra的值
	 * @return
	 */
	public boolean sendToAlias(String alias, String content, String key, String value);

	/**
	 * 把自定义小消息发送给指定的标签(关注该标签的全部用户)
	 * @param tag
	 * @param content 发送的内容
	 * @param key  extra的键
	 * @param value  extra的值
	 * @return
	 */
	public boolean sendToTag(String tag, String content, String key, String value);

	/**
	 * 把自定义小消息发送给全部的系统用户
	 * @param content 发送的内容
	 * @param key  extra的键
	 * @param value  extra的值
	 * @return
	 */
	public boolean sendToAllUser(String content, String key, String value);

}
