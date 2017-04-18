package com.cn.leedane.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.FilterUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.PrivateChatMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.PrivateChatBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.PrivateChatService;

/**
 * 私信信息列表service实现类
 * @author LeeDane
 * 2016年7月12日 下午2:04:19
 * Version 1.0
 */
@Service("privateChatService")
public class PrivateChatServiceImpl implements PrivateChatService<PrivateChatBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private PrivateChatMapper privateChatMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private UserHandler userHandler;

	@Autowired
	private NotificationHandler notificationHandler;

	@Override
	public Map<String, Object> getLimit(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatServiceImpl-->getLimit():jo="+jo.toString());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		return message;
	}


	@Override
	public Map<String, Object> send(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("PrivateChatServiceImpl-->send():jo="+jo.toString());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		int toUserId = JsonUtil.getIntValue(jo, "to_user_id"); //发送给对方的用户ID
		if(toUserId < 1 || user.getId() == toUserId){
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户不存在或请求参数不对.value));
		}
		String content = JsonUtil.getStringValue(jo, "content"); //私信内容
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message))
			return message;
		
		if(StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.私信内容不能为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.私信内容不能为空.value);
			return message;
		}
			
		int type = JsonUtil.getIntValue(jo, "type", 0); //私信类型
		PrivateChatBean privateChatBean = new PrivateChatBean();
		privateChatBean.setContent(content);
		privateChatBean.setCreateTime(new Date());
		privateChatBean.setCreateUserId(user.getId());
		privateChatBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		privateChatBean.setToUserId(toUserId);
		privateChatBean.setType(type);
		
		boolean result = privateChatMapper.save(privateChatBean) > 0;
			
		if(result){
			
			Map<String, Object> chatMap = privateChatBeanToMap(privateChatBean);
			//给对方发送通知
			notificationHandler.sendNotificationById(false, user, toUserId, content, NotificationType.私信, DataTableType.私信.value, privateChatBean.getId(), null);
			
			message.put("isSuccess", result);
			message.put("message", chatMap);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"给用户Id为：", toUserId, "发送私信，内容是：" , content, StringUtil.getSuccessOrNoStr(result)).toString(), "send()", ConstantsUtil.STATUS_NORMAL, 0);
					
		return message;
	}
	
	/**
	 * 将chatbean转化成map集合存储
	 * @param chatBean
	 * @return
	 */
	public static Map<String, Object> privateChatBeanToMap(PrivateChatBean privateChatBean){
		Map<String, Object> chat = new HashMap<String, Object>();
		chat.put("id", privateChatBean.getId());
		chat.put("create_user_id", privateChatBean.getCreateUserId());
		chat.put("to_user_id", privateChatBean.getToUserId());
		chat.put("create_time", DateUtil.DateToString(privateChatBean.getCreateTime()));
		chat.put("type", privateChatBean.getType());
		chat.put("content", privateChatBean.getContent());
		return chat;
	}
}
