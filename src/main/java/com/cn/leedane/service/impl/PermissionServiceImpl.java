package com.cn.leedane.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.handler.RolePermissionHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.PermissionMapper;
import com.cn.leedane.mapper.RolePermissionMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.PermissionBean;
import com.cn.leedane.model.RolePermissionBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.PermissionService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 权限管理service实现类
 * @author LeeDane
 * 2017年4月17日 下午2:25:11
 * version 1.0
 */
@Service("permissionService")
public class PermissionServiceImpl implements PermissionService<PermissionBean> {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private PermissionMapper permissionMapper;
	
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private RolePermissionHandler rolePermissionHandler;
	
	@Override
	public Map<String, Object> save(JSONObject jsonObject, UserBean user,
			HttpServletRequest request) {
		logger.info("PermissionServiceImpl-->save():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		//commentService.getCommentsByLimit(jsonObject, user, request);
		PermissionBean permissionBean = new PermissionBean();
		permissionBean.setCode(JsonUtil.getStringValue(jsonObject, "code"));
		permissionBean.setCreateTime(new Date());
		permissionBean.setCreateUserId(user.getId());
		permissionBean.setDesc(JsonUtil.getStringValue(jsonObject, "desc"));
		permissionBean.setName(JsonUtil.getStringValue(jsonObject, "name"));
		permissionBean.setOrder(JsonUtil.getIntValue(jsonObject, "order", 1));
		permissionBean.setStatus(JsonUtil.getIntValue(jsonObject, "status", ConstantsUtil.STATUS_NORMAL));
		boolean result = permissionMapper.save(permissionBean) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> edit(JSONObject jsonObject, UserBean user,
			HttpServletRequest request) {
		logger.info("PermissionServiceImpl-->edit():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		PermissionBean permissionBean = new PermissionBean();
		permissionBean.setCode(JsonUtil.getStringValue(jsonObject, "code"));
		permissionBean.setCreateTime(new Date());
		permissionBean.setCreateUserId(user.getId());
		permissionBean.setDesc(JsonUtil.getStringValue(jsonObject, "desc"));
		permissionBean.setName(JsonUtil.getStringValue(jsonObject, "name"));
		permissionBean.setOrder(JsonUtil.getIntValue(jsonObject, "order", 1));
		permissionBean.setId(JsonUtil.getIntValue(jsonObject, "id", 0));
		permissionBean.setStatus(JsonUtil.getIntValue(jsonObject, "status", ConstantsUtil.STATUS_NORMAL));
		boolean result = permissionMapper.update(permissionBean) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(int pmid, UserBean user,
			HttpServletRequest request) {
		logger.info("PermissionServiceImpl-->delete():pmid="+ pmid);
		ResponseMap message = new ResponseMap();
		
		//删除权限之间的关系
		List<Map<String, Object>> uids = rolePermissionMapper.getUsersByPermissionId(pmid);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				//清空权限相关的缓存
				rolePermissionHandler.deleteByUser(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		rolePermissionMapper.deleteByField(RolePermissionBean.class, "permission_id", pmid);
		
		boolean result = permissionMapper.deleteById(PermissionBean.class, pmid) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user,
			HttpServletRequest request) {
		logger.info("PermissionServiceImpl-->paging():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jsonObject, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jsonObject, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(jsonObject, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		
		sql.append("select p.id, p.permission_desc, p.permission_name, p.permission_code, p.permission_order, date_format(p.create_time,'%Y-%m-%d %H:%i:%s') create_time , p.create_user_id, p.status");
		sql.append(", (select group_concat(r.role_code) from t_role_permission rp INNER JOIN t_role r on rp.role_id = r.id where rp.permission_id = p.id) roles");
		sql.append(" from "+DataTableType.权限.value+" p ");
		sql.append(" order by p.permission_order desc,p.id desc limit ?,?");
		rs = permissionMapper.executeSQL(sql.toString(), start, pageSize);

		if(CollectionUtil.isNotEmpty(rs)){
			int createUserId = 0;			
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}
			message.put("total", SqlUtil.getTotalByList(permissionMapper.getTotal(DataTableType.权限.value, null)));
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取权限列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public Map<String, Object> deletes(String pmids, UserBean user,
			HttpServletRequest request) {
		logger.info("PermissionServiceImpl-->deletes():pmids="+ pmids);
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(pmids))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
		
		String[] pmidArray = pmids.split(",");
		int[] ids = new int[pmidArray.length];
		
		StringBuffer sql = new StringBuffer("delete from " + DataTableType.角色权限.value + " where ");
		for(int i = 0; i < pmidArray.length; i++){
			ids[i] = StringUtil.changeObjectToInt(pmidArray[i]);
			if(i == pmidArray.length - 1)
				sql.append("permission_id = "+ ids[i]);
			else
				sql.append("permission_id = "+ ids[i] +" or ");
		}
		
		//清空用户的权限redis
		List<Map<String, Object>> uids = rolePermissionMapper.getUsersByPermissionIds(ids);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				//清空权限相关的缓存
				rolePermissionHandler.deleteByUser(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		rolePermissionMapper.deleteByField(RolePermissionBean.class, "permission_id", pmidArray);
		 
	 	boolean result = permissionMapper.deleteByIds(PermissionBean.class, ids) == pmidArray.length;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> roles(int pmid, UserBean user,
			HttpServletRequest request) {
		logger.info("PermissionServiceImpl-->roles():pmid="+pmid);
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> rs = permissionMapper.roles(pmid, ConstantsUtil.STATUS_NORMAL);
				
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取角色列表").toString(), "roles()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> allot(int pmid, String roles, UserBean user,
			HttpServletRequest request) {
		logger.info("PermissionServiceImpl-->allot():pmid="+pmid +", roles="+ roles);
		
		ResponseMap message = new ResponseMap();
		
		//根据权限id去删除所有相关的角色
		String[] roleArray = roles.split(",");
		int[] roleIds = new int[roleArray.length];
		for(int i = 0; i < roleArray.length; i++){
			roleIds[i] = StringUtil.changeObjectToInt(roleArray[i]);
		}
		
		//获取权限的所有用户ID
		Set<Integer> clearIds = new HashSet<Integer>();
		List<Map<String, Object>> uids = rolePermissionMapper.getUsersByPermissionId(pmid);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				clearIds.add(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		rolePermissionMapper.deleteByField(RolePermissionBean.class, "permission_id", pmid);
		
		//RolePermissionBean rolePermissionBean;
		Date createTime = DateUtil.getCurrentTime();
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		for(int roleId: roleIds){
			Map<String, Object> map = new HashMap<String, Object>();
			//rolePermissionBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			//rolePermissionBean.setCreateTime(createTime);
			//rolePermissionBean.setCreateUserId(user.getId());
			//rolePermissionBean.se
			map.put("role_id", roleId);
			map.put("permission_id", pmid);
			map.put("create_user_id", user.getId());
			map.put("create_time", createTime);
			data.add(map);
		}
		
		//删除所有的
		
		if(StringUtil.isNotNull(roles)){
			rolePermissionMapper.insertByBatch(data);
		}
		
		//再次获取权限的所有用户ID
		List<Map<String, Object>> uidsAfter = rolePermissionMapper.getUsersByPermissionId(pmid);
		if(CollectionUtil.isNotEmpty(uidsAfter)){
			for(Map<String, Object> uid: uidsAfter)
				clearIds.add(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		
		//清空角色权限相关的缓存
		if(clearIds.size() > 0){
			for(Integer clearId: clearIds)
				rolePermissionHandler.deleteByUser(clearId);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"给权限ID为"+ pmid +",分配角色ids"+roles).toString(), "allot()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", "操作成功");
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}
}
