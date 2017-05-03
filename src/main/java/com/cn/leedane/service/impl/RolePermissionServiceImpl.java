package com.cn.leedane.service.impl;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.handler.RolePermissionHandler;
import com.cn.leedane.model.RoleBean;
import com.cn.leedane.model.UserRoleBean;
import com.cn.leedane.service.RolePermissionService;

/**
 * 用户角色service实现类
 * @author LeeDane
 * 2017年4月10日 上午10:29:31
 * version 1.0
 */

@Service("rolePermissionService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class RolePermissionServiceImpl implements RolePermissionService<UserRoleBean> {
	
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private RolePermissionHandler rolePermissionHandler;

	@Override
	public List<RoleBean> getUserRoleBeans(int userid) {
		logger.info("UserRoleServiceImpl-->getUserRoleBeans():userid="+userid);
		return rolePermissionHandler.getUserRoleBeans(userid);
	}
}