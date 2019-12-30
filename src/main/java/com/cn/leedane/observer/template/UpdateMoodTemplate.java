package com.cn.leedane.observer.template;

import com.cn.leedane.utils.EnumUtil;

/**
 * 更新心情的通知模板类
 * @author LeeDane
 * 2016年7月12日 下午1:51:25
 * Version 1.0
 */
public class UpdateMoodTemplate implements NotificationTemplate {

	@Override
	public EnumUtil.NoticeSMSType getNotifitionType() {
		return EnumUtil.NoticeSMSType.PUBLISHED_MOOD;
	}

	@Override
	public String getNotifitionContent() {
		return "您的好友{to_user_remark}更新了说说";
	}

}
