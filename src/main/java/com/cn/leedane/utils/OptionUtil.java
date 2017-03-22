package com.cn.leedane.utils;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;

/**
 * 选项配置静态类
 * @author LeeDane
 * 2016年12月1日 上午11:59:45
 * Version 1.0
 */
public class OptionUtil {
	
	public static UserBean adminUser = null;
	
	static{
		SystemCache systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		//获取选项表中的管理员ID
		Object admin = systemCache.getCache("admin-id");
		int adminId = 1;
		if(admin == null){
			System.out.println("系统管理员账号ID为空,已启动ID为1的用户做为默认管理员账号");
		}else {
			adminId = StringUtil.changeObjectToInt(admin);
		}
		
		@SuppressWarnings("unchecked")
		UserService<UserBean> userService = (UserService<UserBean>) SpringUtil.getBean("userService");
		adminUser = userService.findById(adminId);
	}

}
