package com.cn.leedane.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.RoleBean;

/**
 * 链接权限或者角色service接口类
 * @author LeeDane
 * 2017年4月20日 下午5:48:09
 * Version 1.0
 */
@Transactional("txManager")
public interface LinkRoleOrPermissionService<T extends IDBean>{
	
	
}
