package com.cn.leedane.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CircleOfFriendsHandler;
import com.cn.leedane.handler.FanHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.FanMapper;
import com.cn.leedane.model.FanBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FanService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * 粉丝service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:34:20
 * Version 1.0
 */
@Service("fanService")
public class FanServiceImpl implements FanService<FanBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private FanMapper fanMapper;
	
	@Autowired
	private UserService<UserBean> userService;
	
	public void setUserService(UserService<UserBean> userService) {
		this.userService = userService;
	}
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private FanHandler fanHandler;
	
	@Autowired
	private CircleOfFriendsHandler circleOfFriendsHandler;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private NotificationHandler notificationHandler;
	
	@Override
	public Map<String, Object> getMyAttentionsLimit(JSONObject jo, UserBean user, HttpServletRequest request) {
		long start = System.currentTimeMillis();
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		int toUserId = JsonUtil.getIntValue(jo, "toUserId", user.getId());
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		logger.info("FanServiceImpl-->getMyAttentionFansLimit():jo="+jo.toString());
		
		ResponseMap message = new ResponseMap();
		
		//暂时不支持查看别人的关注好友列表
		if(toUserId != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		StringBuffer sql;
		if("firstloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id, f.to_user_id user_id, f.user_remark remark, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.create_user_id=? ");
			sql.append(" order by f.id desc limit 0,?");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id, f.to_user_id user_id, f.user_remark remark, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.create_user_id=? ");
			sql.append(" and f.id < ? order by f.id desc limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id, f.to_user_id user_id, f.user_remark remark, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.create_user_id=? ");
			sql.append(" and f.id > ? limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId,firstId, pageSize);
		}
				
		if(rs !=null && rs.size() > 0){
			int toId = 0;
			for(int i = 0; i < rs.size(); i++){
				toId = StringUtil.changeObjectToInt(rs.get(i).get("user_id"));
				if(toId> 0){
					rs.get(i).putAll(userHandler.getBaseUserInfo(toId));
				}
				rs.get(i).put("is_fan", fanHandler.inAttention(toId, toUserId));
				rs.get(i).put("is_attention", true);
			}	
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"获取所关注的对象的列表", "getMyAttentionFansLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		
		long end = System.currentTimeMillis();
		logger.info("获取我关注对象列表总计耗时：" +(end - start) +"毫秒");
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getToAttentionsLimit(JSONObject jo, UserBean user, HttpServletRequest request) {
		long start = System.currentTimeMillis();
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		int toUserId = JsonUtil.getIntValue(jo, "toUserId");
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		logger.info("FanServiceImpl-->getToAttentionsLimit():jo="+jo.toString());
		
		ResponseMap message = new ResponseMap();
		
		if(toUserId < 1 || toUserId == user.getId())
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		StringBuffer sql;
		if("firstloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id, f.to_user_id user_id, f.user_remark remark, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.create_user_id=? ");
			sql.append(" order by f.id desc limit 0,?");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id, f.to_user_id user_id, f.user_remark remark, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.create_user_id=? ");
			sql.append(" and f.id < ? order by f.id desc limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id, f.to_user_id user_id, f.user_remark remark, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.create_user_id=? ");
			sql.append(" and f.id > ? limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId,firstId, pageSize);
		}
				
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("user_id"));
				logger.info("user_id:"+createUserId);
				if(createUserId> 0){
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				}
				//是否粉和是否关注都相对于我(user)而已展示
				rs.get(i).put("is_fan", fanHandler.inAttention(user.getId(), createUserId));
				rs.get(i).put("is_attention", fanHandler.inAttention(user.getId(), createUserId));
			}	
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"获取ID为"+toUserId+"的用户所关注的对象的列表", "getToAttentionsLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		
		long end = System.currentTimeMillis();
		logger.info("获取TA关注对象列表总计耗时：" +(end - start) +"毫秒");
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getMyFansLimit(JSONObject jo, UserBean user, HttpServletRequest request) {
		long start = System.currentTimeMillis();
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		int toUserId = JsonUtil.getIntValue(jo, "toUserId", user.getId());
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		logger.info("FanServiceImpl-->getMyFansLimit():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		if(toUserId != user.getId())
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		StringBuffer sql;
		if("firstloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id user_id, f.to_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.to_user_id=? ");
			sql.append(" order by f.id desc limit 0,?");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id user_id, f.to_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.to_user_id=? ");
			sql.append(" and f.id < ? order by f.id desc limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id user_id, f.to_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.to_user_id=? ");
			sql.append(" and f.id > ? limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId,firstId, pageSize);
		}
				
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("user_id"));
				logger.info("user_id:"+createUserId);
				if(createUserId> 0){
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				}
				rs.get(i).put("is_fan", true);
				rs.get(i).put("is_attention", fanHandler.inAttention(toUserId, createUserId));
			}	
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"获取粉丝列表", "getMyFansLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		
		long end = System.currentTimeMillis();
		logger.info("获取我的粉丝列表总计耗时：" +(end - start) +"毫秒");
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getToFansLimit(JSONObject jo, UserBean user, HttpServletRequest request) {
		long start = System.currentTimeMillis();
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		int toUserId = JsonUtil.getIntValue(jo, "toUserId");
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		logger.info("FanServiceImpl-->getToFansLimit():jo="+jo.toString());
		
		ResponseMap message = new ResponseMap();
		if(toUserId < 1 || toUserId == user.getId())
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		StringBuffer sql;
		if("firstloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id user_id, f.to_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.to_user_id=? ");
			sql.append(" order by f.id desc limit 0,?");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id user_id, f.to_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.to_user_id=? ");
			sql.append(" and f.id < ? order by f.id desc limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select f.id, f.create_user_id user_id, f.to_user_id, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.粉丝.value+" f");
			sql.append(" where f.status=? and f.to_user_id=? ");
			sql.append(" and f.id > ? limit 0,? ");
			rs = fanMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, toUserId,firstId, pageSize);
		}
				
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("user_id"));
				logger.info("user_id:"+createUserId);
				if(createUserId> 0){
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				}
				//是否粉和是否关注都相对于我(user)而已展示
				rs.get(i).put("is_fan", fanHandler.inAttention(user.getId(), createUserId));
				rs.get(i).put("is_attention", fanHandler.inAttention(user.getId(), createUserId));
			}	
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"获取用户ID为："+toUserId+"的粉丝列表", "getToFansLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		
		long end = System.currentTimeMillis();
		logger.info("获取TA的粉丝列表总计耗时：" +(end - start) +"毫秒");
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public boolean cancel(JSONObject jo, UserBean user, HttpServletRequest request) {
		String toUserIds = JsonUtil.getStringValue(jo, "toUserIds");
		logger.info("FanServiceImpl-->cancel():id="+user.getId()+",toUserIds="+toUserIds);
		boolean result = false;
		if(StringUtil.isNotNull(toUserIds)){
			String[] ids = toUserIds.split(",");
			int[] fanIds = new int[ids.length];
			for(int i=0; i< ids.length; i++){
				fanIds[i] = Integer.parseInt(ids[i]);
			}
			result = this.fanMapper.deleteSql(FanBean.class, "  where ( create_user_id = ? and to_user_id in("+ getFanStr(fanIds) +") ) ", user.getId()) > 0;
			if(result){
				for(int i = 0; i< fanIds.length; i++)
					fanHandler.cancelAttention(user.getId(), fanIds[i]);
			}
		}
		
		return result;
	}
	
	private String getFanStr(int... fanIds){
		logger.info("FanDaoImpl-->getFanStr():fanIds="+fanIds);
		if(fanIds.length > 0){
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < fanIds.length; i++){
				
				if(i == fanIds.length -1){
					buffer.append(fanIds[i]);
				}else{
					buffer.append(fanIds[i] + ",");
				}
			}
			
			return buffer.toString();
		}
		return "";
	}

	@Override
	public boolean isFanEachOther(JSONObject jo, UserBean user, HttpServletRequest request) {
		int toUserId = JsonUtil.getIntValue(jo, "toUserId");
		logger.info("FanServiceImpl-->isFanEachOther():id="+user.getId()+",toUserId="+toUserId);
		List<Map<String, Object>> list = this.fanMapper.isFanEachOther(user.getId(), toUserId, ConstantsUtil.STATUS_NORMAL);
		if(!CollectionUtils.isEmpty(list) && list.size() == 2)
			return true;
		return false;
	}

	@Override
	public Map<String, Object> addFan(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("FanServiceImpl-->addFan():jo="+jo.toString());
		int toUserId = JsonUtil.getIntValue(jo, "toUserId");
		String remark = JsonUtil.getStringValue(jo, "remark");
		ResponseMap message = new ResponseMap();
		if(toUserId == 0) {
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();			
		}

		UserBean toUser = userService.findById(toUserId);
		if(toUser == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		
		if(toUserId == user.getId()){
			message.put("message", "不能添加自己为好友"); 
			return message.getMap();	
		}
		
		
		if(cheakIsAddFan(user.getId(), toUserId)){
			message.put("message", "您已关注过TA，请勿重复操作！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();	
		}
		FanBean fanBean = new FanBean();
		fanBean.setToUserId(toUserId);
		fanBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		fanBean.setCreateUserId(user.getId());
		fanBean.setCreateTime(new Date());
		fanBean.setUserRemark(remark);
		
		if(!(fanMapper.save(fanBean)> 0)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
			return message.getMap();	
		}else{
			//发送通知给相应的用户
			String content = user.getAccount() +"关注您";
			notificationHandler.sendNotificationById(false, user, toUserId, content, NotificationType.通知, DataTableType.粉丝.value, fanBean.getId(), null);
		}
		
		fanHandler.addAttention(user, toUser);
		
		//合并两个用户的朋友圈
		//myCircleOfFriendsHandler.mergeTimeLine(user.getId(), toUserId);
		
		message.put("message", "恭喜您成为"+toUser.getAccount()+"的粉丝，今后他/她的动态将在您的朋友圈出现"); 
		message.put("isSuccess", true);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"成为："+toUser.getAccount()+"为粉丝", "addFan()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}

	/**
	 * 判断是否已经成为TA的粉丝
	 * @param id
	 * @param toUserId
	 * @return
	 */
	private boolean cheakIsAddFan(int id, int toUserId) {
		return this.fanMapper.executeSQL("select id from "+DataTableType.粉丝.value+" where create_user_id=? and to_user_id =? and status=?", id, toUserId, ConstantsUtil.STATUS_NORMAL).size() > 0;
	}

	@Override
	public Map<String, Object> isFan(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("FanServiceImpl-->isFan():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		int toUserId = JsonUtil.getIntValue(jo, "toUserId");
		
		if(toUserId == 0) {
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();			
		}
		
		if(isFanEachOther(jo, user, request)){
			message.put("message", "已互相关注");
			message.put("isSuccess", true);
			return message.getMap();
		}
		
		boolean isFan = fanMapper.executeSQL("select id from "+DataTableType.粉丝.value+" where status=? and create_user_id=? and to_user_id=?", ConstantsUtil.STATUS_NORMAL, user.getId(), toUserId).size() > 0;
		if(isFan){
			message.put("message", "已关注");
			message.put("isSuccess", true);
			return message.getMap();
		}else{
			message.put("message", "关注Ta");
			return message.getMap();
			
		}
	}

	@Override
	public List<Map<String, Object>> executeSQL(String sql, Object... params) {
		return fanMapper.executeSQL(sql, params);
	}
	
}
