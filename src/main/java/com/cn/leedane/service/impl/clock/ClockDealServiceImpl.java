package com.cn.leedane.service.impl.clock;

import com.cn.leedane.display.clock.ClockMemberDisplay;
import com.cn.leedane.exception.IllegalOperationException;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockDynamicHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.handler.clock.ClockMemberHandler;
import com.cn.leedane.mapper.clock.ClockDealMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockDealBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockDealService;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private ClockMemberHandler clockMemberHandler;
	
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
	
	@Value("${constant.defalult.clock.again.request.add.time}")
    public int CLOCK_AGAIN_REQUEST_ADD_TIME;
	
	@Autowired
	private ClockDynamicHandler clockDynamicHandler;
	
	@Override
	public Map<String, Object> add(int clockId, int memberId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockDealServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		//校验
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		
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
			List<ClockMemberBean> members = clockMemberHandler.members(clockId);
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
		if(newStatus != ConstantsUtil.STATUS_NORMAL){//不是管理员同意
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
			if(newStatus != ConstantsUtil.STATUS_NORMAL){
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
				List<ClockMemberBean> members = clockMemberHandler.members(clockId);
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
			HttpRequestInfoBean request) {
		logger.info("ClockDealServiceImpl-->update(): clockId="+ clockId +", memberId="+ memberId +",jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int clockId, int memberId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockDealServiceImpl-->delete():clockId=" +clockId +", memberId="+ memberId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		return message.getMap();
	}

	@Override
	public Map<String, Object> addClocks(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
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
			HttpRequestInfoBean request) {
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
			HttpRequestInfoBean request) {
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
			HttpRequestInfoBean request) {
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

	@Override
	public Map<String, Object> requestAdd(int clockId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockDealServiceImpl-->requestAdd():clockId = "+ clockId +",userId=" +user.getId() +", json=" +json.toString());
//		json.put("status", ConstantsUtil.STATUS_WAIT_MANAGE_AGREE);
//		return add(clockId, user.getId(), json, user, request);
		
		//校验
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		checkClockCanDo(clockBean, user.getId());

		ResponseMap message = new ResponseMap();
		//获取请求记录
		ClockDealBean myClockDealBean = clockDealMapper.getMyRequestAddRecord(user.getId(), clockId);
		if(myClockDealBean != null){
			//判断修改时间, 10分钟才能请求一次
			if(DateUtil.isInMinutes(myClockDealBean.getModifyTime(), new Date(), CLOCK_AGAIN_REQUEST_ADD_TIME))
				throw new ParameterUnspecificationException("距离上次请求没超过"+ CLOCK_AGAIN_REQUEST_ADD_TIME + "分钟");
			
			//更新时间
			myClockDealBean.setModifyTime(new Date());
			boolean update = clockDealMapper.update(myClockDealBean) > 0;
			if(update){
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("content", user.getAccount() + "再次请求加入任务《"+ clockBean.getTitle() +"》");
				mp.put("clock_id", clockId);
				CustomMessage customMessage = new JpushCustomMessage();
				customMessage.sendToAlias("leedane_user_"+ clockBean.getCreateUserId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.请求加入任务.value);
				message.put("isSuccess", true);
				message.put("message", "您的请求已经发送，请耐心等待管理者审核！");
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
				return message.getMap();
			}else{
				message.put("isSuccess", false);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
				return message.getMap();
			}
		}else{
			boolean hasCreaterInvite = false;//判断是否有管理员的邀请记录
			List<ClockDealBean>  clockDealBeans= clockDealMapper.userClockRecord(user.getId(), clockId);
			if(CollectionUtil.isNotEmpty(clockDealBeans)){
				for(ClockDealBean clockDealBean: clockDealBeans){
					if(clockDealBean.getNewStatus() == ConstantsUtil.STATUS_NORMAL)
						throw new ParameterUnspecificationException("您已经在任务成员列表中，请勿重复操作！");
					
					//判断是否任务是自动加入或者有管理员的邀请记录
					if((clockBean.isAutoAdd() || clockDealBean.getCreateUserId() == clockBean.getCreateUserId()) && !hasCreaterInvite){
						hasCreaterInvite = true;
					}
				}
			}
			
			ClockDealBean clockDealBean = new ClockDealBean();
			clockDealBean.setClockId(clockId);
			clockDealBean.setMemberId(user.getId());
			clockDealBean.setCreateTime(new Date());
			clockDealBean.setCreateUserId(user.getId());
			clockDealBean.setModifyTime(new Date());
			clockDealBean.setModifyUserId(user.getId());
			
			boolean result = false;
			//有创建者的邀请记录，将直接调用同意加入
			if(hasCreaterInvite){
				clockDealBean.setStatus(ConstantsUtil.STATUS_WAIT_MANAGE_AGREE);
				clockDealBean.setNewStatus(ConstantsUtil.STATUS_NORMAL);
				//说明有邀请记录，找到是否有管理员邀请记录
				result = clockDealMapper.save(clockDealBean) > 0;
				if(result){
					//调用同意邀请的接口
					return inviteAgree(clockId, user.getId(), json, user, request);
				}
			}else{
				
//				if(!clockBean.isAutoAdd() && user.getId() != clockBean.getCreateUserId()){
//					throw new IllegalOperationException("您非任务创建者并且任务非自动添加成员，无法邀请！");
//				}
				clockDealBean.setStatus(ConstantsUtil.STATUS_WAIT_MANAGE_AGREE);
				clockDealBean.setNewStatus(ConstantsUtil.STATUS_WAIT_MANAGE_AGREE);
				//说明有邀请记录，找到是否有管理员邀请记录
				result = clockDealMapper.save(clockDealBean) > 0;
				if(result){
					
					//保存动态信息
					clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), clockDynamicHandler.getUserName(user, user.getId()) + "请求加入任务", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
					
					//通知对方有人邀请
					Map<String, Object> mp = new HashMap<String, Object>();
					mp.put("content", userHandler.getUserName(user.getId()) +"请求加入您的任务《"+ clockBean.getTitle() +"》");
					mp.put("clock_id", clockId);
					CustomMessage customMessage = new JpushCustomMessage();
					customMessage.sendToAlias("leedane_user_"+ clockBean.getCreateUserId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.请求加入任务.value);
					message.put("isSuccess", true);
					message.put("message", "您的请求已经发送，请耐心等待管理者审核！");
					message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
					return message.getMap();
				}
			}
			return message.getMap();
		}
	}


	@Override
	public Map<String, Object> requestAgree(int clockId, int memberId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockDealServiceImpl-->requestAgree():clockId = "+ clockId +",userId=" +user.getId() +", json=" +json.toString());
		//校验
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		checkClockCanDo(clockBean, memberId);
		
		if(!clockBean.isAutoAdd() && user.getId() != clockBean.getCreateUserId()){
			throw new IllegalOperationException("您非任务创建者并且任务非自动添加成员，无法同意请求！");
		}
		ResponseMap message = new ResponseMap();
		//获取目前所有请求加入记录
		ClockDealBean requestMe = clockDealMapper.requestMe(user.getId(), memberId, clockId);
		if(requestMe != null){
			//获取所有跟我和这个任务相关的记录
//			List<ClockDealBean> memberClocks = clockDealMapper.getMemberClockDeals(user.getId(), clockId);
			//直接通过
			clockDealMapper.updateStatus(clockId, memberId, ConstantsUtil.STATUS_NORMAL);
			clockMemberService.add(clockId, memberId, json, user, request);
			
			//删除用户对应的任务缓存
			clockHandler.deleteDateClocksCache(memberId);
			
			//通知用户成功加入任务
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("content", "您已成功加入任务《"+ clockBean.getTitle() +"》");
			mp.put("clock_id", clockId);
			CustomMessage customMessage = new JpushCustomMessage();
			customMessage.sendToAlias("leedane_user_"+ memberId,  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.同意加入任务.value);
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作成功.value);
			return message.getMap();
		}else{
			throw new IllegalOperationException("没有找到请求记录，无法同意请求，请稍后重试！");
		}
	}

	@Override
	public Map<String, Object> inviteAdd(int clockId, int memberId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		//邀请加入(对方不在该任务中)
		ResponseMap message = new ResponseMap();
		//校验
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		checkClockCanDo(clockBean, memberId);
		
		//判断是否是任务创建者
		if(clockBean.getCreateUserId() != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
				
		
		if(memberId == user.getId())
			throw new IllegalOperationException("无法执行邀请操作：原因是自己邀请自己！");
			
		ClockDealBean myClockDealBean = null;
		//判断是否已经加入
		List<ClockDealBean>  clockDealBeans= clockDealMapper.userClockRecord(memberId, clockId);
		if(CollectionUtil.isNotEmpty(clockDealBeans)){
			for(ClockDealBean clockDealBean: clockDealBeans){
				if(clockDealBean.getNewStatus() == ConstantsUtil.STATUS_NORMAL)
					throw new ParameterUnspecificationException("您已经在任务成员列表中，请勿重复操作！"); 
				
				//获取对方对我的邀请记录
				if(clockDealBean.getMemberId() == memberId && clockDealBean.getNewStatus() == ConstantsUtil.STATUS_WAIT_MEMBER_AGREE && myClockDealBean == null){
					//判断是否有对方邀请我的创建记录
					if(clockDealBean.getCreateUserId() == memberId && clockDealBean.getNewStatus() == ConstantsUtil.STATUS_WAIT_MEMBER_AGREE){
						return inviteAgree(clockId, memberId, json, user, request);
					}
					
					myClockDealBean = clockDealBean;
				}
			}
		}
		
		//获取对方对我的邀请记录
//		ClockDealBean inviteMe = clockDealMapper.userRequestClockDeal(user.getId(), user.getId(), clockId);
		//没有邀请记录
		if(myClockDealBean != null){
			//判断修改时间, 10分钟才能请求一次
			if(DateUtil.isInMinutes(myClockDealBean.getModifyTime(), new Date(), CLOCK_AGAIN_REQUEST_ADD_TIME))
				throw new ParameterUnspecificationException("距离上次请求没超过"+ CLOCK_AGAIN_REQUEST_ADD_TIME + "分钟");
			
			//更新时间
			myClockDealBean.setModifyTime(new Date());
			boolean update = clockDealMapper.update(myClockDealBean) > 0;
			if(update){
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("content", "再次邀请"+ userHandler.getUserName(memberId) +"加入任务《"+ clockBean.getTitle() +"》");
				mp.put("clock_id", clockId);
				CustomMessage customMessage = new JpushCustomMessage();
				customMessage.sendToAlias("leedane_user_"+ memberId,  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.邀请加入任务.value);
				message.put("isSuccess", true);
				message.put("message", "您的邀请已经发送，请耐心等待《"+ userHandler.getUserName(memberId) +"》同意！");
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
				return message.getMap();
			}else{
				message.put("isSuccess", false);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
				return message.getMap();
			}
			
			//如果我是创建者，将直接更新为同意
		}else{
			if(!clockBean.isAutoAdd() && user.getId() != clockBean.getCreateUserId()){
				throw new IllegalOperationException("您非任务创建者并且任务非自动添加成员，无法邀请！");
			}
			
			ClockDealBean clockDealBean = new ClockDealBean();
			clockDealBean.setClockId(clockId);
			clockDealBean.setMemberId(memberId);
			clockDealBean.setCreateTime(new Date());
			clockDealBean.setCreateUserId(user.getId());
			clockDealBean.setModifyTime(new Date());
			clockDealBean.setModifyUserId(user.getId());
			boolean result = clockDealMapper.save(clockDealBean) > 0;
			if(result){
				
				//保存动态信息
				clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), "邀请"+ userHandler.getUserName(memberId) +"加入任务", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
				
				//通知对方有人邀请
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("content", userHandler.getUserName(user.getId()) +"邀请您加入任务《"+ clockBean.getTitle() +"》");
				mp.put("clock_id", clockId);
				CustomMessage customMessage = new JpushCustomMessage();
				customMessage.sendToAlias("leedane_user_"+ memberId,  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.邀请加入任务.value);
				message.put("isSuccess", true);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
				message.put("responseCode", EnumUtil.ResponseCode.操作成功.value);
				return message.getMap();
			}
		}
		return message.getMap();
	}


	@Override
	public Map<String, Object> inviteAgree(int clockId, int memberId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		
		//找出有没有创建者的邀请记录
		
		//如果有：直接加入任务并且更新所有的邀请记录为加入任务状态
			//是否
		checkClockCanDo(clockBean, memberId);
		
		if(!clockBean.isAutoAdd() && user.getId() != clockBean.getCreateUserId()){
			throw new IllegalOperationException("您非任务创建者并且任务非自动添加成员，无法同意邀请！");
		}
		
		//获取目前邀请记录
		ClockDealBean inviteMe = clockDealMapper.inviteMe(user.getId(), memberId, clockId);
		if(inviteMe == null)
			throw new IllegalOperationException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该任务您还没有邀请记录.value));
		ResponseMap message = new ResponseMap();
		boolean result = clockMemberService.add(clockId, memberId, json, user, request);
				
		//更新所有的任务的状态为加入状态
		if(result){
			List<ClockDealBean>  clockDealBeans = clockDealMapper.userClockRecord(memberId, clockId);
			//更新所有的任务的状态为加入状态
			for(ClockDealBean clockDeal: clockDealBeans){
				clockDealMapper.updateStatus(clockId, clockDeal.getMemberId(), ConstantsUtil.STATUS_NORMAL);
				
				//删除用户对应的任务缓存
				clockHandler.deleteDateClocksCache(clockDeal.getMemberId());
				
				//通知用户成功加入任务
				if(clockDeal.getMemberId() != user.getId()){
					Map<String, Object> mp = new HashMap<String, Object>();
					mp.put("content", "您已成功加入任务《"+ clockBean.getTitle() +"》");
					mp.put("clock_id", clockId);
					CustomMessage customMessage = new JpushCustomMessage();
					customMessage.sendToAlias("leedane_user_"+ clockDeal.getMemberId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.同意加入任务.value);
				}
			}
			
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作成功.value);
		}else{
			//暂时无法同意邀请，可以联系任务管理者处理
			message.put("isSuccess", false);
			message.put("message", "您没有权限同意邀请，可以联系该任务管理者处理！");
		}
		return message.getMap();
	}
	
	/**
	 * 
	 * @param clockBean
	 * @param memberId
	 */
	private void checkClockCanDo(ClockBean clockBean, int memberId){
		//自己不能加入自己的任务
		if(memberId == clockBean.getCreateUserId())
			throw new IllegalOperationException("已经在任务成员列表中，不支持此操作！");
		
		//校验任务是否已经结束
		if(clockBean.getEndDate() != null && clockBean.getEndDate().getTime() < DateUtil.stringToDate(DateUtil.DateToString(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").getTime()){
			throw new IllegalOperationException("该任务已经结束！");
		}
		
		//如果没有开启共享，提示用户无法操作
		if(!clockBean.isShare())
			throw new IllegalOperationException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该任务不支持共享.value));
	
		//判断是否过期
		if(clockBean.getApplyEndDate() != null && clockBean.getApplyEndDate().getTime() < DateUtil.stringToDate(DateUtil.DateToString(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").getTime()){
			throw new IllegalOperationException("已超出该任务报名日期！");
		}
		List<ClockMemberBean> members = clockMemberHandler.members(clockBean.getId());
		//判断是否超员
		if(members.size() >= clockBean.getTakePartNumber()){
			throw new IllegalOperationException("已超出该任务最大参与人数！");
		}
	}
}
