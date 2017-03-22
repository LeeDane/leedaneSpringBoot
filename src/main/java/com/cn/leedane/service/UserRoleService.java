package com.cn.leedane.service;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;

/**
 * 用户角色service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:36:35
 * Version 1.0
 */
@Transactional("txManager")
public interface UserRoleService <T extends IDBean>{
	
}
