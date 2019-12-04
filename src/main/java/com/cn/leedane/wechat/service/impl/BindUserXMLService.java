package com.cn.leedane.wechat.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cn.leedane.wechat.service.BaseXMLWechatService;
import com.cn.leedane.wechat.util.MessageUtil;

/**
 * 绑定用户链接封装的xml处理
 * @author LeeDane
 * 2016年4月7日 上午11:15:13
 * Version 1.0
 */
public class BindUserXMLService extends BaseXMLWechatService {
	
	
	public BindUserXMLService(HttpServletRequest request, Map<String, String> map) {
		super(request, map);
	}
	
	@Override
	protected String execute() {
		return MessageUtil.initBingLoginMessage(getBasePath() ,ToUserName,FromUserName,"sendmood");
	}
}
