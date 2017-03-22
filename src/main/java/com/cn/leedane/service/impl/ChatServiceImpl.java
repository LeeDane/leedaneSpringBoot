package com.cn.leedane.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.FilterUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.ChatMapper;
import com.cn.leedane.model.ChatBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.ChatService;
import com.cn.leedane.service.OperateLogService;

/**
 * 聊天信息列表service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:22:56
 * Version 1.0
 */
@Service("chatService")
public class ChatServiceImpl implements ChatService<ChatBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private ChatMapper chatMapper;
	
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
		int toUserId = JsonUtil.getIntValue(jo, "toUserId"); //发送给对方的用户ID
		if(toUserId < 1 || user.getId() == toUserId){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作权限.value);
			return message;
		}
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //开始的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<>();
		
		if(method.equalsIgnoreCase("uploading") && firstId < 1){
			message.put("isSuccess", true);
			message.put("message", rs);
			return message;
		}
	
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select c.id, c.is_read, c.create_user_id, c.create_user_name, c.to_user_id, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time, c.type, c.content");
			sql.append(" from "+DataTableType.聊天.value+" c where ((c.create_user_id = ? and c.to_user_id =?) or (c.to_user_id =? and c.create_user_id = ?))");
			sql.append(" order by c.id desc limit 0,?");
			rs = chatMapper.executeSQL(sql.toString(), user.getId(), toUserId,  user.getId(), toUserId,pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select c.id, c.is_read, c.create_user_id, c.create_user_name, c.to_user_id , date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time, c.type, c.content");
			sql.append(" from "+DataTableType.聊天.value+" c where ((c.create_user_id = ? and c.to_user_id =?) or (c.to_user_id =? and c.create_user_id = ?))");
			sql.append(" and c.id < ? order by c.id desc limit 0,? ");
			rs = chatMapper.executeSQL(sql.toString(), user.getId(), toUserId, user.getId(), toUserId, firstId, pageSize);
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select c.id, c.is_read, c.create_user_id, c.create_user_name, c.to_user_id , date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time, c.type, c.content");
			sql.append(" from "+DataTableType.聊天.value+" c where ((c.create_user_id = ? and c.to_user_id =?) or (c.to_user_id =? and c.create_user_id = ?))");
			sql.append(" and c.id > ? order by c.id desc limit 0,? ");
			rs = chatMapper.executeSQL(sql.toString(), user.getId(), toUserId, user.getId(), toUserId, lastId, pageSize);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取聊天分页列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
			
		message.put("isSuccess", true);
		message.put("message", rs);
		return message;
	}

	@Override
	public Map<String, Object> send(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatServiceImpl-->send():jo="+jo.toString());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		int toUserId = JsonUtil.getIntValue(jo, "toUserId"); //发送给对方的用户ID
		if(toUserId < 1 || user.getId() == toUserId){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作权限.value);
			return message;
		}
		String content = JsonUtil.getStringValue(jo, "content"); //聊天内容
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message))
			return message;
		
		if(StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.聊天内容不能为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.聊天内容不能为空.value);
			return message;
		}
			
		int type = JsonUtil.getIntValue(jo, "type", 0); //聊天类型
		ChatBean chatBean = new ChatBean();
		chatBean.setContent(content);
		chatBean.setCreateTime(new Date());
		chatBean.setCreateUserId(user.getId());
		chatBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		chatBean.setToUserId(toUserId);
		chatBean.setCreateUserName(user.getAccount());
		chatBean.setType(type);
		if(toUserId == user.getId()){//对于自己发的，标记为已读
			chatBean.setRead(true);
		}
		
		boolean result = chatMapper.save(chatBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"给用户ID为：", toUserId, "发送聊天信息", StringUtil.getSuccessOrNoStr(result)).toString(), "send()", ConstantsUtil.STATUS_NORMAL, 0);
		if(result){
			
			Map<String, Object> chatMap = chatBeanToMap(chatBean);
			//给对方发送通知
			notificationHandler.sendCustomMessageById(user, toUserId, chatMap);
			
			message.put("isSuccess", result);
			message.put("message", chatMap);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		return message;
	}
	
	/**
	 * 将chatbean转化成map集合存储
	 * @param chatBean
	 * @return
	 */
	public Map<String, Object> chatBeanToMap(ChatBean chatBean){
		Map<String, Object> chat = new HashMap<String, Object>();
		chat.put("id", chatBean.getId());
		chat.put("create_user_id", chatBean.getCreateUserId());
		chat.put("to_user_id", chatBean.getToUserId());
		chat.put("create_user_name", chatBean.getCreateUserName());
		chat.put("create_time", DateUtil.DateToString(chatBean.getCreateTime()));
		chat.put("type", chatBean.getType());
		chat.put("content", chatBean.getContent());
		chat.put("is_read", chatBean.isRead());
		return chat;
	}


	@Override
	public Map<String, Object> updateRead(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatServiceImpl-->updateRead():jo="+jo.toString());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		String cids = JsonUtil.getStringValue(jo, "cids"); //聊天信息ID
		if(StringUtil.isNull(cids)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message;
		}
		
		String[] cidArray = cids.split(",");
		ChatBean chatBean = null;
		boolean result = false;
		for(String cid: cidArray){
			chatBean = chatMapper.findById(ChatBean.class, StringUtil.changeObjectToInt(cid));
			if(chatBean != null){
				chatBean.setRead(true);
				result = chatMapper.update(chatBean) > 0;
			}
		} 
	
		if(result){
			message.put("isSuccess", result);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		return message;
	}


	@Override
	public Map<String, Object> noReadList(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatServiceImpl-->noReadList():jo="+jo.toString());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		
		List<Map<String, Object>> rs = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id, c.is_read, c.create_user_id, c.create_user_name, c.to_user_id , date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time, c.type, c.content");
		sql.append(" from "+DataTableType.聊天.value+" c where c.to_user_id =? and c.is_read = ? and c.status=?");
		rs = chatMapper.executeSQL(sql.toString(), user.getId(), false, ConstantsUtil.STATUS_NORMAL);
		
		message.put("isSuccess", true);
		message.put("message", rs);
		
		return message;
	}
	
	@Override
	public Map<String, Object> deleteChat(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatServiceImpl-->deleteChat():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		int cid = JsonUtil.getIntValue(jo, "cid");
		if(cid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message;
		}
		ChatBean chatBean = chatMapper.findById(ChatBean.class, cid);
		String content = "";
		boolean result = false;
		if(chatBean != null){
			content = chatBean.getContent();
			result = chatMapper.deleteById(ChatBean.class, chatBean.getId()) > 0;
			if(result){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除聊天记录成功.value));
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除聊天记录失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.删除聊天记录失败.value);
			}
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除的聊天记录不存在.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除的聊天记录不存在.value);
			return message;
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"刪除聊天记录Id为：", cid , ",内容为：", content, StringUtil.getSuccessOrNoStr(result)).toString(), "deleteChat()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", result);
		return message;
	}

	@Override
	public Map<String, Object> getOneChatByAllUser(JSONObject jo,
			UserBean user, HttpServletRequest request) {
		logger.info("ChatServiceImpl-->getOneChatByAllUser():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", true);
		List<ChatBean> rs = chatMapper.getOneChatByAllUser(user.getId(), ConstantsUtil.STATUS_NORMAL);
		List<Map<String, Object>> resList = new ArrayList<Map<String,Object>>();
		for(ChatBean chatBean: rs){
			resList.add(chatBeanToMap(chatBean));
		}
		message.put("message", resList);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取全部与其有过聊天记录的用户的最新一条聊天信息").toString(), "getOneChatByAllUser()", ConstantsUtil.STATUS_NORMAL, 0);
		return message;
	}
}
