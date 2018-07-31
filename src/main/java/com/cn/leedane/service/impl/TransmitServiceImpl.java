package com.cn.leedane.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.TransmitHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.TransmitMapper;
import com.cn.leedane.model.FriendBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.TransmitBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.FriendService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.TransmitService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.FilterUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
/**
 * 转发service的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:12:45
 * Version 1.0
 */
@Service("transmitService")
public class TransmitServiceImpl extends AdminRoleCheckService implements TransmitService<TransmitBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private TransmitMapper transmitMapper;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private FriendHandler friendHandler;
	
	@Autowired
	private CommonHandler commonHandler;
	
	@Autowired
	private TransmitHandler transmitHandler;
	
	@Autowired
	private FriendService<FriendBean> friendService;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user, HttpServletRequest request){
		//{\"table_name\":\"t_mood\", \"table_id\":1, 'content':'转发信息'}
		logger.info("TransmitServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0);
		String content = JsonUtil.getStringValue(jo, "content");
		ResponseMap message = new ResponseMap();
		boolean result = false;
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message))
			return message.getMap();
		
		if(StringUtil.isNull(tableName) || tableId < 1 || StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		
		//转发权限校验
		if(!checkTransmit(tableName, tableId, message)){
			return message;
		}

		TransmitBean bean = new TransmitBean();
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setTableName(tableName);
		bean.setTableId(tableId);
		bean.setContent(content);
		bean.setFroms(JsonUtil.getStringValue(jo, "froms"));
		if(transmitMapper.save(bean) > 0){
			result = true;
			int createUserId = 0;
			//String str = "{from_user_remark}转发："+content;
			createUserId = SqlUtil.getCreateUserIdByList(transmitMapper.getObjectCreateUserId(tableName, tableId));
			if(createUserId > 0 && createUserId != user.getId()){
				Set<Integer> ids = new HashSet<Integer>();
				ids.add(createUserId);
				notificationHandler.sendNotificationByIds(false, user, ids, content, NotificationType.转发, tableName, tableId, bean);
			}
			
			//有@人通知相关人员
			Set<String> usernames = StringUtil.getAtUserName(content);
			if(usernames.size() > 0){
				//str = "{from_user_remark}在转发时候@您,点击查看详情";
				notificationHandler.sendNotificationByNames(false, user, usernames, content, NotificationType.艾特我, tableName, tableId, bean);
			}
			
		}
		transmitHandler.addTransmit(tableName, tableId);
		if(result){
			message.put("isSuccess", result);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.转发成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.转发失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.转发失败.value);
		}
		return message.getMap();
	}
	
	/**
	 * 检验转发是否可以进行下去
	 * @param tableName
	 * @param tableId
	 * @param message
	 */
	private boolean checkTransmit(String tableName, int tableId, Map<String, Object> message) {
		
		List<Map<String, Object>> list = transmitMapper.executeSQL("select id, can_transmit from "+tableName +" where id = ? limit 0,1", tableId);
		//检查该实体数据是否数据存在,防止对不存在的对象添加转发
		if(list == null || list.size() != 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作实例.value);
			return false;
		}
		
		boolean canTransmit = StringUtil.changeObjectToBoolean(list.get(0).get("can_transmit"));
		if(!canTransmit){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.该资源现在不支持转发.value));
			message.put("responseCode", EnumUtil.ResponseCode.该资源现在不支持转发.value);
		}
		return canTransmit;
	}
	
	@Override
	public Map<String, Object> deleteTransmit(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("TransmitServiceImpl-->deleteTransmit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int tid = JsonUtil.getIntValue(jo, "tid");
		int createUserId = JsonUtil.getIntValue(jo, "create_user_id");
		
		ResponseMap message = new ResponseMap();
		
		if(tid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		boolean result = false;
		TransmitBean transmitBean = transmitMapper.findById(TransmitBean.class, tid);
		if(transmitBean != null && transmitBean.getCreateUserId() == createUserId){
			result = transmitMapper.deleteById(TransmitBean.class, tid) > 0;
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作对象不存在.value);
		}
		if(result){
			transmitHandler.deleteTransmit(transmitBean.getTableId(), transmitBean.getTableName());
			message.put("isSuccess", result);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除转发成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除转发失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除转发失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除转发ID为", tid, "的数据", StringUtil.getSuccessOrNoStr(result)).toString(), "deleteTransmit()", StringUtil.changeBooleanToInt(result), 0);
		return message.getMap();
	}

	@Override
	public List<Map<String, Object>> getLimit(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		if(user == null)
			user = new UserBean();
		logger.info("TransmitServiceImpl-->getLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int toUserId = JsonUtil.getIntValue(jo, "toUserId"); //操作的对象用户的id，如获取指定心情的转发数，这个为0
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0);
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		boolean showUserInfo = JsonUtil.getBooleanValue(jo, "showUserInfo");
		/*if(userId < 1)
			return new ArrayList<Map<String,Object>>();*/
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();

		//查找该用户所有的转发
		if(StringUtil.isNull(tableName) && toUserId > 0 && tableId < 1){		
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select t.id, t.froms, t.content, t.table_name, t.table_id, t.create_user_id, u.account, date_format(t.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.转发.value+" t inner join "+DataTableType.用户.value+" u on u.id = t.create_user_id where t.create_user_id = ? and t.status = ? ");
				sql.append(" order by t.id desc limit 0,?");
				rs = transmitMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select t.id, t.froms, t.content, t.table_name, t.table_id, t.create_user_id, u.account, date_format(t.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.转发.value+" t inner join "+DataTableType.用户.value+" u on u.id = t.create_user_id where t.create_user_id = ? and t.status = ? ");
				sql.append(" and t.id < ? order by t.id desc limit 0,? ");
				rs = transmitMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select t.id, t.froms, t.content, t.table_name, t.table_id, t.create_user_id, u.account, date_format(t.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.转发.value+" t inner join "+DataTableType.用户.value+" u on u.id = t.create_user_id where t.create_user_id = ? and t.status = ? ");
				sql.append(" and t.id > ? limit 0,?  ");
				rs = transmitMapper.executeSQL(sql.toString() , toUserId, ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
			}
		}
		
		//查找指定表的数据
		if(StringUtil.isNotNull(tableName) && toUserId < 1 && tableId > 0){
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select t.id, t.froms, t.content, t.table_name, t.table_id, t.create_user_id, u.account, date_format(t.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.转发.value+" t inner join "+DataTableType.用户.value+" u on u.id = t.create_user_id where  t.status = ? and t.table_name = ? and t.table_id = ? ");
				sql.append(" order by t.id desc limit 0,?");
				rs = transmitMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableId, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select t.id, t.froms, t.content, t.table_name, t.table_id, t.create_user_id, u.account, date_format(t.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.转发.value+" t inner join "+DataTableType.用户.value+" u on u.id = t.create_user_id where t.status = ? and t.table_name = ? and t.table_id = ?");
				sql.append(" and t.id < ? order by t.id desc limit 0,? ");
				rs = transmitMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableId, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select t.id, t.froms, t.content, t.table_name, t.table_id, t.create_user_id, u.account, date_format(t.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.转发.value+" t inner join "+DataTableType.用户.value+" u on u.id = t.create_user_id where t.status = ? and t.table_name = ? and t.table_id = ?");
				sql.append(" and t.id > ? limit 0,?  ");
				rs = transmitMapper.executeSQL(sql.toString() , ConstantsUtil.STATUS_NORMAL, tableName, tableId, firstId, pageSize);
			}
		}
		if(rs.size() >0){
			int createUserId = 0;
			JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			String tabName;
			int tabId;
			for(int i=0; i < rs.size(); i++){
				if(StringUtil.isNull(tableName) && tableId <1){
					//在非获取指定表下的转发列表的情况下的前面35个字符
					tabName = StringUtil.changeNotNull((rs.get(i).get("table_name")));
					tabId = StringUtil.changeObjectToInt(rs.get(i).get("table_id"));
					rs.get(i).put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
				}
				if(showUserInfo){
					createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId, user, friendObject));
				}
			}
		}
		
		//保存操作日志
		//operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取用户ID为：",toUserId,",表名：",tableName,"，表id为：",tableId,"的转发列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		return rs;
	}

	@Override
	public int getTotalTransmits(int userId) {
		return SqlUtil.getTotalByList(transmitMapper.getTotalByUser(DataTableType.转发.value, userId));
	}
	
	@Override
	public Map<String, Object> updateTransmitStatus(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("TransmitServiceImpl-->updateTransmitStatus():jo="+jo.toString());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id");
		boolean canTransmit = JsonUtil.getBooleanValue(jo, "can_transmit", true);
		ResponseMap message = new ResponseMap();
		
		int createUserId = SqlUtil.getCreateUserIdByList(transmitMapper.getObjectCreateUserId(tableName, tableId));
		
		checkAdmin(user, createUserId);
		
		boolean result = transmitMapper.updateSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(tableName)), " set can_transmit=? where id=?", canTransmit, tableId) > 0;
		
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.更新转发状态成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.更新转发状态失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.更新转发状态失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"更新表名为：", tableName ,",表ID为：", tableId, "转发状态为", canTransmit, "，结果更新", StringUtil.getSuccessOrNoStr(result)).toString(), "updateTransmitStatus()", StringUtil.changeBooleanToInt(result), 0);
		message.put("isSuccess", result);
		return message.getMap();
	}

	@Override
	public int getTotal(String tableName, String where) {
		return SqlUtil.getTotalByList(transmitMapper.getTotal(tableName, where));
	}
}
