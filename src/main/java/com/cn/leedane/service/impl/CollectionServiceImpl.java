package com.cn.leedane.service.impl;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.CollectionMapper;
import com.cn.leedane.model.CollectionBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.CollectionService;
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
 * 收藏夹service的实现类
 * @author LeeDane
 * 2016年7月12日 下午1:25:34
 * Version 1.0
 */
@Service("collectionService")
public class CollectionServiceImpl extends AdminRoleCheckService implements CollectionService<CollectionBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CollectionMapper collectionMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private FriendHandler friendHandler;

	@Autowired
	private CommonHandler commonHandler;
	
	@Override
	public Map<String, Object> addCollect(JSONObject jo, UserBean user,
										  HttpRequestInfoBean request) {
		//{\"table_name\":\""+DataTableType.心情.value+"\", \"table_id\":123}
		logger.info("CollectionServiceImpl-->addCollect():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id");
		
		ResponseMap message = new ResponseMap();
		
		if(SqlUtil.getBooleanByList(collectionMapper.exists(CollectionBean.class, tableName, tableId, user.getId()))){
			message.put("message", "您已收藏，请勿重复操作！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();
		}
		CollectionBean bean = new CollectionBean();
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setTableName(tableName);
		bean.setTableId(tableId);
		boolean result = collectionMapper.save(bean) > 0;
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.收藏成功.value));
			message.put("isSuccess", result);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"收藏表ID为：", tableId, ",表名为：", tableName, "的记录", StringUtil.getSuccessOrNoStr(result)).toString(), "addCollect()", StringUtil.changeBooleanToInt(result), 0);		
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> deleteCollection(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CollectionServiceImpl-->deleteCollection():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int cid = JsonUtil.getIntValue(jo, "cid");
		//int createUserId = JsonUtil.getIntValue(jo, "create_user_id");
		ResponseMap message = new ResponseMap();
		
		if(cid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		CollectionBean collectionBean = collectionMapper.findById(CollectionBean.class, cid);
		//非登录用户不能删除操作
		checkAdmin(user, collectionBean.getCreateUserId());
		
		boolean result = collectionMapper.deleteById(CollectionBean.class, collectionBean.getId()) > 0;

		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除收藏ID为", cid, "的数据", StringUtil.getSuccessOrNoStr(result)).toString(), "deleteCollection()", StringUtil.changeBooleanToInt(result), 0);
		return message.getMap();
	}

	@Override
	public List<Map<String, Object>> getLimit(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CollectionServiceImpl-->getLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int toUserId = JsonUtil.getIntValue(jo, "toUserId");
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		boolean showUserInfo = JsonUtil.getBooleanValue(jo, "showUserInfo");
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		//查找该用户所有的收藏(该用户必须是登录用户)
		if(toUserId > 0 && toUserId == user.getId()){		
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.table_name, c.table_id, c.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.收藏.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id where c.create_user_id = ? and c.status = ? ");
				sql.append(" order by c.id desc limit 0,?");
				rs = collectionMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.table_name, c.table_id, c.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.收藏.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id where c.create_user_id = ? and c.status = ?");
				sql.append(" and c.id < ? order by c.id desc limit 0,? ");
				rs = collectionMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.table_name, c.table_id, c.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.收藏.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id where c.create_user_id = ? and c.status = ? ");
				sql.append(" and c.id > ? limit 0,?  ");
				rs = collectionMapper.executeSQL(sql.toString() , toUserId, ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
			}
		}/*
		
		//查找该用户指定表的数据
		if(StringUtil.isNotNull(tableName) && toUserId < 1 && tableId > 0){
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.table_name, c.table_id, c.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.收藏.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id where c.status = ? and c.table_name = ? and c.table_id =? ");
				sql.append(" order by c.id desc limit 0,?");
				rs = collectionDao.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableId, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.table_name, c.table_id, c.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.收藏.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id where c.status = ? and c.table_name = ? and c.table_id =? ");
				sql.append(" and c.id < ? order by c.id desc limit 0,? ");
				rs = collectionDao.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableId, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.table_name, c.table_id, c.create_user_id, u.account, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
				sql.append(" from "+DataTableType.收藏.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id where c.status = ? and c.table_name = ? and c.table_id =? ");
				sql.append(" and c.id > ? limit 0,?  ");
				rs = collectionDao.executeSQL(sql.toString() , ConstantsUtil.STATUS_NORMAL, tableName, tableId, firstId, pageSize);
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
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"读取用户：", userHandler.getUserName(toUserId), "的收藏列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return rs;
	}
	
}
