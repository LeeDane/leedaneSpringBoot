package com.cn.leedane.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.NotificationMapper;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.NotificationService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;
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
			HttpServletRequest request) {
		logger.info("NotificationServiceImpl-->getLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String type = JsonUtil.getStringValue(jo, "type"); //通知类型
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		
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
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取收到的通知列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> sendBroadcast(JSONObject jo, UserBean user,
			HttpServletRequest request) {
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
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"向所有在线用户发送通知", StringUtil.getSuccessOrNoStr(result)).toString(), "sendBroadcast()", StringUtil.changeBooleanToInt(result), 0);
		message.put("isSuccess", result);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> deleteNotification(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("NotificationServiceImpl-->deleteNotification():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		int nid = JsonUtil.getIntValue(jo, "nid");
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
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"刪除通知Id为：", nid , StringUtil.getSuccessOrNoStr(result)).toString(), "deleteNotification()", StringUtil.changeBooleanToInt(result), 0);
		message.put("isSuccess", result);
		return message.getMap();
	}

	@Override
	public boolean update(NotificationBean t) {
		return notificationMapper.update(t) > 0;
	}
}
