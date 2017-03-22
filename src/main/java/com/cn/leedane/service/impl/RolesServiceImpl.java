package com.cn.leedane.service.impl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cn.leedane.model.RolesBean;
import com.cn.leedane.service.RolesService;

/**
 * 角色service实现类
 * @author LeeDane
 * 2016年7月12日 下午2:08:06
 * Version 1.0
 */
@Service("rolesService")
public class RolesServiceImpl implements RolesService<RolesBean> {
	Logger logger = Logger.getLogger(getClass());
}
