package com.cn.leedane.wechat.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.service.AppVersionService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.wechat.service.BaseXMLWechatService;

/**
 * 新版本app模式下的实现
 * @author LeeDane
 * 2016年6月6日 上午10:01:55
 * Version 1.0
 */
public class NewestAppXMLService extends BaseXMLWechatService {

	
	@Autowired
	private AppVersionService<FilePathBean> appVersionService;
	
	public void setAppVersionService(
			AppVersionService<FilePathBean> appVersionService) {
		this.appVersionService = appVersionService;
	}
	
	public NewestAppXMLService(){
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected String execute() {
		if(appVersionService == null){
			appVersionService = (AppVersionService<FilePathBean>) SpringUtil.getBean("appVersionService");
		}
		List<Map<String, Object>> list = appVersionService.getNewestVersion();
		if(list != null && list.size() ==1){
			String path = StringUtil.changeNotNull(list.get(0).get("path"));
			if(StringUtil.isNotNull(path)){
				return path;
			}
		}
		
		return "找不到上传的APP版本链接";
	}
}
