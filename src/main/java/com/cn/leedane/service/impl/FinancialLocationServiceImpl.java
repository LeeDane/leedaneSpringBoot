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
import com.cn.leedane.mapper.FinancialLocationMapper;
import com.cn.leedane.model.FinancialLocationBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.FinancialLocationService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
/**
 * 记账位置service的实现类
 * @author LeeDane
 * 2016年11月22日 下午2:54:40
 * Version 1.0
 */
@Service("financialLocationService")
public class FinancialLocationServiceImpl extends AdminRoleCheckService implements FinancialLocationService<FinancialLocationBean>{
	Logger logger = Logger.getLogger(getClass());
	
	public static final String tableName = "T_FINANCIAL_LOCATION";
	
	@Autowired
	private FinancialLocationMapper financialLocationMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpServletRequest request){
		logger.info("FinancialLocationServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String location = JsonUtil.getStringValue(jo, "location");
		String locationDesc = JsonUtil.getStringValue(jo, "locationDesc");
		int status = JsonUtil.getIntValue(jo, "status", ConstantsUtil.STATUS_DISABLE);
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(location))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.记账位置信息为空.value));
			
		if(exists(location, user)){
			message.put("message", "您已添加过该记账位置记录，请勿重复操作！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();
		}
		FinancialLocationBean bean = new FinancialLocationBean();
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setStatus(status);
		bean.setLocation(location);
		if(StringUtil.isNotNull(locationDesc))
			bean.setLocationDesc(locationDesc);
		boolean result = financialLocationMapper.save(bean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.添加成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.添加失败.value));
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"用户ID为：",user.getId() , "添加位置信息为：", location, StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);
					
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(JSONObject jo, UserBean user,
			HttpServletRequest request){
		logger.info("FinancialLocationServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int flid = JsonUtil.getIntValue(jo, "flid"); //记录的ID
		String location = JsonUtil.getStringValue(jo, "location");
		String locationDesc = JsonUtil.getStringValue(jo, "locationDesc");
		int status = JsonUtil.getIntValue(jo, "status", ConstantsUtil.STATUS_DISABLE);
		
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(location) || flid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();
		}
		
		FinancialLocationBean bean = financialLocationMapper.findById(FinancialLocationBean.class, flid);
		
		checkAdmin(user, bean.getCreateUserId());
		
		bean.setStatus(status);
		bean.setLocation(location);
		if(StringUtil.isNotNull(locationDesc))
			bean.setLocationDesc(locationDesc);
		boolean result = financialLocationMapper.update(bean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改失败.value));
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"用户ID为：",user.getId() , "修改位置信息为：", location, StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);

		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("FinancialLocationServiceImpl-->paging():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		ResponseMap message = new ResponseMap();
		
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select f.id, f.status, f.location, f.location_desc, f.create_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+tableName+" f where f.create_user_id = ?");
			sql.append(" order by f.id desc limit 0,?");
			rs = financialLocationMapper.executeSQL(sql.toString(), user.getId(), pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select f.id, f.status, f.location, f.location_desc, f.create_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+tableName+" f where f.create_user_id = ?");
			sql.append(" and f.id < ? order by f.id desc limit 0,? ");
			rs = financialLocationMapper.executeSQL(sql.toString(), user.getId(), lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select f.id, f.status, f.location, f.location_desc, f.create_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+tableName+" f where f.create_user_id = ? ");
			sql.append(" and f.id > ? limit 0,?  ");
			rs = financialLocationMapper.executeSQL(sql.toString() , user.getId(), firstId, pageSize);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"用户ID为：",user.getId() , "获取记账位置列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
				
		message.put("isSuccess", true);
		message.put("message", rs);
		System.out.println("获得记账位置的数量：" +rs.size());
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("FinancialLocationServiceImpl-->delete():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int flid = JsonUtil.getIntValue(jo, "flid");
		ResponseMap message = new ResponseMap();
		
		if(flid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();
		}
		
		boolean result = false;
		FinancialLocationBean bean = financialLocationMapper.findById(FinancialLocationBean.class, flid);
		
		checkAdmin(user, bean.getCreateUserId());
		
		result = financialLocationMapper.deleteById(FinancialLocationBean.class, flid) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"用户ID为：",user.getId() , "删除位置信息ID为：", flid, StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, 0);

		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getAll(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("FinancialLocationServiceImpl-->getAll():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		ResponseMap message = new ResponseMap();
		
		sql.append("select f.id, f.status, f.location, f.location_desc, f.create_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
		sql.append(" from "+tableName+" f where f.create_user_id = ?");
		sql.append(" order by f.id desc");
		rs = financialLocationMapper.executeSQL(sql.toString(), user.getId());
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"用户ID为：",user.getId() , "获取所有记账位置列表").toString(), "getAll()", ConstantsUtil.STATUS_NORMAL, 0);
				
		message.put("isSuccess", true);
		message.put("message", rs);
		System.out.println("获得记账位置的数量：" +rs.size());
		return message.getMap();
	}
	
	/**
	 * 判断记录是否存在
	 * @param location
	 * @param user
	 * @return
	 */
	private boolean exists(String location, UserBean user){
		return SqlUtil.getBooleanByList(financialLocationMapper.executeSQL("select id from "+ tableName +" where create_user_id = ? and location = ?", user.getId(), location));
	}
}
