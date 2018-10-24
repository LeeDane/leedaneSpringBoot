package com.cn.leedane.service.impl.clock;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.mapper.clock.ClockMemberMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * 任务提醒service实现类
 * @author LeeDane
 * 2018年8月29日 下午5:34:53
 * version 1.0
 */
@Service("clockMemberService")
public class ClockMemberServiceImpl extends AdminRoleCheckService implements ClockMemberService<ClockMemberBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ClockMemberMapper clockMemberMapper;
	
	@Autowired
	private ClockMapper clockMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private ClockHandler clockHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Override
	public boolean add(int clockId, int memberId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockMemberServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		ClockBean clockBean = clockMapper.findById(ClockBean.class, clockId);
		//校验
		if(clockBean == null){
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该提醒任务不存在或者不支持共享.value));
		}
		
		if(memberId < 1){
			throw new ParameterUnspecificationException("成员ID不能为空");
		}
		ClockMemberBean clockMemberBean = new ClockMemberBean();
		clockMemberBean.setClockId(clockId);
		clockMemberBean.setMemberId(memberId);
		clockMemberBean.setRemind(JsonUtil.getStringValue(jo, "remind")); //设置提醒时间
		clockMemberBean.setNotification(JsonUtil.getBooleanValue(jo, "notification", true)); //设置是否接受通知
		clockMemberBean.setCreateTime(new Date());
		clockMemberBean.setCreateUserId(user.getId());
		clockMemberBean.setModifyTime(new Date());
		clockMemberBean.setModifyUserId(user.getId());
		clockMemberBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		boolean	result = clockMemberMapper.save(clockMemberBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为任务ID为", clockId , "添加新的成员："+ memberId +"，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);		
		return result;
	}


	@Override
	public Map<String, Object> update(int clockId, int memberId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockMemberServiceImpl-->update(): clockId="+ clockId +", memberId="+ memberId +",jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int clockId, int memberId, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockMemberServiceImpl-->delete():clockId=" +clockId +", memberId="+ memberId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		return message.getMap();
	}

	
	@Override
	public Map<String, Object> members(int clockId, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockMemberServiceImpl-->members():clockId = "+ clockId +",userId=" +user.getId() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		message.put("isSuccess", true);
		message.put("message", clockMemberMapper.members(clockId));
		return message.getMap();
	}
}
