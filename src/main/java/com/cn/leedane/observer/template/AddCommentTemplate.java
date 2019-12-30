package com.cn.leedane.observer.template;

import com.cn.leedane.utils.EnumUtil;

/**
 * 增加评论的通知模板类
 * @author LeeDane
 * 2016年7月12日 下午1:51:12
 * Version 1.0
 */
public class AddCommentTemplate implements NotificationTemplate {

	@Override
	public EnumUtil.NoticeSMSType getNotifitionType() {
		return EnumUtil.NoticeSMSType.ADD_COMMENT;
	}

	@Override
	public String getNotifitionContent() {
		return "{}{to_user_remark}更新了说说";
	}

}
