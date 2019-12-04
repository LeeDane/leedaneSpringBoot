package com.cn.leedane.message;

import com.cn.leedane.message.notification.Notification;

/**
 * 发送通知的接口
 * @author LeeDane
 * 2016年7月12日 下午2:22:31
 * Version 1.0
 */
public interface ISendNotification {
	public boolean Send(Notification notification);
}
