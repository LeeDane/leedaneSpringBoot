package com.cn.leedane.observer.template;

import com.cn.leedane.utils.EnumUtil;

/**
 * 通知的模板类
 * @author LeeDane
 * 2016年7月12日 下午1:51:19
 * Version 1.0
 */
public interface NotificationTemplate {
	
	public EnumUtil.NoticeSMSType getNotifitionType();
	
	/**
	 * 其中支持的参数{to_user_id}, {from_user_id}, {to_user_remark}, {from_user_remark}
	 */
	public String getNotifitionContent();

}
