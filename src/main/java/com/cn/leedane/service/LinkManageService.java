package com.cn.leedane.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * 链接权限管理service接口类
 * @author LeeDane
 * 2017年4月10日 下午4:49:16
 * version 1.0
 */
@Transactional("txManager")
public interface LinkManageService<LinkManageBean>{
	
	/**
	 * 获取所有的链接(包括正常状态和非正常状态)
	 * @param user
	 * @return
	 */
	public List<LinkManageBean> getAllLinks();
}
