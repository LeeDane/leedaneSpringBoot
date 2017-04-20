package com.cn.leedane.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.cn.leedane.mapper.ReportMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.ReportBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.ReportService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
/**
 * 举报service的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:06:17
 * Version 1.0
 */
@Service("reportService")
public class ReportServiceImpl extends AdminRoleCheckService implements ReportService<ReportBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ReportMapper reportMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private CommonHandler commonHandler;
	
	@Override
	public Map<String, Object> addReport(JSONObject jo, final UserBean user, HttpServletRequest request){
		//{\"table_name\":\"t_mood\", \"table_id\":2334, 'reason':'青色'}
		logger.info("ReportServiceImpl-->addReport():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		final String tableName = JsonUtil.getStringValue(jo, "table_name");
		final int tableId = JsonUtil.getIntValue(jo, "table_id");
		int type = JsonUtil.getIntValue(jo, "type", 0);
		String reason = JsonUtil.getStringValue(jo, "reason");
		final boolean anonymous = JsonUtil.getBooleanValue(jo, "anonymous", true); //是否匿名举报，默认是匿名
		ResponseMap message = new ResponseMap();
		
		if(SqlUtil.getBooleanByList(reportMapper.exists(ReportBean.class, tableName, tableId, user.getId()))){
			message.put("message", "您已经对此举报过，请稍待我们审核！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();
		}
		
		final int resourcesCreateUserId = SqlUtil.getCreateUserIdByList(reportMapper.getObjectCreateUserId(tableName, tableId));
		
		if(user.getId() == resourcesCreateUserId){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.不能举报自己发布的资源.value));
			message.put("responseCode", EnumUtil.ResponseCode.不能举报自己发布的资源.value);
			return message.getMap();
		}
		
		if(!SqlUtil.getBooleanByList(reportMapper.recordExists(tableName, tableId)))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			
		ReportBean bean = new ReportBean();
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setTableName(tableName);
		bean.setTableId(tableId);
		if(type == EnumUtil.ReportType.倾诉投诉.value){
			bean.setReason(reason);
		}else{
			bean.setReason(EnumUtil.getReportType(type));
		}
		bean.setReportType(type);
		boolean result = reportMapper.save(bean) > 0;
		message.put("isSuccess", result);
		if(!result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.举报成功.value));
			
			//通知相关人员有资源被举报
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					String content = "您发布的《"+commonHandler.getContentByTableNameAndId(tableName, tableId, user)+"》被用户"+ (anonymous ? "" : user.getAccount())+"举报";
					//Object beanObject = commonHandler.getBeanByTableNameAndId(tableName, tableId, user);
					Object beanObject = null;
					notificationHandler.sendNotificationById(false, user, resourcesCreateUserId, content , NotificationType.通知, tableName, tableId, beanObject);
					
				}
			}).start();
		}
		
		//保存操作日志
		String subject = user.getAccount() + "举报，表名："+tableName+",表ID:"+tableId+StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "addReport()", StringUtil.changeBooleanToInt(result) , 0);
		return message.getMap();
	}

	@Override
	public Map<String, Object> cancel(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ReportServiceImpl-->cancel():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id");
		
		ResponseMap message = new ResponseMap();
	
		int createUserId = SqlUtil.getCreateUserIdByList(reportMapper.getObjectCreateUserId(tableName, tableId));
		
		checkAdmin(user, createUserId);
		
		boolean result = reportMapper.deleteSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(DataTableType.举报.value)), " where table_id = ? and table_name = ? and create_user_id=?", tableId, tableName, user.getId()) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", "取消举报成功");
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据删除失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"取消举报，表名是"+tableName+",表ID为："+ tableId, "cancel()", StringUtil.changeBooleanToInt(result), 0);
		return message.getMap();
	}

	@Override
	public List<Map<String, Object>> getLimit(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ReportServiceImpl-->getLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int userId = JsonUtil.getIntValue(jo, "uid", user.getId());
//		String tableName = JsonUtil.getStringValue(jo, "table_name");
//		int tableId = JsonUtil.getIntValue(jo, "table_id", 0);
//		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
//		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
//		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
//		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		
		if(userId < 1)
			return new ArrayList<Map<String,Object>>();
//		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		
		return rs;
	}
	
}
