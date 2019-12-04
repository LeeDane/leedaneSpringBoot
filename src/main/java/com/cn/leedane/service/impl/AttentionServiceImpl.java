package com.cn.leedane.service.impl;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.AttentionMapper;
import com.cn.leedane.model.AttentionBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.AttentionService;
import com.cn.leedane.service.OperateLogService;
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
 * 关注service的实现类
 * @author LeeDane
 * 2016年7月12日 下午12:20:42
 * Version 1.0
 */
@Service("attentionService")
public class AttentionServiceImpl extends AdminRoleCheckService implements AttentionService<AttentionBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private AttentionMapper attentionMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private FriendHandler friendHandler;
	
	@Autowired
	private CommonHandler commonHandler;
	

	@Override
	public Map<String, Object> addAttention(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		//{\"table_name\":\""+DataTableType.心情.value+"\", \"table_id\":2334}
		logger.info("AttentionServiceImpl-->addAttention():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		long tableId = JsonUtil.getLongValue(jo, "table_id");
		
		ResponseMap message = new ResponseMap();
		
		if(SqlUtil.getBooleanByList(attentionMapper.exists(AttentionBean.class, tableName, tableId, user.getId()))){
			message.put("message", "您已关注，请勿重复关注！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();
		}
		
		if(!SqlUtil.getBooleanByList(attentionMapper.recordExists(tableName, tableId)))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			
		AttentionBean bean = new AttentionBean();
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setTableName(tableName);
		bean.setTableId(tableId);
		boolean result = attentionMapper.save(bean) > 0;
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"关注", userHandler.getUserName(bean.getId()), StringUtil.getSuccessOrNoStr(result)).toString(), "addAttention()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.关注成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		message.put("isSuccess", result);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> deleteAttention(JSONObject jo, UserBean user,
											   HttpRequestInfoBean request) {
		logger.info("AttentionServiceImpl-->deleteAttention():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		long aid = JsonUtil.getLongValue(jo, "aid");
		//int createUserId = JsonUtil.getIntValue(jo, "create_user_id");
		
		ResponseMap message = new ResponseMap();		
		if(aid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		AttentionBean attentionBean = attentionMapper.findById(AttentionBean.class, aid);
		
		//非登录用户不能删除操作
		checkAdmin(user, attentionBean.getCreateUserId());
		
		boolean result = attentionMapper.deleteById(AttentionBean.class, attentionBean.getId()) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}	
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"解除关注ID为", aid, StringUtil.getSuccessOrNoStr(result)).toString(), "deleteAttention()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public List<Map<String, Object>> getLimit(JSONObject jo, UserBean user,
											  HttpRequestInfoBean request) {
		logger.info("AttentionServiceImpl-->getLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		long toUserId = JsonUtil.getLongValue(jo, "toUserId");
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		long lastId = JsonUtil.getLongValue(jo, "last_id"); //开始的页数
		long firstId = JsonUtil.getLongValue(jo, "first_id"); //结束的页数
		boolean showUserInfo = JsonUtil.getBooleanValue(jo, "showUserInfo");
		
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		//查找该用户所有的关注(该用户必须是登录用户)
		if(toUserId > 0 && toUserId == user.getId()){		
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select a.id, a.table_name, a.table_id, a.create_user_id, u.account, date_format(a.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.关注.value+" a inner join "+DataTableType.用户.value+" u on u.id = a.create_user_id where a.create_user_id = ? and a.status = ? ");
				sql.append(" order by a.id desc limit 0,?");
				rs = attentionMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select a.id, a.table_name, a.table_id, a.create_user_id, u.account, date_format(a.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.关注.value+" a inner join "+DataTableType.用户.value+" u on u.id = a.create_user_id where a.create_user_id = ? and a.status = ?");
				sql.append(" and a.id < ? order by a.id desc limit 0,? ");
				rs = attentionMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select a.id, a.table_name, a.table_id, a.create_user_id, u.account, date_format(a.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.关注.value+" a inner join "+DataTableType.用户.value+" u on u.id = a.create_user_id where a.create_user_id = ? and a.status = ? ");
				sql.append(" and a.id > ? limit 0,?  ");
				rs = attentionMapper.executeSQL(sql.toString() , toUserId, ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
			}
		}
		
		//查找该用户指定表的数据
		/*if(StringUtil.isNotNull(tableName) && toUserId < 1 && tableId > 0){
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select a.id, a.table_name, a.table_id, a.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.关注.value+" a inner join "+DataTableType.用户.value+" u on u.id = a.create_user_id where  a.status = ? and a.table_name = ? and a.table_id = ?");
				sql.append(" order by a.id desc limit 0,?");
				rs = attentionDao.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableId, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select a.id, a.table_name, a.table_id, a.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.关注.value+" a inner join "+DataTableType.用户.value+" u on u.id = a.create_user_id where a.status = ? and a.table_name = ? and a.table_id = ?");
				sql.append(" and a.id < ? order by a.id desc limit 0,? ");
				rs = attentionDao.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableId, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select a.id, a.table_name, a.table_id, a.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.关注.value+" a inner join "+DataTableType.用户.value+" u on u.id = a.create_user_id where a.status = ? and a.table_name = ? and a.table_id = ?");
				sql.append(" and a.id > ? limit 0,?  ");
				rs = attentionDao.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableId, firstId, pageSize);
			}
		}*/
		
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			
			//String account = "";
			String tabName;
			int tabId;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				//在非获取指定表下的评论列表的情况下的前面35个字符
				tabName = StringUtil.changeNotNull((rs.get(i).get("table_name")));
				tabId = StringUtil.changeObjectToInt(rs.get(i).get("table_id"));
				rs.get(i).put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
				if(showUserInfo){
					createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId, user, friendObject));
				}
			}	
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取用户ID为：",toUserId,"的关注列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return rs;
	}
	
}
