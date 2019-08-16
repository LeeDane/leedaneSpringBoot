package com.cn.leedane.service.impl;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.FriendMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.FriendService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.SqlBaseService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 好友service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:42:59
 * Version 1.0
 */
@Service("friendService")
public class FriendServiceImpl implements FriendService<FriendBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private FriendMapper friendMapper;
	
	@Autowired
	private UserService<UserBean> userService;
	
	public void setUserService(UserService<UserBean> userService) {
		this.userService = userService;
	}

	@Autowired
	private FriendHandler friendHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private SqlBaseService<IDBean> sqlBaseService;
	
	
	@Override
	public Map<String, Object> deleteFriends(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->deleteFriends():jo="+jo.toString());
		
		ResponseMap message = new ResponseMap();
		int fid = JsonUtil.getIntValue(jo, "fid");
		if(fid < 1) {
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();			
		}
				
		FriendBean friendBean = friendMapper.findById(FriendBean.class, fid);
		int toUserId = friendBean.getToUserId();
		int fromUserId = friendBean.getFromUserId();
		boolean result = false;
		if(friendBean != null){
			result = friendMapper.deleteById(FriendBean.class, fid) > 0;
		}
		if(result){
			//清空redis用户的缓存
			friendHandler.delete(fromUserId, toUserId);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.解除好友关系成功.value));
			//发送通知给相应的用户
			String content = "您的好友" +user.getAccount() +"已经解除了您们的好友关系";
			notificationHandler.sendNotificationById(false, user, (user.getId() == fromUserId ? toUserId: fromUserId), content, NotificationType.通知, DataTableType.好友.value, fid, null);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库删除数据失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库删除数据失败.value);
		}
		message.put("isSuccess", result);
		String subject = user.getAccount()+"解除关系ID为"+fid+"的好友关系"+StringUtil.getSuccessOrNoStr(result);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, subject, "deleteFriends()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		
		return message.getMap();
	}

	@Override
	public boolean isFriend(int id, int to_user_id) {
		logger.info("FriendServiceImpl-->isFriend():id="+id+",to_user_id="+to_user_id);
		return SqlUtil.getBooleanByList(this.friendMapper.isFriend(id, to_user_id, ConstantsUtil.STATUS_NORMAL));
	}

	@Override
	public Map<String, Object> addFriend(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->addFriend():jo="+jo.toString());
		int toUserId = JsonUtil.getIntValue(jo, "to_user_id");
		ResponseMap message = new ResponseMap();
		if(toUserId == 0) {
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();			
		}
		
		if(isFriendRecord(user.getId(), toUserId)){
			message.put("message", "等待对方确认..."); 
			message.put("isSuccess", true);
			return message.getMap();	
		}
		
		UserBean toUser = userService.findById(toUserId);
		if(toUser == null){
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户不存在或请求参数不对.value));
		}
		
		if(toUserId == user.getId()){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.不能添加自己为好友.value));
			message.put("responseCode", EnumUtil.ResponseCode.不能添加自己为好友.value);
			return message.getMap();	
		}
		FriendBean friendBean = new FriendBean();
		friendBean.setFromUserId(user.getId());
		friendBean.setToUserId(toUserId);
		friendBean.setAddIntroduce(JsonUtil.getStringValue(jo, "add_introduce"));
		friendBean.setStatus(ConstantsUtil.STATUS_DISABLE);
		friendBean.setCreateUserId(user.getId());
		friendBean.setCreateTime(new Date());
		
		if(StringUtil.isNotNull(JsonUtil.getStringValue(jo, "to_user_remark"))){
			friendBean.setToUserRemark(JsonUtil.getStringValue(jo, "to_user_remark"));
		}else{
			friendBean.setToUserRemark(toUser.getAccount());
		}
		//因为from_user_remark字段不能为空，所以暂时给一个""值
		friendBean.setFromUserRemark("");
		if(!(friendMapper.save(friendBean)>0)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.添加好友失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.添加好友失败.value);
			return message.getMap();	
		}
		//发送通知给相应的用户
		String content = user.getAccount() +"请求与您成为好友";
		notificationHandler.sendNotificationById(false, user, toUserId, content, NotificationType.通知, DataTableType.好友.value, friendBean.getId(), null);
	
		message.put("message", "等待对方确认..."); 
		message.put("isSuccess", true);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"请求添加账号："+toUser.getAccount()+"为好友", "addFriend()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> addAgree(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->addAgree():jo="+jo.toString());
		int fid = JsonUtil.getIntValue(jo, "fid");
		ResponseMap message = new ResponseMap();
		if(fid == 0){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();	
		}
		FriendBean friendBean = friendMapper.findById(FriendBean.class, fid);
		if(friendBean == null){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.好友关系不存在.value));
			message.put("responseCode", EnumUtil.ResponseCode.好友关系不存在.value);
			return message.getMap();		
		}
		
		if(friendBean.getStatus() != ConstantsUtil.STATUS_DISABLE){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.好友关系不是待确认状态.value));
			message.put("responseCode", EnumUtil.ResponseCode.好友关系不是待确认状态.value);
			return message.getMap();	
		}
		
		if(friendBean.getStatus() == ConstantsUtil.STATUS_NORMAL){
			message.put("isSuccess", true);
			message.put("message", "恭喜，TA已经是好友"); 
			return message.getMap();	
		}
		
		friendBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		friendBean.setModifyUserId(user.getId());
		friendBean.setModifyTime(new Date());
		String fromUserRemark = null;
		if(StringUtil.isNotNull(JsonUtil.getStringValue(jo, "from_user_remark"))){
			fromUserRemark = JsonUtil.getStringValue(jo, "from_user_remark");
		}else{
			fromUserRemark = userHandler.getUserName(friendBean.getFromUserId());	
		}
			
		friendBean.setFromUserRemark(fromUserRemark);
		 
		if(!(friendMapper.save(friendBean)>0)){
			//message.put("message", "同意好友关系失败，请稍后重试"); 
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
			return message.getMap();	
		}
		
		//更新redis的朋友关系
		message.put("isSuccess", friendHandler.addFriends(friendBean.getToUserId(), friendBean.getFromUserId(), friendBean.getToUserRemark(), friendBean.getFromUserRemark()));
		message.put("message", "恭喜，TA已经是好友"); 
		
		//发送通知给相应的用户
		String content = user.getAccount() +"已经同意您的好友请求";
		notificationHandler.sendNotificationById(false, user, friendBean.getFromUserId(), content, NotificationType.通知, DataTableType.好友.value, friendBean.getId(), null);
	
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"同意好友关系"+fid, "addAgree()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public boolean isFriendRecord(int id, int to_user_id) {
		logger.info("FriendServiceImpl-->isFriendRecord():id="+id+",to_user_id="+to_user_id);
		return SqlUtil.getBooleanByList(this.friendMapper.isFriendRecord(id, to_user_id));
	}

	@Override
	public Map<String, Object> friendsAlreadyPaging(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->friendsAlreadyPaging():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
	
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, '' introduce, date_format(modify_time,'%Y-%m-%d %H:%i:%s') create_time, status, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status =?");
			sql.append(" UNION");
			sql.append(" select id, from_user_id fid, add_introduce introduce, date_format(modify_time,'%Y-%m-%d %H:%i:%s') create_time, status, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status =?");
			sql.append(" order by id desc limit 0,?");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(),ConstantsUtil.STATUS_NORMAL, ConstantsUtil.STATUS_NORMAL, user.getId(), ConstantsUtil.STATUS_NORMAL, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, '' introduce, date_format(modify_time,'%Y-%m-%d %H:%i:%s') create_time, status, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status =? and id < ?");
			sql.append(" UNION");
			sql.append(" select id, from_user_id fid, add_introduce introduce, date_format(modify_time,'%Y-%m-%d %H:%i:%s') create_time, status, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status =?");
			sql.append(" and id < ? order by id desc limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(),ConstantsUtil.STATUS_NORMAL, lastId, ConstantsUtil.STATUS_NORMAL, user.getId(), ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, '' introduce, date_format(modify_time,'%Y-%m-%d %H:%i:%s') create_time, status, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status =? and id > ?");
			sql.append(" UNION");
			sql.append(" select id, from_user_id fid, add_introduce introduce, date_format(modify_time,'%Y-%m-%d %H:%i:%s') create_time, status, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status =?");
			sql.append(" and id > ? limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(),ConstantsUtil.STATUS_NORMAL, firstId, ConstantsUtil.STATUS_NORMAL, user.getId(), ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
		}
		
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("fid"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}	
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"读取已经跟我成为好友关系的分页列表").toString(), "friendsAlreadyPaging()", ConstantsUtil.STATUS_NORMAL, 0);
			
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> friendsNotyetPaging(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->friendsNotyetPaging():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
	
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select id, "+ 4 +" status, to_user_id fid, add_introduce introduce, date_format((case when modify_time is null then create_time else modify_time end),'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status =?");
			sql.append(" UNION");
			sql.append(" select id, "+ ConstantsUtil.STATUS_DISABLE +" status, from_user_id fid, add_introduce introduce, date_format((case when modify_time is null then create_time else modify_time end),'%Y-%m-%d %H:%i:%s') create_time, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status =?");
			sql.append(" order by id desc limit 0,?");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(),ConstantsUtil.STATUS_DISABLE, ConstantsUtil.STATUS_NORMAL, user.getId(), ConstantsUtil.STATUS_DISABLE, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select id, "+ 4 +" status, to_user_id fid, add_introduce introduce, date_format((case when modify_time is null then create_time else modify_time end),'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status =? and id < ?");
			sql.append(" UNION");
			sql.append(" select id, "+ ConstantsUtil.STATUS_DISABLE +" status, from_user_id fid, add_introduce introduce, date_format((case when modify_time is null then create_time else modify_time end),'%Y-%m-%d %H:%i:%s') create_time, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status =?");
			sql.append(" and id < ? order by id desc limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(),ConstantsUtil.STATUS_DISABLE, lastId, ConstantsUtil.STATUS_NORMAL, user.getId(), ConstantsUtil.STATUS_DISABLE, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select id, "+ 4 +" status, to_user_id fid, add_introduce introduce, date_format((case when modify_time is null then create_time else modify_time end),'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status =? and id > ?");
			sql.append(" UNION");
			sql.append(" select id, "+ ConstantsUtil.STATUS_DISABLE +" status, from_user_id fid, add_introduce introduce, date_format((case when modify_time is null then create_time else modify_time end),'%Y-%m-%d %H:%i:%s') create_time, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status =?");
			sql.append(" and id > ? limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(),ConstantsUtil.STATUS_DISABLE, firstId, ConstantsUtil.STATUS_NORMAL, user.getId(), ConstantsUtil.STATUS_DISABLE, firstId, pageSize);
		}
		
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("fid"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}	
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取暂时未跟我成为好友关系的分页列表").toString(), "friendsNotyetPaging()", ConstantsUtil.STATUS_NORMAL, 0);
			
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> requestPaging(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->requestPaging():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
	
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, status, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark");
			sql.append(" from "+DataTableType.好友.value+" where from_user_id =?");
			sql.append(" order by id desc limit 0,?");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, status, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark");
			sql.append(" from "+DataTableType.好友.value+" where from_user_id =?");
			sql.append(" and id < ? order by id desc limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, status, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark");
			sql.append(" from "+DataTableType.好友.value+" where from_user_id =?");
			sql.append(" and id > ? limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), firstId, pageSize);
		}
		
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("fid"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}	
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取我发送的好友请求列表").toString(), "requestPaging()", ConstantsUtil.STATUS_NORMAL, 0);
			
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> responsePaging(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->responsePaging():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
	
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, status, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark");
			sql.append(" from "+DataTableType.好友.value+" where from_user_id =?");
			sql.append(" order by id desc limit 0,?");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, status, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark");
			sql.append(" from "+DataTableType.好友.value+" where from_user_id =?");
			sql.append(" and id < ? order by id desc limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select id, to_user_id fid, status, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark");
			sql.append(" from "+DataTableType.好友.value+" where from_user_id =?");
			sql.append(" and id > ? limit 0,? ");
			rs = friendMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, user.getId(), firstId, pageSize);
		}
		
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("fid"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}	
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取等待我同意的好友关系列表").toString(), "responsePaging()", ConstantsUtil.STATUS_NORMAL, 0);
			
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> matchContact(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->matchContact():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		
		String contacts = JsonUtil.getStringValue(jo, "contacts"); //用户本地上传的联系人
		if(StringUtil.isNull(contacts)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}	
		//获取所有通讯录用户
		//JSONObject contactsObj = JSONObject.fromObject(contacts);
		//List<ShowContactsBean> showContactsBeans = (List<ShowContactsBean>) JSONObject.toBean(contactsObj);
		
		//获得所有系统用户的信息
		//JSONArray userInfos = userHandler.getAllUserDetail();
		
		//获取该用户的所有好友ID
		//Set<Integer> ids = friendHandler.getFromToFriendIds(user.getId());
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"本地联系人跟服务器上的好友进行匹配").toString(), "matchContact()", ConstantsUtil.STATUS_NORMAL, 0);
			
		message.put("isSuccess", true);
		message.put("message", null);
		return message.getMap();
	}

	@Override
	public Map<String, Object> friends(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FriendServiceImpl-->matchContact():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
		String sql = " select to_user_id id, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status = "+ConstantsUtil.STATUS_NORMAL+") else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status = "+ConstantsUtil.STATUS_NORMAL+" "
				+" UNION " 
				+" select from_user_id id, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status = "+ConstantsUtil.STATUS_NORMAL+") else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status = "+ConstantsUtil.STATUS_NORMAL;

		List<Map<String, Object>> rs = friendMapper.executeSQL(sql, user.getId(), user.getId());
		if(rs !=null && rs.size() > 0){
			int fId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				fId = StringUtil.changeObjectToInt(rs.get(i).get("id"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(fId));
			}
		}
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}
	
}
