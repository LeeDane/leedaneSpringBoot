package com.cn.leedane.service.impl.clock;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.display.clock.ClockMemberDisplay;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.mapper.clock.ClockDealMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.mapper.clock.ClockMemberMapper;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockDealBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockDealService;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 任务成员关系service实现类
 * @author LeeDane
 * 2018年8月29日 下午5:34:53
 * version 1.0
 */
@Service("clockDealService")
public class ClockDealServiceImpl extends AdminRoleCheckService implements ClockDealService<ClockDealBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ClockMemberMapper clockMemberMapper;
	
	@Autowired
	private ClockDealMapper clockDealMapper;
	
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
	private ClockMemberService<ClockMemberBean> clockMemberService;
	
	@Override
	public Map<String, Object> add(int clockId, int memberId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		ClockBean clockBean = clockMapper.findById(ClockBean.class, clockId);
		//校验
		if(clockBean == null){
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该提醒任务不存在或者不支持共享.value));
		}
		
		if(memberId < 1){
			throw new ParameterUnspecificationException("成员ID不能为空");
		}
		//自己不能加入自己的任务
		if(memberId == clockBean.getCreateUserId())
			throw new ParameterUnspecificationException("您自己的任务，不支持此操作！");
		
		ResponseMap message = new ResponseMap();
		ClockDealBean clockDealBean = new ClockDealBean();
		//获取当前的操作状态
		int newStatus = jo.optInt("new_status");
				
		//非创建人
		if(memberId != clockBean.getCreateUserId()){
			//判断目前是否共享
			if(!clockBean.isShare()){
				throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该任务目前不支持共享.value));
			}
			
			Date now = DateUtil.stringToDate(DateUtil.DateToString(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
			//判断目前只是过期
			if(clockBean.getApplyEndDate() != null && clockBean.getApplyEndDate().getTime() < now.getTime()){
				throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.已超出任务的参与日期.value));
			}
			
			//判断是否超员
			List<ClockMemberBean> members = clockMemberMapper.members(clockId);
			//
			if(clockBean.getTakePartNumber() < members.size()){
				throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.已超出任务的参与人数.value));
			}
			clockDealBean.setStatus(jo.optInt("status"));
			clockDealBean.setNewStatus(jo.optInt("status"));
		}else{
			clockDealBean.setNewStatus(newStatus);
		}
		
		
		boolean result = false;
		if(newStatus != ConstantsUtil.STATUS_MANAGE_AGREE){//不是管理员同意
			clockDealBean.setClockId(clockId);
			clockDealBean.setMemberId(memberId);
			clockDealBean.setCreateTime(new Date());
			clockDealBean.setCreateUserId(user.getId());
			clockDealBean.setModifyTime(new Date());
			clockDealBean.setModifyUserId(user.getId());
			result = clockDealMapper.save(clockDealBean) > 0;
		}else{
			result = clockDealMapper.updateStatus(clockId, memberId, ConstantsUtil.STATUS_NORMAL);
			
			//在成员表中保存成员加入记录
			if(result)
				result = clockMemberService.add(clockId, memberId, jo, user, request);
			//保存基本信息
		}
		
		if(result){
			message.put("isSuccess", true);
			String content = null;
			String userName = userHandler.getUserName(memberId);
			if(newStatus != ConstantsUtil.STATUS_MANAGE_AGREE){
				if(memberId != clockBean.getCreateUserId()){
					content = "发送成功，等待任务创建者审核！";
					//给管理员发送有等待审核的任务人员加入信息
					Map<String, Object> mp = new HashMap<String, Object>();
					mp.put("clock_id", clockId);
					mp.put("clock_member_id", clockDealBean.getId());
					CustomMessage customMessage = new JpushCustomMessage();
					if(jo.getInt("status") == ConstantsUtil.STATUS_WAIT_MANAGE_AGREE){
						mp.put("content", userName +"请求加入您的任务《"+ clockBean.getTitle() +"》");
						customMessage.sendToAlias("leedane_user_"+ clockBean.getCreateUserId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.请求加入任务.value);
					}
					
					if(jo.getInt("status") == ConstantsUtil.STATUS_WAIT_MEMBER_AGREE){
						mp.put("content", userName +"邀请您加入任务《"+ clockBean.getTitle() +"》");
						customMessage.sendToAlias("leedane_user_"+ clockBean.getCreateUserId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.邀请加入任务.value);
					}
					
				}else{
					content = "操作成功！";
				}
			}else{
				content = "已经审核通过！";
				//通知其他成员，其中当前的成员和非当前的成员
				List<ClockMemberBean> members = clockMemberMapper.members(clockId);
				if(CollectionUtil.isNotEmpty(members)){
					for(ClockMemberBean member: members){
						//删除所有成员的缓存
						clockHandler.deleteDateClocksCache(member.getMemberId());
						if(member.getMemberId() != user.getId() && member.isNotification()){
							Map<String, Object> mp = new HashMap<String, Object>();
							if(member.getMemberId() == memberId){
								mp.put("content", "您已被同意加入任务《"+ clockBean.getTitle() +"》");
							}else{
								mp.put("content", userName +"加入任务《"+ clockBean.getTitle() +"》");
							}
							mp.put("clock_id", clockId);
							CustomMessage customMessage = new JpushCustomMessage();
							customMessage.sendToAlias("leedane_user_"+ member.getMemberId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.同意加入任务.value);
						}
					}
				}
			}
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为任务ID为", clockId , "添加新的成员："+ memberId +"，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);		
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(int clockId, int memberId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->update(): clockId="+ clockId +", memberId="+ memberId +",jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int clockId, int memberId, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->delete():clockId=" +clockId +", memberId="+ memberId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		return message.getMap();
	}

	@Override
	public Map<String, Object> requestAdd(int clockId, JSONObject json,
			UserBean user, HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->requestAdd():clockId = "+ clockId +",userId=" +user.getId() +", json=" +json.toString());
		json.put("status", ConstantsUtil.STATUS_WAIT_MANAGE_AGREE);
		return add(clockId, user.getId(), json, user, request);
	}


	@Override
	public Map<String, Object> requestAgree(int clockMemberId, JSONObject json,
			UserBean user, HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->requestAgree():clockId = "+ clockMemberId +",userId=" +user.getId() +", json=" +json.toString());
		
		ClockMemberBean member = clockMemberMapper.findById(ClockMemberBean.class, clockMemberId);
		if(member == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该人员还没有申请加入任务记录.value));
		
		if(member.getStatus() != ConstantsUtil.STATUS_WAIT_MANAGE_AGREE)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该任务申请记录不是等待创建者审批状态.value));
		
		json.put("new_status", ConstantsUtil.STATUS_MANAGE_AGREE);
		return add(member.getClockId(), member.getMemberId(), json, user, request);
	}


	@Override
	public Map<String, Object> addClocks(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->addClocks():userId=" +user.getId() +", json=" +json.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<ClockMemberDisplay> clockMemberDisplays = clockDealMapper.addClocks(user.getId(), start, pageSize);
		message.put("isSuccess", true);
		message.put("message", clockMemberDisplays);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> inviteClocks(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->inviteClocks():userId=" +user.getId() +", json=" +json.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<ClockMemberDisplay> clockMemberDisplays = clockDealMapper.inviteClocks(user.getId(), start, pageSize);
		message.put("isSuccess", true);
		message.put("message", clockMemberDisplays);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> myInviteClocks(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->myInviteClocks():userId=" +user.getId() +", json=" +json.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<ClockMemberDisplay> clockMemberDisplays = clockDealMapper.myInviteClocks(user.getId(), start, pageSize);
		message.put("isSuccess", true);
		message.put("message", clockMemberDisplays);
		return message.getMap();
	}


	@Override
	public Map<String, Object> agreeClocks(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockDealServiceImpl-->agreeClocks():userId=" +user.getId() +", json=" +json.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<ClockMemberDisplay> clockMemberDisplays = clockDealMapper.agreeClocks(user.getId(), start, pageSize);
		message.put("isSuccess", true);
		message.put("message", clockMemberDisplays);
		return message.getMap();
	}
}
