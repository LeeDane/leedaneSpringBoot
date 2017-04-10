package com.cn.leedane.service.impl;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.mapper.UserRoleMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.RoleBean;
import com.cn.leedane.model.UserRoleBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserRoleService;
import com.cn.leedane.utils.ConstantsUtil;

/**
 * 用户角色service实现类
 * @author LeeDane
 * 2017年4月10日 上午10:29:31
 * version 1.0
 */

@Service("userRoleService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class UserRoleServiceImpl implements UserRoleService<UserRoleBean> {
	
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;


	@Override
	public List<RoleBean> getUserRoleBeans(int userid) {
		logger.info("UserRoleServiceImpl-->getUserRoleBeans():userid="+userid);
		return userRoleMapper.getUserRoleBeans(userid, ConstantsUtil.STATUS_NORMAL);
	}
}