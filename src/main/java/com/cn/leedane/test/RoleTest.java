package com.cn.leedane.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.cn.leedane.mapper.RoleMapper;
import com.cn.leedane.model.RoleBean;

/**
 * 角色相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:26:39
 * Version 1.0
 */

public class RoleTest extends BaseTest {
	
	@Resource
	private RoleMapper roleMapper;
	
	@Test
	public void addCompany() throws Exception{
		RoleBean role = roleMapper.findById(RoleBean.class, 1);
		roleMapper.save(role);
	}	
}
