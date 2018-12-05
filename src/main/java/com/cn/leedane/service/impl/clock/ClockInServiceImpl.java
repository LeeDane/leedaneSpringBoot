package com.cn.leedane.service.impl.clock;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockDynamicHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.handler.clock.ClockMemberHandler;
import com.cn.leedane.mapper.clock.ClockInMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.mapper.clock.ClockMemberMapper;
import com.cn.leedane.message.JPushMessageNotificationImpl;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.message.notification.MessageNotification;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockDynamicBean;
import com.cn.leedane.model.clock.ClockDynamicQueueBean;
import com.cn.leedane.model.clock.ClockInBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.model.clock.ClockScoreBean;
import com.cn.leedane.model.clock.ClockScoreQueueBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockInService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.ClockDynamicThread;
import com.cn.leedane.thread.single.ClockScoreThread;
import com.cn.leedane.thread.single.VisitorDeleteThread;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ClockInType;
import com.cn.leedane.utils.EnumUtil.ClockScoreBusinessType;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

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
	
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpServletRequest request) {
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
		clockInBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		clockInBean.setModifyTime(systemDate);
		clockInBean.setModifyUserId(userId);
		clockInBean.setClockDate(systemDate);
		boolean result = clockInMapper.save(clockInBean) > 0;
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
			clockDynamicHandler.saveDynamic(clockId, systemDate, userId, userHandler.getUserName(userId) + "打卡成功", true);
			//清空该用户的提醒任务列表缓存
			clockHandler.deleteDateClocksCache(userId);
			
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
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为任务ID：", clockId,"打卡，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);	
		
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(int clockInId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
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
			HttpServletRequest request) {
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
}
