package com.cn.leedane.service;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;

/**
 * 角色service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:34:59
 * Version 1.0
 */
@Transactional("txManager")
public interface RolesService <T extends IDBean>{
	
}
