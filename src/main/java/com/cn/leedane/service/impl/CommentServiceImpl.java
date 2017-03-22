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

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.FilterUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.handler.CommentHandler;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.CommentMapper;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.FriendBean;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.service.FriendService;
import com.cn.leedane.service.NotificationService;
import com.cn.leedane.service.OperateLogService;
/**
 * 评论service的实现类
 * @author LeeDane
 * 2016年7月12日 下午1:29:02
 * Version 1.0
 */
@Service("commentService")
public class CommentServiceImpl extends AdminRoleCheckService implements CommentService<CommentBean>{
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private CommentMapper commentMapper;
	
	@Autowired
	private FriendService<FriendBean> friendService;
	
	@Autowired
	private NotificationService<NotificationBean> notificationService;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private FriendHandler friendHandler;
	
	@Autowired
	private CommonHandler commonHandler;
	
	@Autowired
	private CommentHandler commentHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpServletRequest request){
		// {\"table_name\":\""+DataTableType.心情.value+"\", \"table_id\":123
		//, \"content\":\"我同意\" ,\"level\": 1, \"pid\":23, \"from\":\"Android客户端\"}
		logger.info("CommentServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0);
		String content = JsonUtil.getStringValue(jo, "content");
		String from = JsonUtil.getStringValue(jo, "froms");
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message))
			return message;
		
		if(StringUtil.isNull(tableName) || tableId < 1 || StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message;
		}
		
		//评论权限校验
		if(!checkComment(tableName, tableId, message)){
			return message;
		}
		
		int level = JsonUtil.getIntValue(jo, "level", 5);
		int pid = JsonUtil.getIntValue(jo, "pid", 0);
		CommentBean bean = new CommentBean();
		bean.setCommentLevel(level);
		bean.setContent(content);
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setPid(pid);
		bean.setFroms(from);
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setTableId(tableId);
		bean.setTableName(tableName);
		//bean.setCid(cid);
		
		if(commentMapper.save(bean) > 0){
			int createUserId = 0;
			//String str = "{from_user_remark}评论您："+content;
			if(pid > 0){//说明是回复别人的评论
				createUserId = getCommentCreateUserId(pid);			
			}else{ //说明评论的是某一个对象
				createUserId = SqlUtil.getCreateUserIdByList(commentMapper.getObjectCreateUserId(tableName, tableId));
				
			}
			if(createUserId > 0 && createUserId != user.getId()){
				notificationHandler.sendNotificationById(false, user, createUserId, content, NotificationType.评论, tableName, tableId, bean);
			}
			//当一个人既有评论也有@时候，@不做处理
			//有@人通知相关人员
			Set<String> usernames = StringUtil.getAtUserName(content);
			if(usernames.size() > 0){
				if(pid > 0){
					String createUserName = userHandler.getUserName(createUserId);
					if(StringUtil.isNotNull(createUserName)){
						usernames.remove(createUserName);
					}
				}
				if(usernames.size() > 0){
					//str = "{from_user_remark}在评论时候@您,点击查看详情";
					notificationHandler.sendNotificationByNames(false, user, usernames, content, NotificationType.艾特我, tableName, tableId, bean);
				}
			}
			
			/**
			 * 通过表名+ID唯一存储
			 */
			commentHandler.addComment(tableName, tableId);
		}
		message.put("isSuccess", true);
		return message;
	}
	
	/**
	 * 检验评论是否可以进行下去
	 * @param tableName
	 * @param tableId
	 * @param message
	 */
	private boolean checkComment(String tableName, int tableId, Map<String, Object> message) {
		
		List<Map<String, Object>> list = commentMapper.executeSQL("select id, can_comment from "+tableName +" where id = ? limit 0,1", tableId);
		//检查该实体数据是否数据存在,防止对不存在的对象添加评论
		if(list == null || list.size() != 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作实例.value);
			return false;
		}
		
		boolean canComment = StringUtil.changeObjectToBoolean(list.get(0).get("can_comment"));
		if(!canComment){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.该资源现在不支持评论.value));
			message.put("responseCode", EnumUtil.ResponseCode.该资源现在不支持评论.value);
		}
		return canComment;
	}

	/**
	 * 根据评论的ID获取评论的创建人ID
	 * @param commentId
	 * @return
	 */
	private int getCommentCreateUserId(int commentId){
		int createUserId = 0;
		List<Map<String, Object>> list = commentMapper.executeSQL("select create_user_id from "+DataTableType.评论.value+" where status=? and id=? limit 1", ConstantsUtil.STATUS_NORMAL, commentId);
		if(list != null && list.size() == 1){
			createUserId = StringUtil.changeObjectToInt(list.get(0).get("create_user_id"));
		}
		return createUserId;
	}

	@Override
	public List<Map<String, Object>> getCommentsByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request) throws Exception {
		logger.info("CommentServiceImpl-->getCommentByLimit():jsonObject=" +jo.toString());
		
		if(user == null)
			user = OptionUtil.adminUser;
		 //{\"uid\":2,\"table_name\":\"t_mood\", \"table_id\":123,\"pageSize\":5
		//, \"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //操作表名
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0); //操作表中的id
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int toUserId = JsonUtil.getIntValue(jo, "toUserId"); //操作的对象用户的id
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		boolean showUserInfo = JsonUtil.getBooleanValue(jo, "showUserInfo");
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<>();
	
		//查找该用户所有的转发
		if(StringUtil.isNull(tableName) && toUserId > 0){		
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id");
				sql.append(" where c.create_user_id =? and c.status = ? ");
				sql.append(" order by c.id desc limit 0,?");
				rs = commentMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id");
				sql.append(" where c.create_user_id =? and c.status = ? ");
				sql.append(" and c.id < ? order by c.id desc limit 0,? ");
				rs = commentMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id");
				sql.append(" where c.create_user_id =? and c.status = ? ");
				sql.append(" and c.id > ? limit 0,?  ");
				rs = commentMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
			}
		}
				
		//查找指定表的数据
		if(StringUtil.isNotNull(tableName) && toUserId < 1 && tableId > 0){
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id");
				sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ?");
				sql.append(" order by c.id desc limit 0,?");
				rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){			
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
				sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ?");
				sql.append(" and c.id < ? order by c.id desc limit 0,?");
				rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
				sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ?");
				sql.append(" and c.id > ? limit 0,?");
				rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
			}
		}
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			
			//String account = "";
			String tabName;
			int tabId;
			int pid = 0;
			int pCreateUserId = 0;
			String atUsername = ""; //@用户的名称
			String resultContent = null;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				if(StringUtil.isNull(tableName) && tableId <1){
					//在非获取指定表下的评论列表的情况下的前面35个字符
					tabName = StringUtil.changeNotNull((rs.get(i).get("table_name")));
					tabId = StringUtil.changeObjectToInt(rs.get(i).get("table_id"));
					rs.get(i).put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
				}else{
					pid = StringUtil.changeObjectToInt(rs.get(i).get("pid"));
					if(pid > 0 ){
						pCreateUserId = getCommentCreateUserId(pid);
						if(pCreateUserId > 0){
							atUsername = userHandler.getUserName(pCreateUserId);
							if(StringUtil.isNotNull(atUsername)){
								resultContent = StringUtil.changeNotNull((rs.get(i).get("content")));
								if(resultContent.indexOf("@"+atUsername) > 0 )
									rs.get(i).put("content", resultContent);
								else{
									rs.get(i).put("content", "回复@"+atUsername + " " + resultContent);
								}
							}
						}
					}
				}
				if(showUserInfo){
					createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId, user, friendObject));
				}
			}	
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取用户ID为：",toUserId,",表名：",tableName,"，表id为：",tableId,"的评论列表").toString(), "getCommentByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return rs;
	}
	
	/**
	 * 构建CreateUser的SQL语句字符串
	 * @param ids
	 * @return
	 */
	/*private String buildCreateUserIdInSQL(Object[] ids){
		int length = ids.length;
		if(length == 0) 
			return "";
		StringBuffer buffer = new StringBuffer();
		buffer.append(" and t.create_user_id in (");
		for(int i =0; i < length; i++){
			if(i == length -1){
				buffer.append(ids[i]);
			}else{
				buffer.append(ids[i] + ",");
			}
		}
		buffer.append(") ");
		
		return buffer.toString();
	}*/

	@Override
	public List<Map<String, Object>> getOneCommentItemsByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request) throws Exception {
		logger.info("CommentServiceImpl-->getOneCommentItemsByLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		 //{\"table_name\":\"t_mood\", \"table_id\":123
		//, \"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}
		List<Map<String, Object>> rs = new ArrayList<>();
		int cid = JsonUtil.getIntValue(jo, "cid");
		if(cid < 1) 
			throw new ErrorException("评论id为空");
		
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //操作表名
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0); //操作表中的id
		int pageSize = JsonUtil.getIntValue(jo, "pageSize"); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		StringBuffer sql = new StringBuffer();
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select c.id, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
			sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id, c.pid ");
			sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
			sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ? and c.cid = ? ");
			sql.append(" order by c.id desc");
			sql.append(getLimitSql(pageSize));
			rs = commentMapper.executeSQL(sql.toString()
					, tableName, tableId, ConstantsUtil.STATUS_NORMAL, cid);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){			
			sql.append("select c.id, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
			sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id, c.pid ");
			sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
			sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ? and c.cid = ? ");
			sql.append(" and c.id < ? order by c.id desc");
			sql.append(getLimitSql(pageSize));
			rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, 
					tableName, tableId, ConstantsUtil.STATUS_NORMAL, lastId, cid);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select c.id, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
			sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id, c.pid ");
			sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
			sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ? and c.pid = ? ");
			sql.append(" and c.id > ? ");
			sql.append(getLimitSql(pageSize));
			rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, 
					tableName, tableId, ConstantsUtil.STATUS_NORMAL, firstId, cid);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查看表：",tableName,"，表id为：",tableId,"，评论ID为：", cid, "的评论详情列表").toString(), "getOneCommentItemsByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return rs;
	}
	
	/**
	 * 根据页码生成Limit的SQL
	 * @param pageSize
	 * @return
	 */
	private String getLimitSql(int pageSize){
		if(pageSize == 0) return "";
		return " limit 0,"+ pageSize;
	}

	@Override
	public int getCountByObject(JSONObject jo, UserBean user,
			HttpServletRequest request) throws Exception {
		logger.info("CommentServiceImpl-->getCountByObject():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //操作表名
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0); //操作表中的id
		
		StringBuffer sql = new StringBuffer();
		sql.append("where table_name = '" + tableName +"'");
		sql.append(" and table_id = '" + tableId +"'");
		sql.append(" and status = " +ConstantsUtil.STATUS_NORMAL);
		int count = 0;
		count = SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, sql.toString()));
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查看表：",tableName,"，表id为：",tableId,"，查询得到的总数是：", count, "条").toString(), "getCountByObject()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return count;
	}

	@Override
	public int getCountByUser(JSONObject jo, UserBean user,
			HttpServletRequest request) throws Exception{
		logger.info("CommentServiceImpl-->getCountByUser():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		int uid = JsonUtil.getIntValue(jo, "uid", user.getId()); //计算的用户id
		
		StringBuffer sql = new StringBuffer();
		sql.append(" where create_user_id = " + uid + " and status = " +ConstantsUtil.STATUS_NORMAL);
		int count = 0;
		count =  SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, sql.toString()));
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查询用户ID为：", uid, "得到其评论总数是：", count, "条").toString(), "getCountByUser()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return count;
	}

	@Override
	public Map<String, Object> deleteComment(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CommentServiceImpl-->deleteComment():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		int cid = JsonUtil.getIntValue(jo, "cid");
		int createUserId = JsonUtil.getIntValue(jo, "create_user_id");
		
		//非登录用户不能删除操作
		if(createUserId != user.getId()){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先登录.value);
			return message;
		}
		
		if(cid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作实例.value);
			return message;
		}
		
		boolean result = false;
		CommentBean commentBean = commentMapper.findById(CommentBean.class, cid);
		if(commentBean != null && commentBean.getCreateUserId() == createUserId){
			result = commentMapper.deleteById(CommentBean.class, cid) > 0;
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作对象不存在.value);
		}
		
		if(result){
			commentHandler.deleteComment(commentBean.getTableId(), commentBean.getTableName());
			message.put("isSuccess", true);
		}			
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除评论ID为", cid, "的数据", StringUtil.getSuccessOrNoStr(result)).toString(), "deleteComment()", StringUtil.changeBooleanToInt(result), 0);
		return message;
	}

	@Override
	public int getTotalComments(int userId) {
		return SqlUtil.getTotalByList(commentMapper.getTotalByUser(DataTableType.评论.value, userId));
	}
	
	@Override
	public Map<String, Object> updateCommentStatus(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CommentServiceImpl-->updateCommentStatus():jo="+jo.toString());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id");
		boolean canComment = JsonUtil.getBooleanValue(jo, "can_comment", true);
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		
		int createUserId = SqlUtil.getCreateUserIdByList(commentMapper.getObjectCreateUserId(tableName, tableId));
		
		if(!checkAdmin(user, createUserId)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作权限.value);
			return message;
		}
		
		boolean result = commentMapper.updateSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(tableName)), " set can_comment=? where id=?", canComment, tableId) > 0;
		
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.更新评论状态成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.更新评论状态失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.更新评论状态失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"更新表名为：", tableName ,",表ID为：", tableId, "评论状态为", canComment, "，结果更新", StringUtil.getSuccessOrNoStr(result)).toString(), "updateCommentStatus()", StringUtil.changeBooleanToInt(result), 0);
		message.put("isSuccess", result);
		return message;
	}

	@Override
	public List<Map<String, Object>> executeSQL(String sql, Object... params) {
		return commentMapper.executeSQL(sql, params);
	}

	@Override
	public int getTotal(String tableName, String where) {
		return SqlUtil.getTotalByList(commentMapper.getTotal(tableName, where));
	}

	@Override
	public boolean save(CommentBean t) {
		return commentMapper.save(t) > 0;
	}
}
