package com.cn.leedane.service.impl;

import com.cn.leedane.handler.LinkManageHandler;
import com.cn.leedane.handler.RoleHandler;
import com.cn.leedane.handler.RolePermissionHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.RoleMapper;
import com.cn.leedane.mapper.UserRoleMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.RoleService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * 用户角色service实现类
 * @author LeeDane
 * 2016年7月12日 下午2:17:51
 * Version 1.0
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService<RoleBean> {
private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Autowired
	private UserHandler userHandler;

	@Autowired
	private RoleHandler roleHandler;
	
	@Autowired
	private RolePermissionHandler rolePermissionHandler;
	
	@Autowired
	private LinkManageHandler linkManageHandler;

	@Override
	public Map<String, Object> save(JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("RoleServiceImpl-->save():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		RoleBean roleBean = new RoleBean();
		roleBean.setCode(JsonUtil.getStringValue(jsonObject, "code"));
		roleBean.setCreateTime(new Date());
		roleBean.setCreateUserId(user.getId());
		roleBean.setDesc(JsonUtil.getStringValue(jsonObject, "desc"));
		roleBean.setName(JsonUtil.getStringValue(jsonObject, "name"));
		roleBean.setOrder(JsonUtil.getIntValue(jsonObject, "order", 1));
		roleBean.setStatus(JsonUtil.getIntValue(jsonObject, "status", ConstantsUtil.STATUS_NORMAL));
		boolean result = roleMapper.save(roleBean) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		//清空缓存的全部链接
		linkManageHandler.deleteAllLinkManagesCache();
		return message.getMap();
	}

	@Override
	public Map<String, Object> edit(JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("RoleServiceImpl-->edit():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		RoleBean roleBean = new RoleBean();
		roleBean.setCode(JsonUtil.getStringValue(jsonObject, "code"));
		roleBean.setCreateTime(new Date());
		roleBean.setCreateUserId(user.getId());
		roleBean.setDesc(JsonUtil.getStringValue(jsonObject, "desc"));
		roleBean.setName(JsonUtil.getStringValue(jsonObject, "name"));
		roleBean.setOrder(JsonUtil.getIntValue(jsonObject, "order", 1));
		roleBean.setId(JsonUtil.getIntValue(jsonObject, "id", 0));
		roleBean.setStatus(JsonUtil.getIntValue(jsonObject, "status", ConstantsUtil.STATUS_NORMAL));
		boolean result = roleMapper.update(roleBean) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		//清空缓存的全部链接
		linkManageHandler.deleteAllLinkManagesCache();
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(int rlid, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("RoleServiceImpl-->delete():rlid="+ rlid);
		ResponseMap message = new ResponseMap();
				
		//删除权限之间的关系
		List<Map<String, Object>> uids = userRoleMapper.getUsersByRoleId(rlid);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				//清空权限相关的缓存
				rolePermissionHandler.deleteByUser(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		userRoleMapper.deleteByField(UserRoleBean.class, "role_id", rlid);
		
		boolean result = roleMapper.deleteById(RoleBean.class, rlid) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("RoleServiceImpl-->paging():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jsonObject, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jsonObject, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jsonObject, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		
		sql.append("select r.id, r.role_desc, r.role_name, r.role_code, r.role_order, date_format(r.create_time,'%Y-%m-%d %H:%i:%s') create_time , r.create_user_id, r.status");
		sql.append(", (select group_concat(u.account) from "+ DataTableType.用户角色.value +" ur INNER JOIN "+ DataTableType.用户.value +" u on ur.user_id = u.id where ur.role_id = r.id) users");
		sql.append(" from "+DataTableType.角色.value+" r ");
		sql.append(" order by r.role_order desc,r.id desc limit ?,?");
		rs = roleMapper.executeSQL(sql.toString(), start, pageSize);

		if(CollectionUtil.isNotEmpty(rs)){
			int createUserId = 0;			
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}
		}
		message.put("total", SqlUtil.getTotalByList(roleMapper.getTotal(DataTableType.角色.value, null)));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取角色列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> deletes(String rlids, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("RoleServiceImpl-->deletes():rlids="+ rlids);
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(rlids))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
		
		String[] rlidArray = rlids.split(",");
		int[] ids = new int[rlidArray.length];
		
		StringBuffer sql = new StringBuffer("delete from " + DataTableType.用户角色.value + " where ");
		for(int i = 0; i < rlidArray.length; i++){
			ids[i] = StringUtil.changeObjectToInt(rlidArray[i]);
			if(i == rlidArray.length - 1)
				sql.append("role_id = "+ ids[i]);
			else
				sql.append("role_id = "+ ids[i] +" or ");
		}
		//清空用户的角色权限缓存redis
		List<Map<String, Object>> uids = userRoleMapper.getUsersByRoleIds(ids);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				//清空权限相关的缓存
				rolePermissionHandler.deleteByUser(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		 userRoleMapper.deleteByField(UserRoleBean.class, "role_id", rlidArray);
		 
		 boolean result = roleMapper.deleteByIds(RoleBean.class, ids) == rlidArray.length;
			message.put("isSuccess", result);
			if(result){
				//清空用户的角色redis
				for(String roleId: rlidArray)
					roleHandler.deleteRoleUsersCache(StringUtil.changeObjectToInt(roleId));
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			}
		
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> users(int rlid, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("RoleServiceImpl-->users():rlid="+rlid);
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> rs = roleMapper.users(rlid, ConstantsUtil.STATUS_NORMAL);

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取用户列表").toString(), "users()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> allot(int rlid, String users, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("RoleServiceImpl-->allot():rlid="+rlid +", users="+ users);
		
		ResponseMap message = new ResponseMap();
		
		//根据角色id去删除所有相关的用户
		String[] userArray = users.split(",");
		int[] userIds = new int[userArray.length];
		for(int i = 0; i < userArray.length; i++){
			userIds[i] = StringUtil.changeObjectToInt(userArray[i]);
		}

		//获取角色的所有用户ID
		Set<Integer> clearIds = new HashSet<Integer>();
		List<Map<String, Object>> uids = userRoleMapper.getUsersByRoleId(rlid);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				clearIds.add(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		userRoleMapper.deleteByField(UserRoleBean.class, "role_id", rlid);
		
		Date createTime = DateUtil.getCurrentTime();
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		for(int userId: userIds){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("user_id", userId);
			map.put("role_id", rlid);
			map.put("create_user_id", user.getId());
			map.put("create_time", new Timestamp(createTime.getTime()));
			data.add(map);
		}
		
		if(StringUtil.isNotNull(users)){
			userRoleMapper.insertByBatch(data);
		}
		
		//再次获取角色的所有用户ID
		List<Map<String, Object>> uidsAfter = userRoleMapper.getUsersByRoleId(rlid);
		if(CollectionUtil.isNotEmpty(uidsAfter)){
			for(Map<String, Object> uid: uidsAfter)
				clearIds.add(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
			
		//清空角色权限相关的缓存
		if(clearIds.size() > 0){
			logger.info("将清空以下用户的角色权限相关的缓存----->"+StringUtils.join(clearIds.toArray(), ","));
			for(Integer clearId: clearIds){
				roleHandler.deleteRoleUsersCache(clearId);
				rolePermissionHandler.deleteByUser(clearId);
			}
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"给角色ID为"+ rlid +",分配用户ids"+users).toString(), "allot()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		message.put("message", "操作成功");
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}
}
