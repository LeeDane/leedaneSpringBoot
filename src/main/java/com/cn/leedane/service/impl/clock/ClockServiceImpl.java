package com.cn.leedane.service.impl.clock;

import com.cn.leedane.display.clock.*;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockDynamicHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.handler.clock.ClockMemberHandler;
import com.cn.leedane.mapper.CategoryMapper;
import com.cn.leedane.mapper.clock.*;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.service.clock.ClockService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
	
	@Autowired
	private ClockDynamicHandler clockDynamicHandler;
	
	@Autowired
	private ClockMemberHandler clockMemberHandler;
	
	@Autowired
	private ClockDealMapper clockDealMapper;
	
	@Autowired
	private ClockInMapper clockInMapper;

	@Autowired
	private ClockInResourcesMapper clockInResourcesMapper;

	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
								   HttpRequestInfoBean request) {


		logger.info("ClockServiceImpl-->add():jsonObject=" + jo.toString() + ", user=" + user.getAccount());

		SqlUtil sqlUtil = new SqlUtil();
		ClockBean clockBean = (ClockBean) sqlUtil.getBean(jo, ClockBean.class);

		//参数校验
		if (StringUtil.isNull(clockBean.getTitle()))
			throw new NullPointerException("任务标题不能为空！");

		if (StringUtil.isNull(clockBean.getClockDescribe()))
			throw new NullPointerException("任务描述不能为空！");

		if (clockBean.getStartDate() == null)
			throw new NullPointerException("开始日期不能为空！");

		if (clockBean.getEndDate() != null && clockBean.getStartDate().getTime() > clockBean.getEndDate().getTime())
			throw new ParameterUnspecificationException("开始日期不能大于结束日期！");

//		if(clockBean.getClockStartTime() == null)
//			throw new NullPointerException("打卡开始时间不能为空！");

		if (StringUtil.isNull(clockBean.getClockRepeat()))
			throw new NullPointerException("重复周期不能为空！");

		ResponseMap message = new ResponseMap();
		clockBean.setCreateTime(new Date());
		clockBean.setCreateUserId(user.getId());
		clockBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		clockBean.setModifyTime(new Date());
		clockBean.setModifyUserId(user.getId());
		//参与人数必须大于等于1，因为自己本身就是参与人之一
		if (clockBean.getTakePartNumber() < 1)
			clockBean.setTakePartNumber(1);

		//设置唯一共享ID
		if (clockBean.isShare()) {
			clockBean.setShareId(StringUtil.getShareId());
		}

		boolean result = clockMapper.save(clockBean) > 0;
		if (result) {
			//保存动态信息
			clockDynamicHandler.saveDynamic(clockBean.getId(), new Date(), user.getId(), "创建了任务", true, EnumUtil.CustomMessageExtraType.其他未知类型.value);

			//添加成员
			result = clockMemberService.add(clockBean.getId(), user.getId(), jo, user, request);
			//清空该用户的任务列表缓存
			if (clockBean.getParentId() > 0 || clockBean.getCategoryId() < 1)
				clockHandler.deleteDateClocksCache(user.getId());
			message.put("success", true);
			String content = "您已成功创建新的任务，请准时打卡！";
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		} else {
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(), "创建任务名称为：", clockBean.getTitle(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(long clockId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		long userId = user.getId();
		
		boolean creater = userId == clockBean.getCreateUserId();
		//判断如果是创建人或者是普通成员
		if(creater){
			//先保存用户成员信息
			Map<String, Object> memberUpdate = clockMemberService.update(clockId, user.getId(), jo, user, request);
			if(!StringUtil.changeObjectToBoolean(memberUpdate.get("isSuccess")))
				return memberUpdate;
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
				//清除任务缓存
				clockHandler.deleteClockCache(clockId);
				String content= "修改任务《"+ clockBean.getTitle() +"》成功！";
				message.put("success", true);
				message.put("message", content);
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
				String userName = userHandler.getUserName(userId);
				//通知创建者和所有的成员
				if(clockBean.isShare()){
					List<ClockMemberBean> members = clockMemberHandler.members(clockId);
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
				
				//保存动态信息
				clockDynamicHandler.saveDynamic(clockBean.getId(), new Date(), user.getId(), "更新了任务", true, EnumUtil.CustomMessageExtraType.其他未知类型.value);
				
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
			}
		}else{
			//保存操作日志
			operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改名称为：", clockBean.getTitle(),"的任务的基本信息：", jo.toString(), "，结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
			return clockMemberService.update(clockId, user.getId(), jo, user, request);
		}
		
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(long clockId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->delete():clockId=" +clockId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		//校验
		ClockBean clockBean = clockHandler.getNormalClock(clockId);
		//检验是否在成员列表中
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务");

		long userId = user.getId();
		boolean creater = clockBean.getCreateUserId() == userId;
		boolean result = true;

		//创建者直接删除任务
		if(creater){
			//这里是逻辑删除
			clockBean.setStatus(ConstantsUtil.STATUS_DELETE);
			clockBean.setModifyTime(new Date());
			result = clockMapper.update(clockBean) > 0;
		}else{//非创建者退出任务
			//判断是否可以自动退出
			if(!clockBean.isAutoOut())
				throw new UnauthorizedException("该任务不允许自动退出，可以跟任务管理员协商处理或者联系系统管理员介入。");
			result = clockMemberMapper.exitClock(clockId, userId, userId, ConstantsUtil.STATUS_DELETE);
		}
		
		if(result){
			clockHandler.deleteDateClocksCache(userId);
			clockMemberHandler.deleteClockMembersCache(clockId);
			//清除任务缓存
			clockHandler.deleteClockCache(clockId);
			
			//设置deal表的状态
			clockDealMapper.updateStatus(clockId, userId, ConstantsUtil.STATUS_DELETE);			
			//获取成员列表
			List<ClockMemberBean> members = clockMemberHandler.members(clockId);
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
				notificationHandler.sendNotificationById(false, user.getId(), member.getMemberId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
				
			}
			
			//保存动态信息
			clockDynamicHandler.saveDynamic(clockBean.getId(), new Date(), user.getId(), "删除了任务", true, EnumUtil.CustomMessageExtraType.其他未知类型.value);
			
			message.put("success", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除任务ID为：", clockId ,"的宝宝的基本信息：", "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	@Override
	public Map<String, Object> dateClocks(String date, UserBean user,
			HttpRequestInfoBean request) {
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
				if(clockDisplay.getClockInStatus() == 0){
					//计算任务结束的天数
					clockDisplay.setLeftDay(findDates(DateUtil.stringToDate(date, "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getClockRepeat()).size() - 1);
					clockDisplay.setTotalDay(findDates(DateUtil.stringToDate(clockDisplay.getStartDate(), "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getClockRepeat()).size());
				}
			}
		}
		message.put("success", true);
		message.put("message", clockDisplays);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getOngoingClocks(UserBean user, JSONObject json,
			HttpRequestInfoBean request) {
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
				clockDisplay.setLeftDay(findDates(new Date(), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getClockRepeat()).size() - 1);
				clockDisplay.setTotalDay(findDates(DateUtil.stringToDate(clockDisplay.getStartDate(), "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getClockRepeat()).size());
			}
		}
		message.put("success", true);
		message.put("message", clockDisplays);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getEndeds(UserBean user, JSONObject json,
			HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->getEndeds():userId=" +user.getId() +", user=" +user.getAccount()+", json="+ json);
		ResponseMap message = new ResponseMap();
		
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		String dd = DateUtil.DateToString(new Date(), "yyyy-MM-dd");
		List<ClockDisplay> clockDisplays = clockMapper.getMyEndedClocks(user.getId(), start, pageSize, dd);
		if(CollectionUtil.isNotEmpty(clockDisplays)){
			for(ClockDisplay clockDisplay: clockDisplays){
				//计算任务结束
				int totalDay = findDates(DateUtil.stringToDate(clockDisplay.getStartDate(), "yyyy-MM-dd"), DateUtil.stringToDate(clockDisplay.getEndDate(), "yyyy-MM-dd"), clockDisplay.getClockRepeat()).size();
				clockDisplay.setTotalDay(totalDay);
			}
		}
		message.put("success", true);
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
			HttpRequestInfoBean request) {
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
		message.put("success", true);
		message.put("message", groups);
		return message.getMap();
	}


	@Override
	public Map<String, Object> getClock(long clockId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->getClock():json=" +json.toString() +", clockId=" +clockId);
		ResponseMap message = new ResponseMap();
		List<ClockDisplay> clockDisplays = clockMapper.getMyClock(user.getId(), clockId);
		if(CollectionUtil.isEmpty(clockDisplays))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该提醒任务不存在或者不支持共享.value));
		
		//保存动态信息
		clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), "查看任务信息", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);

		ClockDisplay display = clockDisplays.get(0);
		display.setMembers(clockMemberHandler.members(clockId).size());
		message.put("success", true);
		message.put("message", display);
		return message.getMap();
	}


	@Override
	public Map<String, Object> search(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->search():json=" +json.toString() +", user=" +user.getId());
		String keyword = JsonUtil.getStringValue(json, "keyword");
		if(StringUtil.isNull(keyword))
			throw new ParameterUnspecificationException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.检索关键字不能为空.value));
		ResponseMap message = new ResponseMap();
		List<ClockSearchDisplay> clockSearchDisplays = clockMapper.search(user.getId(), keyword);
		message.put("success", true);
		message.put("message", clockSearchDisplays);
		return message.getMap();
	}


	@Override
	public Map<String, Object> getClockThumbnail(long clockId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->getClockThumbnail():json=" +json.toString() +", clockId=" +clockId);
		ResponseMap message = new ResponseMap();

		//满足某些需要加入任务才能查看的场景使用
		boolean mustIn = JsonUtil.getBooleanValue(json,"in",  false);
		//先判断任务是否存在
		clockHandler.getNormalClock(clockId);
		if(mustIn){
			//检验是否在成员列表中
			if(!clockMemberHandler.inMember(user.getId(), clockId))
				throw new UnauthorizedException("您还未加入该任务");
		}
		
		ClockSearchDisplay clockSearchDisplay = clockMapper.getClockThumbnail(user.getId(), clockId);
		if(clockSearchDisplay == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该提醒任务不存在或者不支持共享.value));
		
		//保存动态信息
		clockDynamicHandler.saveDynamic(clockId, new Date(), user.getId(), "查看任务的缩略信息", false, EnumUtil.CustomMessageExtraType.其他未知类型.value);
				
		clockSearchDisplay.setMemberNumber(clockMemberHandler.members(clockId).size());
		message.put("success", true);
		message.put("message", clockSearchDisplay);
		return message.getMap();
	}


	@Override
	public Map<String, Object> statistics(long clockId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->getClockThumbnail():json=" +json.toString() +", clockId=" +clockId);
		ResponseMap message = new ResponseMap();
		//先判断任务是否存在
		ClockBean clock = clockHandler.getNormalClock(clockId);
		
		//检验是否在成员列表中
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法查看统计");
		
		ClockStatisticsDisplay statisticsDisplay = new ClockStatisticsDisplay();
		
		List<ClockMemberBean> meBeans = clockMemberHandler.members(clockId);
		statisticsDisplay.setClockId(clockId);
		statisticsDisplay.setCreateTime(DateUtil.DateToString(clock.getCreateTime()));
		statisticsDisplay.setCreateUserId(clock.getCreateUserId());
		
		statisticsDisplay.setCreateAccount(userHandler.getUserName(clock.getCreateUserId()));
		if(CollectionUtil.isNotEmpty(meBeans)){
			statisticsDisplay.setMember(meBeans.size());
			statisticsDisplay.setAges(getAges(meBeans));
			statisticsDisplay.setSexs(getSexs(meBeans));
		}
		
		//获取任务的时间列表
		List<Date> allDates = findDates(clock.getStartDate(), clock.getEndDate() == null ? new Date(): (clock.getEndDate().getTime() > new Date().getTime() ?  new Date(): clock.getEndDate()) , clock.getClockRepeat());
		if(CollectionUtil.isNotEmpty(allDates)){
			int dayIndex = (allDates.size() - 1) > 7 ? 7: allDates.size() - 1;
			List<Date> searchDates = new ArrayList<Date>();
			for(int i = allDates.size() - dayIndex -1; i < allDates.size() -1 ; i++){
				searchDates.add(allDates.get(i));
			}
			//取最近7天的日期
			String start = DateUtil.DateToString(searchDates.get(0), "yyyy-MM-dd");
			String end = DateUtil.DateToString(searchDates.get(searchDates.size() - 1), "yyyy-MM-dd");
			List<Map<String, String>> data = clockInMapper.getClockInsRangeDate(clockId, start, end);
			statisticsDisplay.setResults(getClockIns(searchDates, data));
		}
		statisticsDisplay.setMembers(getTop3Member(clockId));
		message.put("success", true);
		message.put("message", statisticsDisplay);
		return message.getMap();
	}

	@Override
	public Map<String, Object> resources(long clockId, int resourceType, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("ClockServiceImpl-->resources():json=" +json.toString() +", clockId=" +clockId + ", resourceType="+ resourceType);
		ResponseMap message = new ResponseMap();
		//先判断任务是否存在
		ClockBean clockBean = clockHandler.getNormalClock(clockId);

		//检验是否在成员列表中
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法查看资源列表");

		if(!clockBean.isSeeEachOther() && clockBean.getCreateUserId() != user.getId())
			throw new UnauthorizedException("已被设置为不能查看资源");

		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(json, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		boolean userPic = JsonUtil.getBooleanValue(json, "userPic"); //标记是否获取创建人头像

		List<ClockInResourceDisplay> resourceDisplays = clockInResourcesMapper.resources(clockId, resourceType, start, pageSize);
		if(userPic){
			for(ClockInResourceDisplay clockInResourceDisplay: resourceDisplays)
				clockInResourceDisplay.setPicPath(userHandler.getUserPicPath(clockInResourceDisplay.getCreateUserId(), "30x30"));
		}
		message.put("success", true);
		message.put("message", resourceDisplays);
		return message.getMap();
	}

	/**
	 * 获取排名前三的额用户
	 * @param clockId
	 * @return
	 */
	private List<Map<String, Object>> getTop3Member(long clockId) {
		List<Map<String, Object>> members = clockMemberMapper.membersSortByIns(clockId, 3);
		if(CollectionUtil.isNotEmpty(members)){
			for(Map<String, Object> mb: members){
				int userId = StringUtil.changeObjectToInt(mb.get("member_id"));
				mb.put("account", userHandler.getUserName(userId));
				mb.put("image", userHandler.getUserPicPath(userId, "30x30"));
			}
			return members;
		}else
			return new ArrayList<Map<String, Object>>();
	}


	/**
	 * 获取任务的打卡情况列表
	 * @param searchDates
	 * @param data
	 * @return
	 */
	private List<Map<String, Object>> getClockIns(List<Date> searchDates,
			List<Map<String, String>> data) {
		List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
		if(CollectionUtil.isNotEmpty(searchDates)){
			for(Date date: searchDates){
				array.add(getObjectByResult(date, data));
			}
		}
		return array;
	}

	/**
	 * 获得结果对象
	 * @param date
	 * @param data
	 * @return
	 */
	private Map<String, Object> getObjectByResult(Date date, List<Map<String, String>> data) {
		Map<String, Object> map = new HashMap<String, Object>();
		String d = DateUtil.DateToString(date, "yyyy-MM-dd");
		map.put("clock_date", d);
		map.put("number", 0);
		if(CollectionUtil.isNotEmpty(data)){
			for(Map<String, String> m: data){
				if(m.containsKey("clock_date") && d.equalsIgnoreCase(StringUtil.changeNotNull(m.get("clock_date")))){
					map.put("number", StringUtil.changeObjectToInt(m.get("number")));
					break;
				}
			}
		}
		return map;
	}


	/**
	 * 获取性别的统计
	 * @param meBeans
	 * @return
	 */
	private Map<String, Integer> getSexs(List<ClockMemberBean> meBeans) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(ClockMemberBean member: meBeans){
			String sex = member.getSex();
			if(StringUtil.isNotNull(sex) && sex.indexOf("男") > -1){
				map.put("男", map.get("男") == null ? 1: map.get("男") + 1);
				continue;
			}
			
			if(StringUtil.isNotNull(sex) && sex.indexOf("女") > -1){
				map.put("女", map.get("女") == null ? 1: map.get("女") + 1);
				continue;
			}
			
			map.put("未知", map.get("未知") == null ? 1: map.get("未知") + 1);
		}
		return map;
	}


	/**
	 * 获取年龄的统计
	 * @param meBeans
	 * @return
	 */
	private Map<String, Integer> getAges(List<ClockMemberBean> meBeans) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(ClockMemberBean member: meBeans){
			int age = member.getAge();
			if(age > 0 && age < 20){
				map.put("20岁以下", map.get("20岁以下") == null ? 1: map.get("20岁以下") + 1);
			}else if(age >= 20 && age < 30){
				map.put("20到30岁", map.get("20到30岁") == null ? 1: map.get("20到30岁") + 1);
			}else if(age >= 30 && age < 40){
				map.put("30到40岁", map.get("30到40岁") == null ? 1: map.get("30到40岁") + 1);
			}else if(age >= 40 && age < 50){
				map.put("40到50岁", map.get("40到50岁") == null ? 1: map.get("40到50岁") + 1);
			}else if(age >= 50){
				map.put("50岁以上", map.get("50岁以上") == null ? 1: map.get("50岁以上") + 1);
			}else{
				map.put("未填写", map.get("未填写") == null ? 1: map.get("未填写") + 1);
			}
		}
		return map;
	}

}
