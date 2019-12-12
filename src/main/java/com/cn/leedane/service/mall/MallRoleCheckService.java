package com.cn.leedane.service.mall;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;

import com.cn.leedane.controller.RoleController;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.EnumUtil;

/**
 * 商城管理员身份证校验
 * @author LeeDane
 * 2017年12月30日 上午9:07:09
 * version 1.0
 */

public class MallRoleCheckService {
	
	/**
	 * 检查是否有管理员/商城管理员账户权限
	 * @param user
	 * @param createUserId
	 */
	public void checkMallAdmin(UserBean user, long createUserId){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		//后台只有管理员权限才能操作
		if((!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && !currentUser.hasRole(RoleController.MALL_ROLE_CODE)) && (createUserId > 0 && createUserId != user.getId())){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		}	
	}
	
	/**
	 * 检查是否有管理员/商城管理员账户权限
	 * @param user
	 */
	public void checkMallAdmin(UserBean user){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		//后台只有管理员权限才能操作
		if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && !currentUser.hasRole(RoleController.MALL_ROLE_CODE)){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请使用有商城管理员权限的账号登录.value));
		}	
	}
}
