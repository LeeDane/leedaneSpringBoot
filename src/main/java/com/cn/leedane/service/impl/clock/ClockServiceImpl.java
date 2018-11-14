package com.cn.leedane.service.impl.clock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.leedane.display.clock.ClockDisplay;
import com.cn.leedane.display.clock.ClockDisplayGroup;
import com.cn.leedane.display.clock.ClockSearchDisplay;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.mapper.CategoryMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.mapper.clock.ClockMemberMapper;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.service.clock.ClockService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 任务提醒service实现类
 * @author LeeDane
 * 2018年8月29日 下午5:34:53
 * version 1.0
 */
@Service("clockService")
public class ClockServiceImpl extends AdminRoleCheckService implements ClockService<ClockBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ClockMapper clockMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private ClockHandler clockHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private ClockMemberService<ClockMemberBean> clockMemberService;
	
	@Autowired
	private ClockMemberMapper clockMemberMapper;
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		SqlUtil sqlUtil = new SqlUtil();
		ClockBean clockBean = (ClockBean) sqlUtil.getBean(jo, ClockBean.class);
		
		//参数校验
		if(StringUtil.isNull(clockBean.getTitle()))
			throw new NullPointerException("任务标题不能为空！");
		
		if(StringUtil.isNull(clockBean.getDescribe_()))
			throw new NullPointerException("任务描述不能为空！");
		
		if(clockBean.getStartDate() == null)
			throw new NullPointerException("开始日期不能为空！");
		
		if(clockBean.getEndDate() != null && clockBean.getStartDate().getTime() >  clockBean.getEndDate().getTime())
			throw new ParameterUnspecificationException("开始日期不能大于结束日期！");
		
