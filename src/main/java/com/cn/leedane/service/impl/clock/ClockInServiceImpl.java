package com.cn.leedane.service.impl.clock;

import com.cn.leedane.display.clock.ClockInDisplay;
import com.cn.leedane.display.clock.ClockMemberDisplay;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockDynamicHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.handler.clock.ClockMemberHandler;
import com.cn.leedane.mapper.clock.ClockInMapper;
import com.cn.leedane.mapper.clock.ClockInResourcesMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.*;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockInService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.ClockScoreThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.ClockInType;
import com.cn.leedane.utils.EnumUtil.ClockScoreBusinessType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 任务打卡service实现类
 * @author LeeDane
 * 2018年8月29日 下午5:34:53
 * version 1.0
 */
@Service("clockInService")
public class ClockInServiceImpl extends AdminRoleCheckService implements ClockInService<ClockInBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ClockInMapper clockInMapper;

	@Autowired
	private ClockInResourcesMapper clockInResourceMapper;
	
	@Autowired
	private ClockMapper clockMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private ClockHandler clockHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private ClockMemberHandler clockMemberHandler;
	
	@Autowired
	private ClockDynamicHandler clockDynamicHandler;
	
	@Value("${constant.defalult.clock.in.score}")
    public int CLOCK_IN_SCORE;

	@Autowired
	private NotificationHandler notificationHandler;
	
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		SqlUtil sqlUtil = new SqlUtil();
		ClockInBean clockInBean = (ClockInBean) sqlUtil.getBean(jo, ClockInBean.class);
		
		int clockId = clockInBean.getClockId();
		ClockBean clock = clockHandler.getNormalClock(clockId);
		
		//检验是否在成员列表中
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法打卡。");
		
		//判断是否需要图片打卡
		if(clock.getClockInType() == ClockInType.图片打卡.value && StringUtil.isNull(clockInBean.getImg())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先上传图片才能继续打卡.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先上传图片才能继续打卡.value);
			return message.getMap();
		}
		
		//是否需要位置打卡
		if(clock.getClockInType() == ClockInType.位置打卡.value && StringUtil.isNull(clockInBean.getLocation())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先定位才能继续打卡.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先定位才能继续打卡.value);
			return message.getMap();
		}
		
		//是否需要计数打卡
		if(clock.getClockInType() == ClockInType.计步打卡.value){
			if(clockInBean.getStep() < 1){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先计步才能继续打卡.value));
				message.put("responseCode", EnumUtil.ResponseCode.请先计步才能继续打卡.value);
				return message.getMap();
			}
			
			if(clockInBean.getStep() < clock.getMustStep()){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.计步数不足以打卡.value) +",最低要求"+ clock.getMustStep() +"步");
				message.put("responseCode", EnumUtil.ResponseCode.计步数不足以打卡.value);
				return message.getMap();
			}
		}
		
		int userId = user.getId();
		
		//判断当天是否可以打卡
		
		Date systemDate = new Date();
		
		//判断打卡的时间段是否正确
		Date clockStartTime = clock.getClockStartTime();
		Date clockEndTime = clock.getClockEndTime();
		Date curr = DateUtil.stringToDate(DateUtil.DateToString(systemDate, "HH:mm:ss"), "HH:mm:ss");
		
		//开始打卡时间和结束打卡时间要是有一个为空，将不限制打卡时间
		if(clockStartTime != null && clockEndTime != null)
			if(curr.getTime() < clockStartTime.getTime() 
					|| (clockEndTime.getTime() > clockStartTime.getTime() && curr.getTime() > clockEndTime.getTime())
					|| (clockEndTime.getTime() < clockStartTime.getTime() && curr.getTime() < clockEndTime.getTime())){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.超出打卡范围.value));
				message.put("responseCode", EnumUtil.ResponseCode.超出打卡范围.value);
				return message.getMap();
			}
		
		clockInBean.setCreateTime(systemDate);
		clockInBean.setCreateUserId(userId);

		//判断是否需要审核
		boolean mustAudit = userId != clock.getCreateUserId() && clock.isMustCheckClockIn();
		if(mustAudit){
			clockInBean.setStatus(ConstantsUtil.STATUS_AUDIT); //等待审核
		}else{
			clockInBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		}

		clockInBean.setModifyTime(systemDate);
		clockInBean.setModifyUserId(userId);
		clockInBean.setClockDate(systemDate);
		boolean result = clockInMapper.save(clockInBean) > 0;

		if(clock.getClockInType() == ClockInType.图片打卡.value){
			ClockInResourcesBean inResourcesBean = new ClockInResourcesBean();
			inResourcesBean.setResourceType(ClockInType.图片打卡.value);
			inResourcesBean.setResource(clockInBean.getImg());
			inResourcesBean.setClockInId(clockInBean.getId());
			inResourcesBean.setMain(true);
			inResourcesBean.setModifyTime(new Date());
			inResourcesBean.setModifyUserId(userId);
			inResourcesBean.setCreateTime(new Date());
			inResourcesBean.setCreateUserId(userId);
			inResourcesBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			result = clockInResourceMapper.save(inResourcesBean) > 0 ;
		}

		if(clock.getClockInType() == ClockInType.位置打卡.value){
			ClockInResourcesBean inResourcesBean = new ClockInResourcesBean();
			inResourcesBean.setResourceType(ClockInType.位置打卡.value);
			inResourcesBean.setResource(clockInBean.getLocation());
			inResourcesBean.setClockInId(clockInBean.getId());
			inResourcesBean.setMain(true);
			inResourcesBean.setModifyTime(new Date());
			inResourcesBean.setModifyUserId(userId);
			inResourcesBean.setCreateTime(new Date());
			inResourcesBean.setCreateUserId(userId);
			inResourcesBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			result = clockInResourceMapper.save(inResourcesBean) > 0 ;
		}

		if(clock.getClockInType() == ClockInType.计步打卡.value){
			ClockInResourcesBean inResourcesBean = new ClockInResourcesBean();
			inResourcesBean.setResourceType(ClockInType.计步打卡.value);
			inResourcesBean.setResource(clockInBean.getStep()+"");
			inResourcesBean.setClockInId(clockInBean.getId());
			inResourcesBean.setMain(true);
			inResourcesBean.setModifyTime(new Date());
			inResourcesBean.setModifyUserId(userId);
			inResourcesBean.setCreateTime(new Date());
			inResourcesBean.setCreateUserId(userId);
			inResourcesBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			result = clockInResourceMapper.save(inResourcesBean) > 0 ;
		}

		if(result){

			ClockScoreQueueBean clockScoreQueueBean = new ClockScoreQueueBean();
			ClockScoreBean clockScoreBean = new ClockScoreBean();
			clockScoreBean.setClockId(clockId);
			clockScoreBean.setCreateTime(systemDate);
			clockScoreBean.setCreateUserId(userId);
			clockScoreBean.setModifyTime(systemDate);
			clockScoreBean.setModifyUserId(userId);
			clockScoreBean.setScore(CLOCK_IN_SCORE);
			clockScoreBean.setScoreDate(systemDate);
			clockScoreBean.setScoreDesc(DateUtil.DateToString(systemDate, "yyyy年MM月dd日")+"对任务《" + clock.getTitle() +"》进行打卡获得"+ CLOCK_IN_SCORE +"积分");
			clockScoreBean.setBusinessType(ClockScoreBusinessType.每日打卡.value);
			clockScoreQueueBean.setClockScoreBean(clockScoreBean);
			clockScoreQueueBean.setOperateType(EnumUtil.ClockScoreOperateType.新增.value);
			new ThreadUtil().singleTask(new ClockScoreThread(user, clockScoreQueueBean));

			//保存动态信息
			clockDynamicHandler.saveDynamic(clockId, systemDate, userId, "打卡成功", true, EnumUtil.CustomMessageExtraType.任务打卡.value);
			//清空该用户的提醒任务列表缓存
			clockHandler.deleteDateClocksCache(userId);
			if(mustAudit){
				//
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("user_id", userId);
				mp.put("user_name", user.getAccount());
				mp.put("clock_title", userHandler.getUserName(userId)+"今日打卡，需要您的审核！");
				mp.put("clock_id", clockId);
				CustomMessage customMessage = new JpushCustomMessage();
				customMessage.sendToAlias("leedane_user_"+ clock.getCreateUserId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.打卡等待审核.value);

				message.put("isSuccess", true);
				message.put("message", "今日打卡成功,请耐心等待管理员审核");
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
				return message.getMap();
			}
			
			//通知创建者和所有的成员
			if(clock.isShare()){
				List<ClockMemberBean> members = clockMemberHandler.members(clockId);
				if(CollectionUtil.isNotEmpty(members)){
					String userName = userHandler.getUserName(userId);
					for(ClockMemberBean member: members){
						if(member.getMemberId() != userId && member.isNotification()){
							Map<String, Object> mp = new HashMap<String, Object>();
							mp.put("user_id", userId);
							mp.put("user_name", userName);
							mp.put("clock_title", clock.getTitle());
							mp.put("clock_id", clockId);
							CustomMessage customMessage = new JpushCustomMessage();
							customMessage.sendToAlias("leedane_user_"+ member.getMemberId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.任务打卡.value);
						}
					}
				}
			}
			
			message.put("isSuccess", true);
			message.put("message", "今日打卡成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为任务ID：", clockId,"打卡，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(int clockInId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
//		BabyBean baby = babyHandler.getNormalBaby(babyId, user);
//		
//		//自动填充更新的bean
//		SqlUtil sqlUtil = new SqlUtil();
//		baby = (BabyBean)sqlUtil.getUpdateBean(jo, baby);
//		baby.setModifyTime(new Date());
//		baby.setModifyUserId(user.getId());
//		
//		boolean result = babyMapper.update(baby) > 0;
//		if(result){
//			//清除该宝宝的缓存
//			babyHandler.deleteBabyBeanCache(baby.getId());
//			//清空该用户的宝宝列表缓存
//			babyHandler.deleteBabyBeansCache(user.getId());
//			message.put("isSuccess", true);
//			String content= "您已成功修改宝宝《"+ baby.getNickname() +"》的信息！";
//			message.put("message", content);
//			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
//			//通知用户
//			notificationHandler.sendNotificationById(true, user, user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
//		}else{
//			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
//			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
//		}
//		
//		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改名称为：", baby.getNickname(),"的宝宝的基本信息：", jo.toString(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);	
//		
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int clockInId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->delete():clockInId=" +clockInId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
//		BabyBean baby = babyHandler.getNormalBaby(babyId, user);
//		//这里是逻辑删除
//		//baby.setStatus(ConstantsUtil.STATUS_DELETE);
//		boolean result = babyMapper.deleteById(BabyBean.class, babyId) > 0;
//		if(result){
//			//清除该宝宝的缓存
//			babyHandler.deleteBabyBeanCache(babyId);
//			//清空该用户的宝宝列表缓存
//			babyHandler.deleteBabyBeansCache(user.getId());
//			message.put("isSuccess", true);
//			String content = "您已成功删除宝宝《"+ baby.getNickname() +"》！";
//			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
//			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
//			
//			//通知用户
//			notificationHandler.sendNotificationById(true, user, user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
//		}else{
//			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
//			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
//		}
		
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除宝宝名称ID为：", babyId ,"的宝宝的基本信息：", "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}

	@Override
	public Map<String, Object> clockIns(int clockId, String date, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->clockIns():clockId=" +clockId + ", date="+ date +", user=" +user.getAccount());
		clockHandler.getNormalClock(clockId);
		ResponseMap message = new ResponseMap();
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法获取成员列表。");
		List<Map<String, Object>> members = clockInMapper.membersSortByIns(clockId, date,0);
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
		clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), "查看"+date+"的打卡情况", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> getUserClockIn(int clockId, int toUserId, String date, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->getUserClockIn():clockId=" +clockId + ", date="+ date +", userId = "+ toUserId +", user=" +user.getAccount());
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		ResponseMap message = new ResponseMap();
		if(!clockMemberHandler.inMember(toUserId, clockId))
			throw new UnauthorizedException(userHandler.getUserName(toUserId)+ "未加入该任务，无法获取任务打卡信息。");

		if(!clockBean.isShare() && clockBean.getCreateUserId() != user.getId())
			throw new UnauthorizedException("该打卡任务是非共享任务");

		ClockInDisplay clockInDisplay = clockInMapper.getUserClockIn(clockId, toUserId, date);
		if(clockInDisplay == null || clockInDisplay.getStatus() == ConstantsUtil.STATUS_DELETE)
			throw new RE404Exception("打卡记录不存在");

		int clockInStatus = clockInDisplay.getStatus();
		if(clockInStatus == ConstantsUtil.STATUS_AUDIT){
			//非创作者和非管理员
			if(user.getId() != clockInDisplay.getCreateUserId() && user.getId() != clockBean.getCreateUserId()){
				throw new UnauthorizedException("审核状态，无法操作！");
			}
		}else if(clockInStatus == ConstantsUtil.STATUS_NORMAL){
			//非创作者和非管理员并且非互看模式下
			if(!clockBean.isSeeEachOther() && user.getId() != clockInDisplay.getCreateUserId() && clockBean.getCreateUserId() != user.getId())
				throw new UnauthorizedException("已被设置为不能查看对方的打卡记录");
		}else if(clockInStatus == ConstantsUtil.STATUS_NO_AGREE){
//			throw new UnauthorizedException("任务经过管理员审核为不通过");
		}else{
			throw new UnauthorizedException("未知状态，无法处理");
		}

		if(clockInDisplay.getStatus() != ConstantsUtil.STATUS_NORMAL && clockInDisplay.getStatus() != ConstantsUtil.STATUS_AUDIT && clockInDisplay.getStatus() != ConstantsUtil.STATUS_NO_AGREE)
			throw new UnauthorizedException("该打卡记录已经无权限被查看");

		clockInDisplay.setIcon(clockBean.getIcon());
		clockInDisplay.setTitle(clockBean.getTitle());
		clockInDisplay.setAccount(userHandler.getUserName(toUserId));

		message.put("message", clockInDisplay);
		message.put("isSuccess", true);
		//保存动态信息
		clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), "查看用户"+ userHandler.getUserName(toUserId)+"在"+date+"的打卡记录情况", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> userClockInNotification(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->userClockInNotification():clockId=" +clockId +", clockInId = "+ clockInId +", user=" +user.getAccount());
		ClockBean clockBean = clockHandler.getNormalClock(clockId);

		ResponseMap message = new ResponseMap();
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法执行此操作");

		ClockInBean clockInBean = clockInMapper.findById(ClockInBean.class, clockInId);
		if(clockInBean == null || clockInBean.getStatus() == ConstantsUtil.STATUS_DELETE)
			throw new RE404Exception("打卡记录不存在");

		int clockInStatus = clockInBean.getStatus();
		if(clockInStatus != ConstantsUtil.STATUS_AUDIT)
			throw new UnauthorizedException("非审核状态");

		//发送通知
		String content = userHandler.getUserName(user.getId())+ "已经在任务<"+ clockBean.getTitle() +">打卡，请尽快审核";
		notificationHandler.sendNotificationById(false, user, clockBean.getCreateUserId(), content, EnumUtil.NotificationType.通知, null, 0, null);

		clockInBean.setModifyTime(new Date());
		clockInBean.setModifyUserId(user.getId());
		message.put("message", "提醒成功");
		message.put("isSuccess", true);
		//保存动态信息
		clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), userHandler.getUserName(user.getId())+"提醒管理员审核打卡", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> clockInCheck(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->clockInCheck():clockId=" +clockId + ", clockInId = "+ clockInId +", user=" +user.getAccount());
		ClockBean clockBean = clockHandler.getNormalClock(clockId);

		boolean agree = JsonUtil.getBooleanValue(json, "agree", false);
		ResponseMap message = new ResponseMap();
		if(user.getId() != clockBean.getCreateUserId())
			throw new UnauthorizedException("非任务管理员");

		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法执行此操作");

		ClockInBean clockInBean = clockInMapper.findById(ClockInBean.class, clockInId);
		if(clockInBean == null || clockInBean.getStatus() == ConstantsUtil.STATUS_DELETE)
			throw new RE404Exception("打卡记录不存在");

		int clockInStatus = clockInBean.getStatus();
		if(clockInStatus != ConstantsUtil.STATUS_AUDIT)
			throw new UnauthorizedException("非审核状态");

		if(agree){
			clockInBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		}else{
			clockInBean.setStatus(ConstantsUtil.STATUS_NO_AGREE);
		}
		clockInBean.setModifyTime(new Date());
		clockInBean.setModifyUserId(user.getId());
		boolean result = clockInMapper.update(clockInBean) > 0;
		if(result){
			message.put("message", "操作成功！");
			message.put("isSuccess", true);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);

			//发送通知
			String content = "您在任务<"+ clockBean.getTitle() +">打卡记录审核"+ (agree ? "通过": "不通过") +"请知悉";
			notificationHandler.sendNotificationById(false, user, clockInBean.getCreateUserId(), content, EnumUtil.NotificationType.通知, null, 0, null);

			//清空该用户的提醒任务列表缓存
			clockHandler.deleteDateClocksCache(clockInBean.getCreateUserId());
			//保存动态信息
			clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), user.getAccount() + "审核打卡："+ userHandler.getUserName(clockInBean.getCreateUserId())+"在"+ DateUtil.DateToString(clockInBean.getClockDate())+"的打卡情况", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}

		return message.getMap();
	}

	@Override
	public Map<String, Object> clockInAddLocation(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->clockInAddLocation():clockId=" +clockId + ", clockInId = "+ clockInId +", user=" +user.getAccount());
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		String location = JsonUtil.getStringValue(json, "location", null);

		if(StringUtil.isNull(location)){
			throw new ParameterUnspecificationException("位置信息为空");
		}
		ResponseMap message = new ResponseMap();
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法执行此操作");

		ClockInBean clockInBean = clockInMapper.findById(ClockInBean.class, clockInId);
		if(clockInBean == null || clockInBean.getStatus() == ConstantsUtil.STATUS_DELETE)
			throw new RE404Exception("打卡记录不存在");

		int clockInStatus = clockInBean.getStatus();
		//只有正常状态和待审核状态才能操作
		if(clockInStatus != ConstantsUtil.STATUS_NORMAL && clockInStatus != ConstantsUtil.STATUS_AUDIT)
			throw new UnauthorizedException("非法状态，无法操作");

		if(user.getId() != clockBean.getCreateUserId() && user.getId() != clockInBean.getCreateUserId())
			throw new UnauthorizedException("非创建者，无权限执行此操作！");

		ClockInResourcesBean resourcesBean = new ClockInResourcesBean();
		resourcesBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		resourcesBean.setResource(location);
		resourcesBean.setClockInId(clockInId);
		resourcesBean.setMain(false);
		resourcesBean.setResourceType(ClockInType.位置打卡.value);
		resourcesBean.setCreateUserId(user.getId());
		resourcesBean.setCreateTime(new Date());
		resourcesBean.setModifyTime(new Date());
		resourcesBean.setModifyUserId(user.getId());
		boolean result = clockInResourceMapper.save(resourcesBean) > 0;
		if(result){
			message.put("message", "操作成功！");
			message.put("isSuccess", true);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//保存动态信息
			clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), user.getAccount() + "补充位置信息", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> clockInAddImage(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->clockInAddImage():clockId=" +clockId + ", clockInId = "+ clockInId +", user=" +user.getAccount());
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		String image = JsonUtil.getStringValue(json, "image", null);

		if(StringUtil.isNull(image)){
			throw new ParameterUnspecificationException("图片信息为空");
		}
		ResponseMap message = new ResponseMap();
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法执行此操作");

		ClockInBean clockInBean = clockInMapper.findById(ClockInBean.class, clockInId);
		if(clockInBean == null || clockInBean.getStatus() == ConstantsUtil.STATUS_DELETE)
			throw new RE404Exception("打卡记录不存在");

		int clockInStatus = clockInBean.getStatus();
		//只有正常状态和待审核状态才能操作
		if(clockInStatus != ConstantsUtil.STATUS_NORMAL && clockInStatus != ConstantsUtil.STATUS_AUDIT)
			throw new UnauthorizedException("非法状态，无法操作");

		if(user.getId() != clockBean.getCreateUserId() && user.getId() != clockInBean.getCreateUserId())
			throw new UnauthorizedException("非创建者，无权限执行此操作！");

		ClockInResourcesBean resourcesBean = new ClockInResourcesBean();
		resourcesBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		resourcesBean.setResource(image);
		resourcesBean.setClockInId(clockInId);
		resourcesBean.setMain(false);
		resourcesBean.setResourceType(ClockInType.图片打卡.value);
		resourcesBean.setCreateUserId(user.getId());
		resourcesBean.setCreateTime(new Date());
		resourcesBean.setModifyTime(new Date());
		resourcesBean.setModifyUserId(user.getId());
		boolean result = clockInResourceMapper.save(resourcesBean) > 0;
		if(result){
			message.put("message", "操作成功！");
			message.put("isSuccess", true);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//保存动态信息
			clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), user.getAccount() + "补充位置信息", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> deleteResource(int clockId, int clockInId, int resourceId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockInServiceImpl-->deleteResource():clockId=" +clockId + ", clockInId = "+ clockInId +", resourceId="+ resourceId +", user=" +user.getAccount());
		ClockBean clockBean = clockHandler.getNormalClock(clockId);

		ClockInBean clockInBean = clockInMapper.findById(ClockInBean.class, clockInId);
		if(clockInBean == null || clockInBean.getStatus() == ConstantsUtil.STATUS_DELETE){
			throw new RE404Exception("打卡记录不存在");
		}

		//判断是否是自己还是管理员的才做
		ResponseMap message = new ResponseMap();

		if(user.getId() != clockBean.getCreateUserId() && user.getId() != clockInBean.getCreateUserId())
			throw new UnauthorizedException("非创建者，无权限执行此操作！");

		ClockInResourcesBean clockInResourcesBean = clockInResourceMapper.findById(ClockInResourcesBean.class, resourceId);
		if(clockInResourcesBean == null || clockInResourcesBean.getStatus() == ConstantsUtil.STATUS_DELETE){
			throw new RE404Exception("该资源记录已经被删除");
		}

		//主图不允许删除
		if(clockInResourcesBean.isMain()){
			throw new RE404Exception("该资源无法被删除");
		}

		clockInResourcesBean.setStatus(ConstantsUtil.STATUS_DELETE);
		clockInResourcesBean.setModifyTime(new Date());
		clockInResourcesBean.setModifyUserId(user.getId());
		boolean result = clockInResourceMapper.update(clockInResourcesBean) > 0;
		if(result){
			message.put("message", "删除成功！");
			message.put("isSuccess", true);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//保存动态信息
			clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), user.getAccount() + "删除资源", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		return message.getMap();
	}
}
