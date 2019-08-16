package com.cn.leedane.service.impl;

import com.cn.leedane.mapper.OperateLogMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.OperateLogSaveThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 操作日志service的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:02:58
 * Version 1.0
 */
@Service("operateLogService")
public class OperateLogServiceImpl implements OperateLogService<OperateLogBean>{
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private OperateLogMapper operateLogMapper;
	
	/*@Override
	public boolean save(OperateLogBean t) throws Exception {
		return operateLogDao.save(t);
	}*/

	@Override
	public boolean saveOperateLog(UserBean user, HttpRequestInfoBean request,
			Date createTime, String subject, String method, int status, int operateType){
		/*if(user == null)
			return false;*/

		//如果标题中还没有用户姓名
		if(user != null && !subject.contains(user.getAccount())){
			subject = user.getAccount() + subject;
		}

		if(null == user)
			status = ConstantsUtil.STATUS_SELF; //未登录的用户状态统一改成私有的
		//异步添加用户solr索引
		new ThreadUtil().singleTask(new OperateLogSaveThread(user, request, createTime, subject, method, status, operateType));
		return true;
	}

	@Override
	public Map<String, Object> getUserLoginLimit(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("OperateLogServiceImpl-->getUserLoginLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		StringBuffer sql = new StringBuffer();
		
		ResponseMap message = new ResponseMap();
		
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		//查找该用户所有的积分历史列表(该用户必须是登录用户)
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select o.id, o.method, o.browser, o.ip, o.status, date_format(o.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.操作日志.value+" o where o.create_user_id = ? and (method ='手机号码登录' or method='账号登录' or method='扫码登录')");
			sql.append(" order by o.id desc limit 0,?");
			rs = operateLogMapper.executeSQL(sql.toString(), user.getId(), pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select o.id, o.method, o.browser, o.ip, o.status, date_format(o.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.操作日志.value+" o where o.create_user_id = ? and (method ='手机号码登录' or method='账号登录' or method='扫码登录')");
			sql.append(" and o.id < ? order by o.id desc limit 0,? ");
			rs = operateLogMapper.executeSQL(sql.toString(), user.getId(), lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select o.id, o.method, o.browser, o.ip, o.status, date_format(o.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.操作日志.value+" o where o.create_user_id = ? and (method ='手机号码登录' or method='账号登录' or method='扫码登录')");
			sql.append(" and o.id > ? limit 0,?  ");
			rs = operateLogMapper.executeSQL(sql.toString() , user.getId(), firstId, pageSize);
		}
		//保存操作日志
//		saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取登录记录列表").toString(), "getUserLoginLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();		
	}

	@Override
	public int getAllReadNumber() {
		logger.info("OperateLogServiceImpl-->getAllReadNumber()");
		return SqlUtil.getTotalByList(operateLogMapper.getAllReadNumber(EnumUtil.LogOperateType.网页端.value));
	}
}
