package com.cn.leedane.service.impl;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.NotificationMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.NotificationService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 通知service的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:00:51
 * Version 1.0
 */
@Service("notificationService")
public class NotificationServiceImpl extends AdminRoleCheckService implements NotificationService<NotificationBean>{
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private NotificationMapper notificationMapper;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private CommonHandler commonHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Override
	public boolean save(NotificationBean t) {
		return notificationMapper.save(t) > 0;
	}

	@Override
	public Map<String, Object> getLimit(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("NotificationServiceImpl-->getLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String type = JsonUtil.getStringValue(jo, "type"); //通知类型
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		long lastId = JsonUtil.getLongValue(jo, "last_id"); //开始的页数
		long firstId = JsonUtil.getLongValue(jo, "first_id"); //结束的页数
		
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(type))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该通知类型不存在.value));
			
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		//分页查找该用户的通知列表
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select n.id, n.from_user_id, n.to_user_id, n.content, n.type, n.extra, n.create_time, n.table_name, n.table_id, n.is_push_error, n.is_read");
			sql.append(" from "+DataTableType.通知.value+" n where n.to_user_id = ? and n.status = ? and n.type=?");
			sql.append(" order by n.id desc limit 0,?");
			rs = notificationMapper.executeSQL(sql.toString(), user.getId(), ConstantsUtil.STATUS_NORMAL, type, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select n.id, n.from_user_id, n.to_user_id, n.content, n.type, n.extra, n.create_time, n.table_name, n.table_id, n.is_push_error, n.is_read");
			sql.append(" from "+DataTableType.通知.value+" n where n.to_user_id = ? and n.status = ? and n.type=?");
			sql.append(" and n.id < ? order by n.id desc limit 0,? ");
			rs = notificationMapper.executeSQL(sql.toString(), user.getId(), ConstantsUtil.STATUS_NORMAL, type, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select n.id, n.from_user_id, n.to_user_id, n.content, n.type, n.extra, n.create_time, n.table_name, n.table_id, n.is_push_error, n.is_read");
			sql.append(" from "+DataTableType.通知.value+" n where n.to_user_id = ? and n.status = ? and n.type=?");
			sql.append(" and n.id > ? limit 0,? ");
			rs = notificationMapper.executeSQL(sql.toString(), user.getId(), ConstantsUtil.STATUS_NORMAL, type, firstId, pageSize);
		}
		
		if(rs !=null && rs.size() > 0){
			int fromUserId = 0;
			String tableName = null;
			int tableId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				//在非获取指定表下的评论列表的情况下的前面35个字符
				tableName = StringUtil.changeNotNull((rs.get(i).get("table_name")));
				tableId = StringUtil.changeObjectToInt(rs.get(i).get("table_id"));
				rs.get(i).put("source", commonHandler.getContentByTableNameAndId(tableName, tableId, user));
			
				fromUserId = StringUtil.changeObjectToInt(rs.get(i).get("from_user_id"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(fromUserId));
			}	
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取收到的通知列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("success", true);
		message.put("message", rs);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> paging(String type,
			int pageSize, int current, int total,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("NotificationServiceImpl-->paging():type=" +type + ", current="+ current +", total="+ total + ", user=" +user.getAccount());
		if(pageSize < 1)
			pageSize = ConstantsUtil.DEFAULT_PAGE_SIZE;
		
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> rs = notificationMapper.paging(user.getId(), type, ConstantsUtil.STATUS_NORMAL, SqlUtil.getPageStart(current, pageSize, total), pageSize);
		if(CollectionUtil.isNotEmpty(rs)){
			int fromUserId = 0;
			String tableName = null;
			int tableId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				//在非获取指定表下的评论列表的情况下的前面35个字符
				tableName = StringUtil.changeNotNull((rs.get(i).get("table_name")));
				tableId = StringUtil.changeObjectToInt(rs.get(i).get("table_id"));
				rs.get(i).putAll(commonHandler.getSourceByTableNameAndId(tableName, tableId, user));
			
				fromUserId = StringUtil.changeObjectToInt(rs.get(i).get("from_user_id"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(fromUserId));
			}
		}
		message.put("total", SqlUtil.getTotalByList(notificationMapper.getTotal(DataTableType.通知.value, "where to_user_id = " + user.getId() +" and status = "+ConstantsUtil.STATUS_NORMAL+" and type ='"+ type +"'")));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"分页获取收到的通知列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("success", true);
		message.put("message", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> sendBroadcast(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("NotificationServiceImpl-->sendBroadcast():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		String broadcast = JsonUtil.getStringValue(jo, "broadcast");
		if(StringUtil.isNull(broadcast)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		
		boolean result = notificationHandler.sendBroadcast(broadcast);
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"向所有在线用户发送通知", StringUtil.getSuccessOrNoStr(result)).toString(), "sendBroadcast()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		message.put("success", result);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> deleteNotification(long nid, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("NotificationServiceImpl-->deleteNotification():nid=" +nid +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		if(nid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			
		NotificationBean notificationBean = notificationMapper.findById(NotificationBean.class, nid);
		if(notificationBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除的通知不存在.value));
		
		checkAdmin(user, notificationBean.getToUserId());
		
		boolean result = false;
		result = notificationMapper.deleteById(NotificationBean.class, nid) > 0;
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除通知成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除通知失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除通知失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"刪除通知Id为：", nid , StringUtil.getSuccessOrNoStr(result)).toString(), "deleteNotification()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		message.put("success", result);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> updateRead(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("NotificationServiceImpl-->updateRead():jo=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();

		long nid = JsonUtil.getLongValue(jo, "nid");
		boolean read = JsonUtil.getBooleanValue(jo, "read", false);
		if(nid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			
		NotificationBean notificationBean = notificationMapper.findById(NotificationBean.class, nid);
		if(notificationBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除的通知不存在.value));
		
		checkAdmin(user, notificationBean.getToUserId());
		
		boolean result = false;
		notificationBean.setRead(read);
		result = notificationMapper.update(notificationBean) > 0;
		if(result){
			notificationHandler.deleteNoReadMessagesNumber(user.getId());
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.修改成功.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.修改失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改通知Id为：", nid , "的状态已读为：" , read, StringUtil.getSuccessOrNoStr(result)).toString(), "updateRead()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		message.put("success", result);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> updateAllRead(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("NotificationServiceImpl-->updateAllRead():jo=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		String type = JsonUtil.getStringValue(jo, "type");
		boolean read = JsonUtil.getBooleanValue(jo, "read", false);
		if(StringUtil.isNull(type)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
			
		boolean result = notificationMapper.updateAllRead(type, read) > 0;
		if(result){
			notificationHandler.deleteNoReadMessagesNumber(user.getId());
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
		message.put("responseCode", EnumUtil.ResponseCode.修改成功.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改通知类型为：", type , "的状态全部已读为：" , read, StringUtil.getSuccessOrNoStr(result)).toString(), "updateAllRead()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		message.put("success", result);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> noReadNumber(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("NotificationServiceImpl-->noReadNumber():jo=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		KeyValuesBean keyValues = notificationHandler.getNoReadMessagesNumber(user.getId()); 
		List<KeyValueBean> list = new ArrayList<KeyValueBean>();
		list.addAll(keyValues.getData());
		int total = 0;
		if(keyValues != null && CollectionUtil.isNotEmpty(keyValues.getData())){
			for(KeyValueBean keyValueBean: keyValues.getData()){
				total += StringUtil.changeObjectToInt(keyValueBean.getValue());
			}
		}
		KeyValueBean kv = new KeyValueBean();
		kv.setKey("全部");
		kv.setValue(StringUtil.changeNotNull(total));
		list.add(kv);
		message.put("message", list);
		message.put("responseCode", EnumUtil.ResponseCode.修改成功.value);
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取未读的消息列表").toString(), "noReadNumber()", 1, 0);
		message.put("success", true);
		return message.getMap();
	}
	
	@Override
	public boolean update(NotificationBean t) {
		return notificationMapper.update(t) > 0;
	}
}
