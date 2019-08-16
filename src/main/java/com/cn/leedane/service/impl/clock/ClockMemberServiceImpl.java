package com.cn.leedane.service.impl.clock;

import com.cn.leedane.display.clock.ClockMemberDisplay;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockDynamicHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.handler.clock.ClockMemberHandler;
import com.cn.leedane.mapper.clock.ClockInMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.mapper.clock.ClockMemberMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.model.clock.ClockScoreBean;
import com.cn.leedane.model.clock.ClockScoreQueueBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.ClockScoreThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.ClockScoreBusinessType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	private ClockInMapper clockInMapper;
	
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
	
	@Autowired
	private ClockDynamicHandler clockDynamicHandler;
	
	@Autowired
	private ClockMemberHandler clockMemberHandler;
	
	@Override
	public boolean add(int clockId, int memberId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockMemberServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		//校验
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		
		if(memberId < 1){
			throw new ParameterUnspecificationException("成员ID不能为空");
		}
		
		Date systemDate = new Date();
		int userId = user.getId();
		ClockMemberBean clockMemberBean = new ClockMemberBean();
		clockMemberBean.setClockId(clockId);
		clockMemberBean.setMemberId(memberId);
		clockMemberBean.setRemind(JsonUtil.getStringValue(jo, "remind")); //设置提醒时间
		clockMemberBean.setNotification(JsonUtil.getBooleanValue(jo, "notification", true)); //设置是否接受通知
		clockMemberBean.setCreateTime(systemDate);
		clockMemberBean.setCreateUserId(userId);
		clockMemberBean.setModifyTime(systemDate);
		clockMemberBean.setModifyUserId(userId);
		clockMemberBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		boolean	result = clockMemberMapper.save(clockMemberBean) > 0;
		
		//保存成功
		if(result){
			clockMemberHandler.deleteClockMembersCache(clockId);
			//非创建者请求加入，扣除加入者积分和创建者积分
			if(clockBean.getCreateUserId() != memberId){
				
				//通知创建者
				ClockScoreQueueBean clockScoreQueueBean = new ClockScoreQueueBean();
				ClockScoreBean clockScoreBean = new ClockScoreBean();
				clockScoreBean.setClockId(clockId);
				clockScoreBean.setCreateTime(systemDate);
				clockScoreBean.setCreateUserId(userId);
				clockScoreBean.setModifyTime(systemDate);
				clockScoreBean.setModifyUserId(userId);
				clockScoreBean.setScore(-clockBean.getRewardScore());
				clockScoreBean.setScoreDate(systemDate);
				clockScoreBean.setScoreDesc(userHandler.getUserName(memberId) +"加入您的任务《" + clockBean.getTitle() +"》，预扣除"+ clockBean.getRewardScore() +"积分");
				clockScoreBean.setBusinessType(ClockScoreBusinessType.成员加入.value);
				clockScoreQueueBean.setClockScoreBean(clockScoreBean);
				clockScoreQueueBean.setOperateType(EnumUtil.ClockScoreOperateType.新增.value);
				new ThreadUtil().singleTask(new ClockScoreThread(user, clockScoreQueueBean));
				
				//通知加入者
				clockScoreBean.setScore(clockBean.getRewardScore());
				clockScoreBean.setCreateUserId(memberId);
				clockScoreBean.setModifyUserId(memberId);
				clockScoreBean.setScoreDesc("加入任务《" + clockBean.getTitle() +"》，预扣除"+ clockBean.getRewardScore() +"积分");
				new ThreadUtil().singleTask(new ClockScoreThread(user, clockScoreQueueBean));
			}
			
			//保存动态信息
			clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), clockDynamicHandler.getUserName(user, memberId) + "加入任务", true,EnumUtil.CustomMessageExtraType.其他未知类型.value);
				
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为任务ID为", clockId , "添加新的成员："+ memberId +"，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return result;
	}


	@Override
	public Map<String, Object> update(int clockId, int memberId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockMemberServiceImpl-->update(): clockId="+ clockId +", memberId="+ memberId +",jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		ClockMemberBean clockMemberBean = clockMemberMapper.findClockMember(clockId, memberId);
		if(clockMemberBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			message.put("message", "还不是该任务的成员，无法修改信息！");
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
			return message.getMap();
		}

		clockMemberBean.setRemind(jo.optString("remind"));
		clockMemberBean.setNotification(jo.optBoolean("notification"));
		clockMemberBean.setModifyUserId(user.getId());
		clockMemberBean.setModifyTime(new Date());
		boolean result = clockMemberMapper.update(clockMemberBean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", "信息修改成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", "信息修改失败！");
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int clockId, int memberId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockMemberServiceImpl-->delete():clockId=" +clockId +", memberId="+ memberId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		return message.getMap();
	}

	
	@Override
	public Map<String, Object> members(int clockId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockMemberServiceImpl-->members():clockId = "+ clockId +",userId=" +user.getId() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法获取成员列表。");
		List<Map<String, Object>> members = clockMemberMapper.membersSortByIns(clockId, 0);
		List<ClockMemberDisplay> displays = new ArrayList<ClockMemberDisplay>();
		if(CollectionUtil.isNotEmpty(members)){
			ClockMemberDisplay display;
			for(Map<String, Object> member: members){
				int memberId = StringUtil.changeObjectToInt(member.get("member_id"));
				display = new ClockMemberDisplay();
				display.setAccount(userHandler.getUserName(memberId));
				display.setCreateUserId(memberId);
				display.setCreateTime(StringUtil.changeNotNull(member.get("create_time")));
				display.setPicPath(userHandler.getUserPicPath(memberId, "30x30"));
				display.setClockInNumber(StringUtil.changeObjectToInt(member.get("number")));
				displays.add(display);
			}
		}
		message.put("message", displays);
		message.put("isSuccess", true);
		//保存动态信息
		clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), "查看任务成员列表", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		return message.getMap();
	}
}
