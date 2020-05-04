package com.cn.leedane.utils;

import com.cn.leedane.handler.UserHandler;
import org.apache.log4j.Logger;

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
	private static Logger logger = Logger.getLogger(OptionUtil.class);
	/**
	 * 管理员账号
	 */
	public static UserBean adminUser = null;
	
	/**
	 * 计算热门圈子的时间
	 */
	public static int circleHostestBeforeDay = 0;

	static{
		SystemCache systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		//获取选项表中的管理员ID
		Object admin = systemCache.getCache("admin-id");
		int adminId = 1;
		if(admin == null){
			logger.info("系统管理员账号ID为空,已启动ID为1的用户做为默认管理员账号");
		}else {
			adminId = StringUtil.changeObjectToInt(admin);
		}
		circleHostestBeforeDay = StringUtil.changeObjectToInt(systemCache.getCache("circle-hostest-before-day"));
		//默认是倒数7天的圈子
		if(circleHostestBeforeDay == 0){
			circleHostestBeforeDay = -7;
		}
		
		@SuppressWarnings("unchecked")
		UserHandler userHandler = (UserHandler) SpringUtil.getBean("userHandler");
		adminUser = userHandler.getUserBean(adminId);
	}

}
