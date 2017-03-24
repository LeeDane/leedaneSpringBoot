package com.cn.leedane.service;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.UserTokenBean;

/**
 * 用户与token关系service接口类
 * @author LeeDane
 * 2017年3月24日 下午1:43:39
 * version 1.0
 */
@Transactional("txManager")
public interface UserTokenService<T extends UserTokenBean>{
	public UserTokenBean getUserToken(int createUserId, String token);
}
