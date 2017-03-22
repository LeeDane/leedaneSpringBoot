package com.cn.leedane.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.cn.leedane.mapper.RolesMapper;
import com.cn.leedane.model.RolesBean;
import com.cn.leedane.service.RolesService;

/**
 * 角色相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:26:39
 * Version 1.0
 */

public class RolesTest extends BaseTest {
	
	@Resource
	private RolesMapper rolesMapper;
	
	@Test
	public void addCompany() throws Exception{
		RolesBean roles = rolesMapper.findById(RolesBean.class, 1);
		rolesMapper.save(roles);
	}	
}
