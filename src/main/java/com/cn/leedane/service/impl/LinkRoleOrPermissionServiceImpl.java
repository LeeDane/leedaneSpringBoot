package com.cn.leedane.service.impl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.mapper.LinkRoleOrPermissionMapper;
import com.cn.leedane.model.LinkRoleOrPermissionBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.LinkRoleOrPermissionService;
import com.cn.leedane.service.OperateLogService;

/**
 * 链接权限或者角色service实现类
 * @author LeeDane
 * 2017年4月20日 下午5:50:07
 * Version 1.0
 */

@Service("linkRoleOrPermissionService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class LinkRoleOrPermissionServiceImpl implements LinkRoleOrPermissionService<LinkRoleOrPermissionBean> {
	
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private LinkRoleOrPermissionMapper linkRoleOrPermissionMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;


}