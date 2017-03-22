package com.cn.leedane.wechat.service.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.wechat.service.BaseXMLWechatService;
import com.cn.leedane.wechat.util.HttpRequestUtil;

public class TranslationXMLService extends BaseXMLWechatService {

	public TranslationXMLService(HttpServletRequest request, Map<String, String> map) {
		super(request, map);
	}
	@Override
	protected String execute() {
		String r = "";	
		try {			
			if(Content.startsWith("翻译")) {
				Content = Content.substring(2, Content.length());	
				if(StringUtil.isNull(Content)){
					r = "进入翻译模式";
				}else{					
					r = HttpRequestUtil.sendAndRecieveFromYoudao(Content);
					r = StringUtil.getYoudaoFanyiContent(r);
				}
			}else{
				r = HttpRequestUtil.sendAndRecieveFromYoudao(Content);
				r = StringUtil.getYoudaoFanyiContent(r);
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return r;
	}
}
