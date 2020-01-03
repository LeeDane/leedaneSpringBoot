package com.cn.leedane.service.impl.baby;

import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.baby.BabyHandler;
import com.cn.leedane.mapper.baby.BabyMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.baby.BabyService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 宝宝service实现类
 * @author LeeDane
 * 2018年6月6日 下午5:08:39
 * version 1.0
 */
@Service("babyService")
public class BabyServiceImpl extends AdminRoleCheckService implements BabyService<BabyBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private BabyMapper babyMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private BabyHandler babyHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("BabyServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		SqlUtil sqlUtil = new SqlUtil();
		BabyBean babyBean = (BabyBean) sqlUtil.getBean(jo, BabyBean.class);
		ResponseMap message = new ResponseMap();
		babyBean.setCreateTime(new Date());
		babyBean.setCreateUserId(user.getId());
		babyBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		babyBean.setModifyTime(new Date());
		babyBean.setModifyUserId(user.getId());
		boolean result = babyMapper.save(babyBean) > 0;
		if(result){
			//清空该用户的宝宝列表缓存
			babyHandler.deleteBabyBeansCache(user.getId());
			message.put("success", true);
			String content = "您已成功创建新的宝宝《"+ babyBean.getNickname() +"》。";
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//通知用户
			notificationHandler.sendNotificationById(true, user.getId(), user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"创建名称为：", babyBean.getNickname(),"的宝宝，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(long babyId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("BabyServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		BabyBean baby = babyHandler.getNormalBaby(babyId, user);
		
		//自动填充更新的bean
		SqlUtil sqlUtil = new SqlUtil();
		baby = (BabyBean)sqlUtil.getUpdateBean(jo, baby);
		baby.setModifyTime(new Date());
		baby.setModifyUserId(user.getId());
		
		boolean result = babyMapper.update(baby) > 0;
		if(result){
			//清除该宝宝的缓存
			babyHandler.deleteBabyBeanCache(baby.getId());
			//清空该用户的宝宝列表缓存
			babyHandler.deleteBabyBeansCache(user.getId());
			message.put("success", true);
			String content= "您已成功修改宝宝《"+ baby.getNickname() +"》的信息！";
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//通知用户
			notificationHandler.sendNotificationById(true, user.getId(), user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改名称为：", baby.getNickname(),"的宝宝的基本信息：", jo.toString(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(long babyId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("BabyServiceImpl-->delete():babyId=" +babyId +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		BabyBean baby = babyHandler.getNormalBaby(babyId, user);
		//这里是逻辑删除
		//baby.setStatus(ConstantsUtil.STATUS_DELETE);
		boolean result = babyMapper.deleteById(BabyBean.class, babyId) > 0;
		if(result){
			//清除该宝宝的缓存
			babyHandler.deleteBabyBeanCache(babyId);
			//清空该用户的宝宝列表缓存
			babyHandler.deleteBabyBeansCache(user.getId());
			message.put("success", true);
			String content = "您已成功删除宝宝《"+ baby.getNickname() +"》！";
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			
			//通知用户
			notificationHandler.sendNotificationById(true, user.getId(), user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除宝宝名称ID为：", babyId ,"的宝宝的基本信息：", "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> changeBorn(long babyId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("BabyServiceImpl-->changeBorn():jsonObject=" +json.toString() +", user=" +user.getAccount()+", babyId="+ babyId);
		ResponseMap message = new ResponseMap();
		//获取宝宝信息(校验是否是自己的宝宝)
		BabyBean babyBean = babyHandler.getNormalBaby(babyId, user);
		babyBean.setBorn(true);
		babyBean.setGregorianBirthDay(new Date());
		babyBean.setModifyTime(new Date());
		babyBean.setModifyUserId(user.getId());
		boolean result = babyMapper.update(babyBean) > 0;
		if(result){
			//清除该宝宝的缓存
			babyHandler.deleteBabyBeanCache(babyBean.getId());
			//清空该用户的宝宝列表缓存
			babyHandler.deleteBabyBeansCache(user.getId());
			message.put("success", true);
			String content= "您已成功修改宝宝《"+ babyBean.getNickname() +"》的信息为出生状态！";
			message.put("message", content);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			//通知用户
			notificationHandler.sendNotificationById(true, user.getId(), user.getId(), content, NotificationType.通知, DataTableType.不存在的表.value, -1, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改宝宝《", babyBean.getNickname() ,"》的信息, 数据是：", json.toString(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "changeBorn()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
}
