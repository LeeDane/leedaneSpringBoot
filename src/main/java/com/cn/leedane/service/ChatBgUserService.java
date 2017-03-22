package com.cn.leedane.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;

/**
 * 聊天背景与用户关系相关service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:31:22
 * Version 1.0
 */
@Transactional("txManager")
public interface ChatBgUserService <T extends IDBean>{
	
	/**
	 * 判断是否下载过
	 * @param userId
	 * @param chatBgTableId
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean exists(int userId, int chatBgTableId);
}