//		if(clockBean.getClockStartTime() == null)
//			throw new NullPointerException("打卡开始时间不能为空！");
		
		if(StringUtil.isNull(clockBean.getRepeat_()))
			throw new NullPointerException("重复周期不能为空！");
				
		ResponseMap message = new ResponseMap();
		clockBean.setCreateTime(new Date());
		clockBean.setCreateUserId(user.getId());
		clockBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		clockBean.setModifyTime(new Date());
		clockBean.setModifyUserId(user.getId());
		//参与人数必须大于等于1，因为自己本身就是参与人之一
		if(clockBean.getTakePartNumber() < 1)
			clockBean.setTakePartNumber(1);
		
		//设置唯一共享ID
		if(clockBean.isShare()){
			clockBean.setShareId(StringUtil.getShareId());
		}
		
		boolean result = clockMapper.save(clockBean) > 0;
		if(result){
			//添加成员
			result = clockMemberService.add(clockBean.getId(), user.getId(), jo, user, request);
			
			//清空该用户的任务列表缓存
			if(clockBean.getParentId() > 0 || clockBean.getCategoryId() < 1)
				clockHandler.deleteDateClocksCache(user.getId());
			message.put("isSuccess", true);
			String content = "您已成功创建新的任务，请准时打卡！";
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"创建任务名称为：", clockBean.getTitle(),"，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);		
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(int clockId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		int userId = user.getId();
		
		boolean creater = userId == clockBean.getCreateUserId();
		//判断如果是创建人或者是普通成员
		if(creater){
			//自动填充更新的bean
			SqlUtil sqlUtil = new SqlUtil();
			clockBean = (ClockBean)sqlUtil.getUpdateBean(jo, clockBean);
			clockBean.setModifyTime(new Date());
			clockBean.setModifyUserId(userId);
			//设置唯一共享ID
			if(clockBean.isShare() && StringUtil.isNull(clockBean.getShareId())){
				clockBean.setShareId(StringUtil.getShareId());
			}
			boolean result = clockMapper.update(clockBean) > 0;
			if(result){
				//清除该用户的任务
				clockHandler.deleteDateClocksCache(userId);
				String content= "修改任务《"+ clockBean.getTitle() +"》成功！";
				message.put("isSuccess", true);
				message.put("message", content);
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
				String userName = userHandler.getUserName(userId);
				//通知创建者和所有的成员
				if(clockBean.isShare()){
					List<ClockMemberBean> members = clockMemberMapper.members(clockId);
					if(CollectionUtil.isNotEmpty(members)){
						for(ClockMemberBean member: members){
							if(member.getMemberId() != userId){
								clockHandler.deleteDateClocksCache(member.getMemberId());
								if(member.isNotification()){
									Map<String, Object> mp = new HashMap<String, Object>();
									mp.put("user_id", userId);
									mp.put("user_name", userName);
									mp.put("clock_title", clockBean.getTitle());
									mp.put("clock_id", clockId);
									CustomMessage customMessage = new JpushCustomMessage();
									customMessage.sendToAlias("leedane_user_"+ member.getMemberId(),  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.任务修改.value);
								}
							}
						}
					}
				}
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
			}
		}else{
			//保存操作日志
			operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改名称为：", clockBean.getTitle(),"的任务的基本信息：", jo.toString(), "，结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);	
			return clockMemberService.update(clockId, user.getId(), jo, user, request);
		}
		
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int clockId, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->delete():clockId=" +clockId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		//校验
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		
		int userId = user.getId();
		boolean creater = clockBean.getCreateUserId() == userId;
		boolean result = true;
		if(creater){
			//这里是逻辑删除
			clockBean.setStatus(ConstantsUtil.STATUS_DELETE);
			clockBean.setModifyTime(new Date());
			result = clockMapper.update(clockBean) > 0;
		}else{
			result = clockMemberMapper.exitClock(clockId, userId, userId, ConstantsUtil.STATUS_DELETE);
		}
		
		if(result){
			clockHandler.deleteDateClocksCache(userId);
			//获取成员列表
			List<ClockMemberBean> members = clockMemberMapper.members(clockId);
			for(ClockMemberBean member: members){
				
				String content = null;
				//如果是创建者删除任务，就通知所有的成员，清除所有成员的缓存
				if(creater){
					content = "您参与的任务《"+ clockBean.getTitle() +"》已经被创建者删除,请知悉。";
					//清除所有成员的缓存
					if(userId != member.getMemberId())
						clockHandler.deleteDateClocksCache(member.getMemberId());
				}else{
					//不清除其他成员的缓存
					content = user.getAccount() +"已退出任务《"+ clockBean.getTitle() +"》。";
				}
				
				//通知用户(不通知自己)
				notificationHandler.sendNotificationById(false, user, member.getMemberId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
				
			}
			
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除任务ID为：", clockId ,"的宝宝的基本信息：", "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}
	@Override
	public Map<String, Object> dateClocks(String date, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->dateClocks():userId=" +user.getId() +", user=" +user.getAccount()+", date="+ date);
		ResponseMap message = new ResponseMap();
		
		//获取当前的时间
		Date systemTime = new Date();
		List<ClockDisplay> clockDisplays = null;
		int week = -1;
		//判断是否当天的，当天的直接从缓存获取，不是当天的从数据库获取
		if(DateUtil.DateToString(systemTime, "yyyy-MM-dd").equals(date)){
			week = DateUtil.dayForWeek(systemTime);
			clockDisplays = clockHandler.dateClocks(user.getId(), systemTime, week);
		}else{
			week = DateUtil.dayForWeek(DateUtil.stringToDate(date, "yyyy-MM-dd"));
			clockDisplays = clockMapper.dateClocks(user.getId(), week+"", date);
		}
		
		if(CollectionUtil.isNotEmpty(clockDisplays)){
			for(ClockDisplay clockDisplay: clockDisplays){
				//没打卡的
				if(!clockDisplay.isClockIn()){
					//计算任务结束的天数
					clockDisplay.setLeftDay(findDates(DateUtil.stringToDate(date, "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getRepeat_()).size() - 1);
					clockDisplay.setTotalDay(findDates(DateUtil.stringToDate(clockDisplay.getStartDate(), "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getRepeat_()).size());
				}
			}
		}
		message.put("isSuccess", true);
		message.put("message", clockDisplays);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getOngoingClocks(UserBean user, JSONObject json,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->getOngoingClocks():userId=" +user.getId() +", user=" +user.getAccount()+",json="+ json);
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<ClockDisplay> clockDisplays = clockMapper.getMyOngoingClocks(user.getId(), start, pageSize, DateUtil.DateToString(new Date(), "yyyy-MM-dd"));
		if(CollectionUtil.isNotEmpty(clockDisplays)){
			for(ClockDisplay clockDisplay: clockDisplays){
				//计算任务结束的天数
				clockDisplay.setLeftDay(findDates(new Date(), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getRepeat_()).size() - 1);
				clockDisplay.setTotalDay(findDates(DateUtil.stringToDate(clockDisplay.getStartDate(), "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getRepeat_()).size());
			}
		}
		message.put("isSuccess", true);
		message.put("message", clockDisplays);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getEndeds(UserBean user, JSONObject json,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->getEndeds():userId=" +user.getId() +", user=" +user.getAccount()+", json="+ json);
		ResponseMap message = new ResponseMap();
		
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<ClockDisplay> clockDisplays = clockMapper.getMyEndedClocks(user.getId(), start, pageSize, DateUtil.DateToString(new Date(), "yyyy-MM-dd"));
		if(CollectionUtil.isNotEmpty(clockDisplays)){
			for(ClockDisplay clockDisplay: clockDisplays){
				//计算任务结束
				int totalDay = findDates(DateUtil.stringToDate(clockDisplay.getStartDate(), "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getRepeat_()).size();
				clockDisplay.setTotalDay(totalDay);
			}
		}
		message.put("isSuccess", true);
		message.put("message", clockDisplays);
		return message.getMap();
	}
	
	/**
	 * 获取指定时间段内的时间列表
	 * @param dBegin 开始时间
	 * @param dEnd 结束时间
	 * @param repeat 重复时间
	 * @return
	 */
	private static List<Date> findDates(Date dBegin, Date dEnd, String repeat) {
		List<Date> lDate = new ArrayList<Date>();
		if(dBegin == null || dEnd == null)
			return lDate;
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			if(StringUtil.isNotNull(repeat)){
				String week = DateUtil.dayForWeek(calBegin.getTime()) +"";
				if(repeat.contains(week)){
					lDate.add(calBegin.getTime());
				}
			}else{
				lDate.add(calBegin.getTime());
			}
		}
		return lDate;
	}
	
	@Override
	public Map<String, Object> systemClocks(UserBean user,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->systemClocks():userId=" +user.getId() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		List<ClockDisplayGroup> groups = clockHandler.systemClocks();
//		Map<Integer, List<ClockDisplay>> map = new HashMap<Integer, List<ClockDisplay>>();
//		//获取总的子分类
//		List<Map<String, Object>> caList = categoryMapper.children(CLOCK_SYSTEM_CATEGORY_ID, user.getId());
//		List<ClockDisplay> clockDisplays = clockMapper.systemClocks();
//		if(CollectionUtil.isNotEmpty(caList) && CollectionUtil.isNotEmpty(clockDisplays)){
//			for(Map<String, Object> caMap: caList){
//				map.put(StringUtil.changeObjectToInt(caMap.get("id")), new ArrayList<ClockDisplay>());
//			}
//			
//			List<ClockDisplay> tempClockDisplays;
//			for(ClockDisplay clockDisplay: clockDisplays){
//				Integer categoryId = Integer.valueOf(clockDisplay.getCategoryId());
//				if(map.containsKey(categoryId)){
//					tempClockDisplays = map.get(categoryId);
//				}else{
//					tempClockDisplays = new ArrayList<ClockDisplay>();
//				}
//				tempClockDisplays.add(clockDisplay);
//				map.put(categoryId, tempClockDisplays);
//			}
//			
//			for(Map<String, Object> caMap: caList){
//				ClockDisplayGroup group = new ClockDisplayGroup();
//				group.setName(StringUtil.changeNotNull(caMap.get("text")));
//				group.setClockDisplays(map.get(Integer.valueOf(StringUtil.changeObjectToInt(caMap.get("id")))));
//				groups.add(group);
//			}
//		}
//				
		message.put("isSuccess", true);
		message.put("message", groups);
		return message.getMap();
	}


	@Override
	public Map<String, Object> getClock(int clockId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->getClock():json=" +json.toString() +", clockId=" +clockId);
		ResponseMap message = new ResponseMap();
		List<ClockDisplay> clockDisplays = clockMapper.getMyClock(user.getId(), clockId);
		if(CollectionUtil.isEmpty(clockDisplays))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该提醒任务不存在或者不支持共享.value));
		message.put("isSuccess", true);
		message.put("message", clockDisplays.get(0));
		return message.getMap();
	}


	@Override
	public Map<String, Object> search(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("ClockServiceImpl-->search():json=" +json.toString() +", user=" +user.getId());
		String keyword = JsonUtil.getStringValue(json, "keyword");
		if(StringUtil.isNull(keyword))
			throw new ParameterUnspecificationException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.检索关键字不能为空.value));
		ResponseMap message = new ResponseMap();
		List<ClockSearchDisplay> clockSearchDisplays = clockMapper.search(user.getId(), keyword);
		message.put("isSuccess", true);
		message.put("message", clockSearchDisplays);
		return message.getMap();
	}


	@Override
	public Map<String, Object> getClockThumbnail(int clockId, JSONObject json,
			UserBean user, HttpServletRequest request) {
		logger.info("ClockServiceImpl-->getClockThumbnail():json=" +json.toString() +", clockId=" +clockId);
		ResponseMap message = new ResponseMap();
		ClockSearchDisplay clockSearchDisplay = clockMapper.getClockThumbnail(user.getId(), clockId);
		if(clockSearchDisplay == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该提醒任务不存在或者不支持共享.value));
		message.put("isSuccess", true);
		message.put("message", clockSearchDisplay);
		return message.getMap();
	}
	
//	public static void main(String[] args) {
//		System.out.println(StringUtil.getShareId());
//		System.out.println(StringUtil.getShareId());
//		System.out.println(StringUtil.getShareId());
//		System.out.println(StringUtil.getShareId());
//		System.out.println(StringUtil.getShareId());
//		System.out.println(StringUtil.getShareId());
//		System.out.println(StringUtil.getShareId());
//		System.out.println(StringUtil.getShareId());
//	}
}
