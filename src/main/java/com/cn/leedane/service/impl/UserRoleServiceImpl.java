package com.cn.leedane.service.impl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cn.leedane.model.UserRoleBean;
import com.cn.leedane.service.UserRoleService;

/**
 * 用户角色service实现类
 * @author LeeDane
 * 2016年7月12日 下午2:17:51
 * Version 1.0
 */
@Service("userRoleService")
public class UserRoleServiceImpl implements UserRoleService<UserRoleBean> {
	Logger logger = Logger.getLogger(getClass());
}
