package com.cn.leedane.service.impl;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.mapper.UserTokenMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserTokenBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserTokenService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SqlUtil;

/**
 * 用户与token关系service实现类
 * @author LeeDane
 * 2017年3月24日 下午1:45:44
 * version 1.0
 */

@Service("userTokenService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class UserTokenServiceImpl implements UserTokenService<UserTokenBean> {
	
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UserTokenMapper userTokenMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public UserTokenBean getUserToken(int createUserId, String token) {
		logger.info("UserTokenServiceImpl-->checkToken():createUserId="+createUserId+",token="+token);
		return userTokenMapper.getUserToken(createUserId, ConstantsUtil.STATUS_NORMAL, token, new Date());
	}
	
	

}