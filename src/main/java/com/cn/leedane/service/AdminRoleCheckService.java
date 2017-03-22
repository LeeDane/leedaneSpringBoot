package com.cn.leedane.service;

import com.cn.leedane.model.UserBean;

/**
 * 管理员身份证校验
 * @author LeeDane
 * 2016年12月22日 下午12:05:43
 * Version 1.0
 */

public class AdminRoleCheckService {
	public boolean checkAdmin(UserBean user, int createUserId){
		return user.isAdmin() || (createUserId > 0 && createUserId == user.getId());
	}
}
