package com.cn.leedane.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;

import com.cn.leedane.controller.RoleController;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.EnumUtil;

/**
 * 管理员身份证校验
 * @author LeeDane
 * 2016年12月22日 下午12:05:43
 * Version 1.0
 */

public class AdminRoleCheckService {
	
	/**
	 * 检查是否有管理员账户权限
	 * @param user
	 * @param createUserId
	 */
	public void checkAdmin(UserBean user, int createUserId){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		//后台只有管理员权限才能操作
		if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && (createUserId > 0 && createUserId != user.getId())){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		}	
	}
	
	/**
	 * 检查是否有管理员账户权限
	 * @param user
	 */
	public void checkAdmin(UserBean user){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		//后台只有管理员权限才能操作
		if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE)){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value));
		}	
	}

	/**
	 * 判断是否有管理员的权限
	 * @return
	 */
	public boolean isAdmin(){
		//获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.hasRole(RoleController.ADMIN_ROLE_CODE);
	}
}
