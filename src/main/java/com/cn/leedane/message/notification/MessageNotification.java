package com.cn.leedane.message.notification;

import java.util.Set;

/**
 * 信息发送的接口
 * @author LeeDane
 * 2016年7月12日 下午2:21:58
 * Version 1.0
 */
public interface MessageNotification {
	
	/**
	 * 把通知发送给指定的设备
	 * @param registrationIds
	 * @param content
	 * @return
	 */
	public boolean sendToRegistrationIDs(Set<String> registrationIds, String content);
	
	/**
	 * 把通知发送给指定的用户
	 * @param alias  用户的别名
	 * @param content  发送的内容
	 * @return
	 */
	public boolean sendToAlias(String alias, String content);
	
	/**
	 * 把通知发送给指定的标签(关注该标签的全部用户)
	 * @param tag  用户的别名
	 * @param content  发送的内容
	 * @return
	 */
	public boolean sendToTag(String tag, String content);
	
	/**
	 * 把通知发送给全部的系统用户
	 * @param content  发送的内容
	 * @return
	 */
	public boolean sendToAllUser(String content);

}
