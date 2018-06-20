package com.cn.leedane.service.impl.baby;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.display.keyValueDisplay;
import com.cn.leedane.display.baby.LifesResultDisplay;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.baby.BabyHandler;
import com.cn.leedane.handler.baby.BabyLifeHandler;
import com.cn.leedane.mapper.baby.BabyLifeMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.model.baby.BabyLifeBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.baby.BabyLifeService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.BabyLifeType;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.baby.BabyUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 宝宝生活方式service实现类
 * @author LeeDane
 * 2018年6月11日 下午6:44:19
 * version 1.0
 */
@Service("babyLifeService")
public class BabyLifeServiceImpl extends AdminRoleCheckService implements BabyLifeService<BabyLifeBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private BabyLifeMapper babyLifeMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private BabyLifeHandler babyLifeHandler;
	
	@Autowired
	private BabyHandler babyHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Override
	public Map<String, Object> add(int babyId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("BabyLifeServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount() +", babyId="+ babyId);
		
		//获取宝宝信息(校验是否是自己的宝宝)
		BabyBean babyBean = babyHandler.getNormalBaby(babyId, user);
		
		SqlUtil sqlUtil = new SqlUtil();
		BabyLifeBean babyLife = (BabyLifeBean) sqlUtil.getBean(jo, BabyLifeBean.class);
		ResponseMap message = new ResponseMap();
		babyLife.setCreateTime(new Date());
		babyLife.setCreateUserId(user.getId());
		babyLife.setStatus(ConstantsUtil.STATUS_NORMAL);
		babyLife.setModifyTime(new Date());
		babyLife.setModifyUserId(user.getId());
		babyLife.setBabyId(babyId);
		boolean result = babyLifeMapper.save(babyLife) > 0;
		if(result){
			//清空该用户的宝宝列表缓存
			babyLifeHandler.deleteBabyLifeCache(babyId, babyLife.getId());
			message.put("isSuccess", true);
			String content = "您成功为宝宝《"+ babyBean.getNickname() +"》添加一条新的生活方式记录！";
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//通知用户
			notificationHandler.sendNotificationById(true, user, user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"您为宝宝《"+ babyBean.getNickname() +"》添加一条心的生活方式记录！，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(int babyId, int lifeId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("BabyLifeServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount()+", babyId="+ babyId +", lifeId="+ lifeId);
		ResponseMap message = new ResponseMap();
		//获取宝宝信息(校验是否是自己的宝宝)
		BabyBean babyBean = babyHandler.getNormalBaby(babyId, user);
		
		///自动填充更新的bean(校验是否是自己宝宝的生活方式)
		BabyLifeBean babyLife = babyLifeHandler.getNormalBabyLife(babyBean.getId(), lifeId, user);
		SqlUtil sqlUtil = new SqlUtil();
		BabyLifeBean babyLifeBean = (BabyLifeBean)sqlUtil.getUpdateBean(jo, babyLife);
		babyLifeBean.setModifyTime(new Date());
		babyLifeBean.setModifyUserId(user.getId());
		boolean result = babyLifeMapper.update(babyLifeBean) > 0;
		if(result){
			//清除该生活方式缓存
			babyLifeHandler.deleteBabyLifeCache(babyBean.getId(), babyLifeBean.getId());
			message.put("isSuccess", true);
			String content = "您已修改了宝宝《"+ babyBean.getNickname() +"》的生活方式！";
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			
			//通知用户
			notificationHandler.sendNotificationById(true, user, user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改宝宝《", babyBean.getNickname() ,"》的生活方式, 数据是：", jo.toString(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int babyId, int lifeId, UserBean user,
			HttpServletRequest request) {
		logger.info("BabyLifeServiceImpl-->delete():babyId=" +babyId +", user=" +user.getAccount()+", lifeId="+ lifeId);
		ResponseMap message = new ResponseMap();
		//获取宝宝信息(校验是否是自己的宝宝)
		BabyBean baby = babyHandler.getNormalBaby(babyId, user);
		
		///自动填充更新的bean(校验是否是自己宝宝的生活方式)
		babyLifeHandler.getNormalBabyLife(babyId, lifeId, user);
		//这里是逻辑删除
		//babyLife.setStatus(ConstantsUtil.STATUS_DELETE);
		boolean result = babyLifeMapper.deleteById(BabyLifeBean.class, lifeId) > 0;
		if(result){
			//清除该宝宝生活方式的缓存
			babyLifeHandler.deleteBabyLifeCache(babyId, lifeId);
			message.put("isSuccess", true);
			String content = "您已成功删除了宝宝《"+ baby.getNickname() +"》的生活方式记录！";
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//通知用户
			notificationHandler.sendNotificationById(true, user, user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除宝宝《", baby.getNickname(), "》生活方式，ID为：", lifeId, "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}


	@Override
	public Map<String, Object> lifes(int babyId, String startDate,
			String endDate, String keyWord, int lifeType, UserBean user,
			HttpServletRequest request) {
		logger.info("BabyLifeServiceImpl-->paging():babyId=" +babyId + ", startDate="+ startDate +", endDate="+ endDate +", keyWord="+ keyWord +", lifeType="+ lifeType +", user="+ user.getAccount());
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		ResponseMap message = new ResponseMap();
		LifesResultDisplay display = new LifesResultDisplay();
		List<BabyLifeBean> babyLifes = babyLifeMapper.lifes(user.getId(), babyId, startDate, endDate, keyWord, lifeType, 0, 1000, ConstantsUtil.STATUS_NORMAL);
		if(CollectionUtil.isNotEmpty(babyLifes)){
			int eat = 0;
			int sleep = 0;
			int wash = 0;
			int sick = 0;
			//记录所有数据的日期
			Set<String> sourceDates = new HashSet<String>();
			
			for(BabyLifeBean life: babyLifes){
				list.add(BabyUtil.transform(life));
				if(life.getLifeType() == BabyLifeType.吃喝.value){
					eat ++;
				}if(life.getLifeType() == BabyLifeType.睡觉.value){
					sleep ++;
				}if(life.getLifeType() == BabyLifeType.洗刷.value){
					wash ++;
				}if(life.getLifeType() == BabyLifeType.臭臭.value){
					sick ++;
				}
				
				sourceDates.add(DateUtil.DateToString(life.getOccurDate(), "yyyy-MM-dd"));
			}
			display.setEatNumber(eat);
			display.setSleepNumber(sleep);
			display.setWashNumber(wash);
			display.setSickNumber(sick);
			
			//处理日期列表
			List<String> dates = new ArrayList<String>();
			boolean day = false;
			if(!sourceDates.isEmpty()){
				List<String> sourceList = new ArrayList<>(sourceDates);
				Collections.sort(sourceList);
				String start = sourceList.get(0);
				String end = sourceList.get(sourceList.size() - 1);
				//变成24小时的
				if(end.equals(start)){
					for(int i = 0; i < 24; i++)
						dates.add(i +"");
				}else{
					List<Date> dateList = DateUtil.findDates(DateUtil.stringToDate(start, "yyyy-MM-dd"), DateUtil.stringToDate(end, "yyyy-MM-dd"));
					for(Date d: dateList){
						dates.add(DateUtil.DateToString(d, "yyyy-MM-dd"));
					}
					day = true;
				}
			}
			
			Map<String, List<BabyLifeBean>> eatLifes = new HashMap<String, List<BabyLifeBean>>();
			Map<String, List<BabyLifeBean>> sleepLifes = new HashMap<String, List<BabyLifeBean>>();
			Map<String, List<BabyLifeBean>> washLifes = new HashMap<String, List<BabyLifeBean>>();
			Map<String, List<BabyLifeBean>> sickLifes = new HashMap<String, List<BabyLifeBean>>();
			List<BabyLifeBean> lifebs;
			for(BabyLifeBean life: babyLifes){
				String dv = day? DateUtil.DateToString(life.getOccurDate(), "yyyy-MM-dd"): DateUtil.DateToString(life.getOccurTime(), "yyyy-MM-dd HH").substring(11, 13);
				
				if(life.getLifeType() == BabyLifeType.吃喝.value){
					eat ++;
					if(eatLifes.containsKey(dv)){
						lifebs = eatLifes.get(dv);
					}else{
						lifebs = new ArrayList<BabyLifeBean>();
					}
					lifebs.add(life);
					eatLifes.put(dv, lifebs);
				}if(life.getLifeType() == BabyLifeType.睡觉.value){
					sleep ++;
					if(sleepLifes.containsKey(dv)){
						lifebs = sleepLifes.get(dv);
					}else{
						lifebs = new ArrayList<BabyLifeBean>();
					}
					lifebs.add(life);
					sleepLifes.put(dv, lifebs);
				}if(life.getLifeType() == BabyLifeType.洗刷.value){
					wash ++;
					if(washLifes.containsKey(dv)){
						lifebs = washLifes.get(dv);
					}else{
						lifebs = new ArrayList<BabyLifeBean>();
					}
					lifebs.add(life);
					washLifes.put(dv, lifebs);
				}if(life.getLifeType() == BabyLifeType.臭臭.value){
					sick ++;
					if(sickLifes.containsKey(dv)){
						lifebs = sickLifes.get(dv);
					}else{
						lifebs = new ArrayList<BabyLifeBean>();
					}
					lifebs.add(life);
					sickLifes.put(dv, lifebs);
				}
				
				sourceDates.add(DateUtil.DateToString(life.getOccurDate(), "yyyy-MM-dd"));
			}
			
			display.setSeries(getSeries(eatLifes, sleepLifes, washLifes, sickLifes, dates, day));
			display.setxAxis(dates);
			
		}
		display.setDisplays(list);
		if(StringUtil.isNotNull(endDate) && StringUtil.isNull(startDate)){
			display.setOccurDate("到" + endDate +"为止");
		}else if(StringUtil.isNotNull(startDate) && StringUtil.isNull(endDate)){
			display.setOccurDate("从" + startDate +"开始");
		}else if(StringUtil.isNull(startDate) && StringUtil.isNull(endDate)){
			display.setOccurDate("查询全部数据");
		}else{
			display.setOccurDate(startDate + (StringUtil.isNotNull(endDate) && !startDate.equals(endDate)? "至" + endDate: ""));
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看宝宝id为"+babyId+"的生活方式列表", "lifes()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", display);
		message.put("isSuccess", true);
		return message.getMap();
	}


	/**
	 * 获取图表展示的series数据
	 * @param babyLifes
	 * @param dates
	 * @param day
	 * @return
	 */
	private JSONArray getSeries(Map<String, List<BabyLifeBean>> eatLifes, Map<String, List<BabyLifeBean>> sleepLifes, Map<String, List<BabyLifeBean>> washLifes, Map<String, List<BabyLifeBean>> sickLifes, List<String> dates, boolean day) {
		JSONArray series = new JSONArray();
		if(CollectionUtil.isNotEmpty(dates)){
			List<Integer> eatValues = new ArrayList<Integer>();
			List<Integer> sleepValues = new ArrayList<Integer>();
			List<Integer> washValues = new ArrayList<Integer>();
			List<Integer> sickValues = new ArrayList<Integer>();
			for(String date: dates){
				eatValues.add(day? (eatLifes.containsKey(date) ? eatLifes.get(date).size(): 0): (eatLifes.containsKey(Integer.parseInt(date) > 9 ? date : "0"+ date) ? eatLifes.get(Integer.parseInt(date) > 9 ? date : "0"+ date).size(): 0));
				sleepValues.add(day? (sleepLifes.containsKey(date) ? sleepLifes.get(date).size(): 0): (sleepLifes.containsKey(Integer.parseInt(date) > 9 ? date : "0"+ date) ? sleepLifes.get(Integer.parseInt(date) > 9 ? date : "0"+ date).size(): 0));
				washValues.add(day? (washLifes.containsKey(date) ? washLifes.get(date).size(): 0): (washLifes.containsKey(Integer.parseInt(date) > 9 ? date : "0"+ date) ? washLifes.get(Integer.parseInt(date) > 9 ? date : "0"+ date).size(): 0));
				sickValues.add(day? (sickLifes.containsKey(date) ? sickLifes.get(date).size(): 0): (sickLifes.containsKey(Integer.parseInt(date) > 9 ? date : "0"+ date) ? sickLifes.get(Integer.parseInt(date) > 9 ? date : "0"+ date).size(): 0));
			}
			Map<String, Object> eatItemMap = new HashMap<String, Object>();
			eatItemMap.put("type", "bar");
			eatItemMap.put("name", "吃喝");
			eatItemMap.put("stack", "生活方式");
			eatItemMap.put("itemStyle", JSONObject.fromObject("{\"color\": \"#337ab7\"}"));
			eatItemMap.put("tooltip", JSONObject.fromObject("{'show': true, 'formatter':'吃喝: {c0}'}"));
			eatItemMap.put("data", eatValues);
			series.add(eatItemMap);
			Map<String, Object> sleepItemMap = new HashMap<String, Object>();
			sleepItemMap.put("type", "bar");
			sleepItemMap.put("name", "睡觉");
			sleepItemMap.put("stack", "生活方式");
			sleepItemMap.put("itemStyle", JSONObject.fromObject("{\"color\": \"#5cb85c\"}"));
			sleepItemMap.put("tooltip", JSONObject.fromObject("{'show': true, 'formatter':'睡觉: {c0}'}"));
			sleepItemMap.put("data", sleepValues);
			series.add(sleepItemMap);
			Map<String, Object> washItemMap = new HashMap<String, Object>();
			washItemMap.put("type", "bar");
			washItemMap.put("name", "洗刷");
			washItemMap.put("stack", "生活方式");
			washItemMap.put("itemStyle", JSONObject.fromObject("{\"color\": \"#f0ad4e\"}"));
			washItemMap.put("tooltip", JSONObject.fromObject("{'show': true, 'formatter':'洗刷: {c0}'}"));
			washItemMap.put("data", washValues);
			series.add(washItemMap);
			Map<String, Object> sickItemMap = new HashMap<String, Object>();
			sickItemMap.put("type", "bar");
			sickItemMap.put("name", "臭臭");
			sickItemMap.put("stack", "生活方式");
			sickItemMap.put("itemStyle", JSONObject.fromObject("{\"color\": \"#d9534f\"}"));
			sickItemMap.put("tooltip", JSONObject.fromObject("{'show': true, 'formatter':'臭臭: {c0}'}"));
			sickItemMap.put("data", sickValues);
			series.add(sickItemMap);
		}
		return series;
	}
}
