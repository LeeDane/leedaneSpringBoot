package com.cn.leedane.service.impl;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.UserTokenMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.UserTokenBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserTokenService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

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
	private UserHandler userHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public UserTokenBean getUserToken(UserBean user, String token, HttpServletRequest request) {
		logger.info("UserTokenServiceImpl-->getUserToken():createUserId="+ user.getId() +",token="+token);
		if(!userHandler.hasToken(user.getId(), token)){
			return userTokenMapper.getUserToken(user.getId(), ConstantsUtil.STATUS_NORMAL, token, DateUtil.getCurrentTime());
		}
		
		UserTokenBean userTokenBean = new UserTokenBean();
		userTokenBean.setToken(token);
		userTokenBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		return userTokenBean;
	}

	@Override
	public ResponseMap addUserToken(UserBean user, String token,
			Date overdue, HttpServletRequest request) {
		logger.info("UserTokenServiceImpl-->getUserToken():createUserId="+user.getId()+",token="+token);
		ResponseMap message = new ResponseMap();
		UserTokenBean userTokenBean = new UserTokenBean();
		userTokenBean.setToken(token);
		userTokenBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		userTokenBean.setCreateUserId(user.getId());
		userTokenBean.setOverdue(overdue);
		userTokenBean.setCreateTime(DateUtil.getCurrentTime());
		boolean result = userTokenMapper.save(userTokenBean) > 0;
		
		if(result){
			if(userHandler.addTokenCode(userTokenBean)){
				message.put("isSuccess", true);
				//保存操作日志
				operateLogService.saveOperateLog(user, request, null, "用户id为"+ user.getId()+"添加token成功", "addUserToken()", ConstantsUtil.STATUS_NORMAL, 0);
				message.put("message", "添加token成功！");
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);	
				return message;
			}
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, "用户id为"+ user.getId()+"添加token失败", "addUserToken()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message;
	}
}