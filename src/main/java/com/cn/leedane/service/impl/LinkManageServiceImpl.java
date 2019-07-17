package com.cn.leedane.service.impl;

import com.cn.leedane.exception.OperateException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.LinkManageHandler;
import com.cn.leedane.handler.RolePermissionHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.LinkManageMapper;
import com.cn.leedane.mapper.LinkRoleOrPermissionMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.LinkManageService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * 链接权限管理service实现类
 * @author LeeDane
 * 2017年4月10日 下午4:49:07
 * version 1.0
 */
@Service("linkManageService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class LinkManageServiceImpl implements LinkManageService<LinkManageBean> {
	
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private LinkManageMapper linkManageMapper;
	
	@Autowired
	private LinkRoleOrPermissionMapper linkRoleOrPermissionMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private RolePermissionHandler rolePermissionHandler;
	
	@Autowired
	private LinkManageHandler linkManageHandler;
	
	/*@Override
	public List<LinkManageBean> getAllLinks() {
		logger.info("LinkManageServiceImpl-->getAllLinks()");
		return linkManageMapper.getAllLinks(ConstantsUtil.STATUS_NORMAL);
	}
	*/
	@Override
	public Map<String, Object> save(JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("LinkManageServiceImpl-->save():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		LinkManageBean linkManageBean = new LinkManageBean();
		linkManageBean.setAlias(JsonUtil.getStringValue(jsonObject, "alias"));
		linkManageBean.setAll_(JsonUtil.getBooleanValue(jsonObject, "all", true));
		linkManageBean.setLink(JsonUtil.getStringValue(jsonObject, "link"));
		linkManageBean.setOrder_(JsonUtil.getIntValue(jsonObject, "order", 1));
		
		boolean role = JsonUtil.getBooleanValue(jsonObject, "role", false);
		linkManageBean.setRole(role);
		linkManageBean.setCreateTime(new Date());
		linkManageBean.setCreateUserId(user.getId());
		linkManageBean.setStatus(JsonUtil.getIntValue(jsonObject, "status", ConstantsUtil.STATUS_NORMAL));
		boolean result = linkManageMapper.save(linkManageBean) > 0;
		
		if(!result){
			throw new OperateException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
		}
		//清空缓存的全部链接
		linkManageHandler.deleteAllLinkManagesCache();
		message.put("isSuccess", result);
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> edit(JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("LinkManageServiceImpl-->edit():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		int lnid = JsonUtil.getIntValue(jsonObject, "id");
		LinkManageBean oldLinkManageBean = linkManageMapper.findById(LinkManageBean.class, lnid);
		if(oldLinkManageBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		
		LinkManageBean linkManageBean = new LinkManageBean();
		linkManageBean.setId(lnid);
		linkManageBean.setAlias(JsonUtil.getStringValue(jsonObject, "alias"));
		linkManageBean.setAll_(JsonUtil.getBooleanValue(jsonObject, "all", true));
		linkManageBean.setLink(JsonUtil.getStringValue(jsonObject, "link"));
		linkManageBean.setOrder_(JsonUtil.getIntValue(jsonObject, "order", 1));
		
		boolean role = JsonUtil.getBooleanValue(jsonObject, "role", false);
		linkManageBean.setRole(role);
		linkManageBean.setCreateTime(new Date());
		linkManageBean.setCreateUserId(user.getId());
		linkManageBean.setStatus(JsonUtil.getIntValue(jsonObject, "status", ConstantsUtil.STATUS_NORMAL));
		boolean result = linkManageMapper.update(linkManageBean) > 0;
		
		if(!result){
			throw new OperateException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
		}
				
		//删除所有的角色或者权限
		//清空权限相关的缓存
		List<Map<String, Object>> uids = linkRoleOrPermissionMapper.getUsersByLinkId(lnid);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				//清空权限相关的缓存
				rolePermissionHandler.deleteByUser(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		linkRoleOrPermissionMapper.deleteByField(LinkRoleOrPermissionBean.class, "link_id", linkManageBean.getId());
		
		message.put("isSuccess", result);
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(int lnid, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("LinkManageServiceImpl-->delete():lnid="+ lnid);
		ResponseMap message = new ResponseMap();
				
		boolean result = linkManageMapper.deleteById(LinkManageBean.class, lnid) > 0;
		message.put("isSuccess", result);
		if(result){
			//删除所有的角色或者权限
			List<Map<String, Object>> uids = linkRoleOrPermissionMapper.getUsersByLinkId(lnid);
			if(CollectionUtil.isNotEmpty(uids)){
				for(Map<String, Object> uid: uids)
					//清空权限相关的缓存
					rolePermissionHandler.deleteByUser(StringUtil.changeObjectToInt(uid.get("user_id")));
			}
			linkRoleOrPermissionMapper.deleteByField(LinkRoleOrPermissionBean.class, "link_id", lnid);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("LinkManageServiceImpl-->paging():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jsonObject, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jsonObject, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jsonObject, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<Map<String, Object>> rs = linkManageMapper.paging(start, pageSize, ConstantsUtil.STATUS_NORMAL);

		if(CollectionUtil.isNotEmpty(rs)){
			int createUserId = 0;			
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}
		}
		message.put("total", SqlUtil.getTotalByList(linkManageMapper.getTotal(DataTableType.链接管理.value, null)));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取链接管理列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> deletes(String lnids, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("LinkManageServiceImpl-->deletes():lnids="+ lnids);
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(lnids))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
		
		String[] lnidArray = lnids.split(",");
		int[] ids = new int[lnidArray.length];
		
		StringBuffer sql = new StringBuffer("delete from " + DataTableType.用户角色.value + " where ");
		for(int i = 0; i < lnidArray.length; i++){
			ids[i] = StringUtil.changeObjectToInt(lnidArray[i]);
			if(i == lnidArray.length - 1)
				sql.append("role_id = "+ ids[i]);
			else
				sql.append("role_id = "+ ids[i] +" or ");
		}
				
		//删除所有的角色或者权限
		List<Map<String, Object>> uids = linkRoleOrPermissionMapper.getUsersByLinkIds(ids);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				//清空权限相关的缓存
				rolePermissionHandler.deleteByUser(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		linkRoleOrPermissionMapper.deleteByField(LinkRoleOrPermissionBean.class, "link_id", lnidArray);
		
		boolean result = linkManageMapper.deleteByIds(LinkManageBean.class, ids) == lnidArray.length;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}
		
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> roleOrPermissions(int lnid, boolean role, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("LinkManageServiceImpl-->roleOrPermissions():lnid="+lnid);
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> rs;
		if(role)
			rs = linkManageMapper.roles(lnid, ConstantsUtil.STATUS_NORMAL);
		else
			rs = linkManageMapper.permissions(lnid, ConstantsUtil.STATUS_NORMAL);
				
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取角色权限列表").toString(), "users()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> allot(int lnid, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("LinkManageServiceImpl-->allot():lnid="+lnid +", json="+ json);
		
		ResponseMap message = new ResponseMap();
		
		String roleOrPermissions = JsonUtil.getStringValue(json, "roleOrPermissionIds");
		boolean role = JsonUtil.getBooleanValue(json, "role");
		//根据角色id去删除所有相关的用户
		String[] roleOrPermissionArray = roleOrPermissions.split(",");
		int[] roleOrPermissionIds = new int[roleOrPermissionArray.length];
		for(int i = 0; i < roleOrPermissionArray.length; i++){
			roleOrPermissionIds[i] = StringUtil.changeObjectToInt(roleOrPermissionArray[i]);
		}
		
		//获取链接的所有用户ID
		Set<Integer> clearIds = new HashSet<Integer>();
		List<Map<String, Object>> uids = linkRoleOrPermissionMapper.getUsersByLinkId(lnid);
		if(CollectionUtil.isNotEmpty(uids)){
			for(Map<String, Object> uid: uids)
				clearIds.add(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		linkRoleOrPermissionMapper.deleteByField(LinkRoleOrPermissionBean.class, "link_id", lnid);
		
		Date createTime = DateUtil.getCurrentTime();
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		for(int roleOrPermissionId: roleOrPermissionIds){
			Map<String, Object> map = new HashMap<String, Object>();
			if(role){
				map.put("role_id", roleOrPermissionId);
			}else{
				map.put("permission_id", roleOrPermissionId);
			}
			map.put("link_id", lnid);
			map.put("role", role);
			map.put("create_user_id", user.getId());
			map.put("create_time", new Timestamp(createTime.getTime()));
			data.add(map);
		}
		
		//删除所有的
		if(StringUtil.isNotNull(roleOrPermissions)){
			linkRoleOrPermissionMapper.insertByBatch(data);
		}
		
		//再次获取链接的所有用户ID
		List<Map<String, Object>> uidsAfter = linkRoleOrPermissionMapper.getUsersByLinkId(lnid);
		if(CollectionUtil.isNotEmpty(uidsAfter)){
			for(Map<String, Object> uid: uidsAfter)
				clearIds.add(StringUtil.changeObjectToInt(uid.get("user_id")));
		}
		
		//清空角色权限相关的缓存
		if(clearIds.size() > 0){
			logger.info("将清空以下用户的角色权限相关的缓存----->"+StringUtils.join(clearIds.toArray(), ","));
			for(Integer clearId: clearIds)
				rolePermissionHandler.deleteByUser(clearId);
		}
				
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"给链接ID为"+ lnid +",分配角色获取权限ids"+roleOrPermissions).toString(), "allot()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", "操作成功");
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		return message.getMap();
	}
}