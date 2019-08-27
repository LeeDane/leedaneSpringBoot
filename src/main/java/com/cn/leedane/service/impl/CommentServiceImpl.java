package com.cn.leedane.service.impl;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.*;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.CommentMapper;
import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.*;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

	@Autowired
	private MoodMapper moodMapper;

	@Autowired
	private BlogMapper blogMapper;

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;

	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpRequestInfoBean request){
		// {\"table_name\":\""+DataTableType.心情.value+"\", \"table_id\":123
		//, \"content\":\"我同意\" ,\"level\": 1, \"pid\":23, \"from\":\"Android客户端\"}
		logger.info("CommentServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0);
		String content = JsonUtil.getStringValue(jo, "content");
		boolean check = JsonUtil.getBooleanValue(jo, "check", true);
		ResponseMap message = new ResponseMap();
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message, request))
			return message.getMap();
		
		if(StringUtil.isNull(tableName) || tableId < 1 || StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		
		//评论权限校验
		if(check && !checkComment(tableName, tableId, message)){
			return message.getMap();
		}
		
		int level = JsonUtil.getIntValue(jo, "level", 5);
		int pid = JsonUtil.getIntValue(jo, "pid", 0);
		CommentBean bean = new CommentBean();
		bean.setCommentLevel(level);
		bean.setContent(content);
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setPid(pid);
		String froms = request.getLocation();
		if(StringUtil.isNull(froms) || "未知".equalsIgnoreCase(froms)){
			froms = StringUtil.changeNotNull(JsonUtil.getStringValue(jo, "froms"));
		}
		bean.setFroms(froms);
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setTableId(tableId);
		bean.setTableName(tableName);
		if(pid > 0)
			bean.setLevel(commentMapper.getLevel(pid));
		//bean.setCid(cid);
		boolean result = commentMapper.save(bean) > 0;
		if(result){
			int createUserId = 0;
			//String str = "{from_user_remark}评论您："+content;
			if(pid > 0){//说明是回复别人的评论
				createUserId = getCommentCreateUserId(pid);			
			}else{ //说明评论的是某一个对象
				if(check)
					createUserId = SqlUtil.getCreateUserIdByList(commentMapper.getObjectCreateUserId(tableName, tableId));
				
			}
			if(createUserId > 0 && createUserId != user.getId()){
				if(StringUtil.isNotNull(tableName) && DataTableType.留言.value.equals(tableName)){
					notificationHandler.sendNotificationById(false, user, createUserId, content, NotificationType.留言, tableName, tableId, bean);
				}else{
					notificationHandler.sendNotificationById(false, user, createUserId, content, NotificationType.评论, tableName, tableId, bean);
				}
			}else{
				//对留言，将消息发送给对应的用户
				if(StringUtil.isNotNull(tableName) && DataTableType.留言.value.equals(tableName) && tableId > 0 && tableId != user.getId()){
					notificationHandler.sendNotificationById(false, user, tableId, content, NotificationType.留言, tableName, tableId, bean);
				}
				
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
		if(result){
			message.put("isSuccess", result);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.评论成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.评论失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.评论失败.value);
		}
		return message.getMap();
	}
	
	/**
	 * 检验评论是否可以进行下去
	 * @param tableName
	 * @param tableId
	 * @param message
	 */
	private boolean checkComment(String tableName, int tableId, Map<String, Object> message) {
		
		List<Map<String, Object>> list = commentMapper.executeSQL("select id, can_comment from "+tableName +" where id = ? limit 1", tableId);
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
	public Map<String, Object> rolling(JSONObject jo,
			UserBean user, HttpRequestInfoBean request){
		logger.info("CommentServiceImpl-->rolling():jsonObject=" +jo.toString());
		ResponseMap message = new ResponseMap();
		if(user == null)
			user = OptionUtil.adminUser;
		 //{\"uid\":2,\"table_name\":\"t_mood\", \"table_id\":123,\"pageSize\":5
		//, \"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //操作表名
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0); //操作表中的id
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int toUserId = JsonUtil.getIntValue(jo, "to_user_id"); //操作的对象用户的id
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		boolean showUserInfo = JsonUtil.getBooleanValue(jo, "showUserInfo");
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
	
		//查找该用户所有的转发
		if(StringUtil.isNull(tableName) && toUserId > 0){		
			if("firstloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id , c.level, c.stick  ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id");
				sql.append(" where c.create_user_id =? and c.status = ? ");
				sql.append(" order by c.id desc limit 0,?");
				rs = commentMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id , c.level, c.stick  ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id");
				sql.append(" where c.create_user_id =? and c.status = ? ");
				sql.append(" and c.id < ? order by c.id desc limit 0,? ");
				rs = commentMapper.executeSQL(sql.toString(), toUserId, ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id , c.level, c.stick ");
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
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id , c.level, c.stick  ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id");
				sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ?");
				sql.append(" order by c.id desc limit 0,?");
				rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, pageSize);
			//下刷新
			}else if("lowloading".equalsIgnoreCase(method)){			
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id , c.level, c.stick  ");
				sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
				sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ?");
				sql.append(" and c.id < ? order by c.id desc limit 0,?");
				rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			//上刷新
			}else if("uploading".equalsIgnoreCase(method)){
				sql.append("select c.id, c.pid, c.froms, c.content, c.table_id, c.table_name, c.create_user_id, u.account");
				sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id , c.level, c.stick ");
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
			String blockquoteAccount = ""; //@用户的名称
			String levelStr;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				Map<String, Object> comment = rs.get(i);
				if(StringUtil.isNull(tableName) && tableId <1){
					//在非获取指定表下的评论列表的情况下的前面35个字符
					tabName = StringUtil.changeNotNull((comment.get("table_name")));
					tabId = StringUtil.changeObjectToInt(comment.get("table_id"));
					comment.put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
				}else{
					pid = StringUtil.changeObjectToInt(comment.get("pid"));
					levelStr = StringUtil.changeNotNull(comment.get("level"));
					if(pid > 0 && StringUtil.isNotNull(levelStr)){
						Map<String, Object> items = new HashMap<>();
						comment.put("item", items);
						getLevelMap(items, StringUtil.changeObjectToInt(pid));
					}

					/*pid = StringUtil.changeObjectToInt(rs.get(i).get("pid"));
					*//*if(pid > 0 ){
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
					}*//*
					if(pid > 0 ){
						CommentBean pCommentBean = commentMapper.findById(CommentBean.class, pid);
						if(pCommentBean != null){
							pCreateUserId = pCommentBean.getCreateUserId();
							blockquoteAccount = "";
							if(pCreateUserId > 0){
								blockquoteAccount = userHandler.getUserName(pCreateUserId);
								if(StringUtil.isNotNull(blockquoteAccount)){
									*//*resultContent = StringUtil.changeNotNull((rs.get(i).get("content")));
									if(resultContent.indexOf("@"+atUsername) > 0 )
										rs.get(i).put("content", resultContent);
									else{
										rs.get(i).put("content", "回复@"+atUsername + " " + resultContent);
									}*//*
									rs.get(i).put("blockquote_account", blockquoteAccount); //引用的用户名称
								}
							}
							rs.get(i).put("blockquote_content", pCommentBean.getContent()); //引用的用户名称
							rs.get(i).put("blockquote_time", DateUtil.DateToString(pCommentBean.getCreateTime())); //引用的用户名称
						}else{
							rs.get(i).put("blockquote_content", "该评论已经被删除"); //引用的用户名称
						}
					}*/
				}
				if(showUserInfo){
					createUserId = StringUtil.changeObjectToInt(comment.get("create_user_id"));
					comment.putAll(userHandler.getBaseUserInfo(createUserId, user, friendObject));
				}
			}	
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取用户ID为：",toUserId,",表名：",tableName,"，表id为：",tableId,"的评论列表").toString(), "rolling()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> paging(JSONObject jo,
			UserBean user, HttpRequestInfoBean request){
		logger.info("CommentServiceImpl-->paging():jsonObject=" +jo.toString());
		ResponseMap message = new ResponseMap();
		if(user == null)
			user = new UserBean();
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //操作表名
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0); //操作表中的id
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int toUserId = JsonUtil.getIntValue(jo, "to_user_id"); //操作的对象用户的id
		boolean showUserInfo = JsonUtil.getBooleanValue(jo, "showUserInfo");
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int currentIndex = JsonUtil.getIntValue(jo, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jo, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		boolean formatTime = JsonUtil.getBooleanValue(jo, "format_time", false); //是否格式化时间
		//查找该用户所有的评论
		if(StringUtil.isNull(tableName) && toUserId > 0){
			rs = commentMapper.getAllByUser(toUserId, ConstantsUtil.STATUS_NORMAL, start, pageSize);
			message.put("total", SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, " c where c.create_user_id ="+ toUserId + " and status = "+ ConstantsUtil.STATUS_NORMAL)));
		}
				
		//查找指定表的数据
		if(StringUtil.isNotNull(tableName) && toUserId < 1 && tableId > 0){
			rs = commentMapper.getAllByTable(tableName, tableId, ConstantsUtil.STATUS_NORMAL, start, pageSize);
			message.put("total", SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, " c where c.table_name = '"+ tableName + "' and c.table_id ="+ tableId + " and status = "+ ConstantsUtil.STATUS_NORMAL)));
		}
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			
			//String account = "";
			String tabName;
			int tabId;
			int pid = 0;
			int pCreateUserId = 0;
			String blockquoteAccount = ""; //@用户的名称
			//为名字备注赋值comments
			for(int i = 0; i < rs.size(); i++){
				if(StringUtil.isNull(tableName) && tableId <1){
					//在非获取指定表下的评论列表的情况下的前面35个字符
					tabName = StringUtil.changeNotNull((rs.get(i).get("table_name")));
					tabId = StringUtil.changeObjectToInt(rs.get(i).get("table_id"));
					rs.get(i).put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
				}else{
					pid = StringUtil.changeObjectToInt(rs.get(i).get("pid"));
					if(pid > 0 ){
						CommentBean pCommentBean = commentMapper.findById(CommentBean.class, pid);
						if(pCommentBean != null){
							pCreateUserId = pCommentBean.getCreateUserId();
							blockquoteAccount = "";
							if(pCreateUserId > 0){
								blockquoteAccount = userHandler.getUserName(pCreateUserId);
								if(StringUtil.isNotNull(blockquoteAccount)){
									/*resultContent = StringUtil.changeNotNull((rs.get(i).get("content")));
									if(resultContent.indexOf("@"+atUsername) > 0 )
										rs.get(i).put("content", resultContent);
									else{
										rs.get(i).put("content", "回复@"+atUsername + " " + resultContent);
									}*/
									rs.get(i).put("blockquote_account", blockquoteAccount); //引用的用户名称
								}
							}
							rs.get(i).put("blockquote_content", pCommentBean.getContent()); //引用的用户名称
							if(formatTime){
								rs.get(i).put("blockquote_time", RelativeDateFormat.format(pCommentBean.getCreateTime())); //引用的用户名称
							}else{
								rs.get(i).put("blockquote_time", DateUtil.DateToString(pCommentBean.getCreateTime())); //引用的用户名称
							}
							
						}else{
							rs.get(i).put("blockquote_content", "该评论已经被删除"); //引用的用户名称
						}
					}
				}
				if(showUserInfo){
					createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId, user, friendObject));
				}
				
				//是否格式化时间
				if(formatTime){
					String createTime = (String) rs.get(i).get("create_time");
					rs.get(i).put("create_time", RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
				}
			}	
		}
		
		//保存操作日志
		//operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取用户ID为：",toUserId,",表名：",tableName,"，表id为：",tableId,"的评论列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}
	
	/**
	 * 构建CreateUser的SQL语句字符串
	 * @param
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
	public Map<String, Object> getOneCommentItemsByLimit(JSONObject jo,
			UserBean user, HttpRequestInfoBean request){
		logger.info("CommentServiceImpl-->getOneCommentItemsByLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		 //{\"table_name\":\"t_mood\", \"table_id\":123
		//, \"first_id\": 2, \"last_id\":2, \"method\":\"firstloading\"}
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int cid = JsonUtil.getIntValue(jo, "cid");
		if(cid < 1){
			message.put("message", "评论ID为空");
			return message.getMap();
		}
		
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //操作表名
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0); //操作表中的id
		int pageSize = JsonUtil.getIntValue(jo, "pageSize"); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id", 0); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		StringBuffer sql = new StringBuffer();
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select c.id, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
			sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id, c.pid, c.stick  ");
			sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
			sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ? and c.cid = ? ");
			sql.append(" order by c.id desc");
			sql.append(getLimitSql(pageSize));
			rs = commentMapper.executeSQL(sql.toString()
					, tableName, tableId, ConstantsUtil.STATUS_NORMAL, cid);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){			
			sql.append("select c.id, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
			sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id, c.pid, c.stick  ");
			sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
			sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ? and c.cid = ? ");
			sql.append(" and c.id < ? order by c.id desc");
			sql.append(getLimitSql(pageSize));
			rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, 
					tableName, tableId, ConstantsUtil.STATUS_NORMAL, lastId, cid);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select c.id, c.content, c.table_id, c.table_name, c.create_user_id, u.account ");
			sql.append(", date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time,c.comment_level, c.table_id, c.pid, c.stick  ");
			sql.append("  from "+DataTableType.评论.value+" c inner join "+DataTableType.用户.value+" u on u.id = c.create_user_id ");
			sql.append(" where c.table_name = ? and c.table_id = ? and c.status = ? and c.pid = ? ");
			sql.append(" and c.id > ? ");
			sql.append(getLimitSql(pageSize));
			rs = commentMapper.executeSQL(sql.toString(), tableName, tableId, ConstantsUtil.STATUS_NORMAL, 
					tableName, tableId, ConstantsUtil.STATUS_NORMAL, firstId, cid);
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查看表：",tableName,"，表id为：",tableId,"，评论ID为：", cid, "的评论详情列表").toString(), "getOneCommentItemsByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
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
	public Map<String, Object> getCountByObject(JSONObject jo, UserBean user,
			HttpRequestInfoBean request){
		logger.info("CommentServiceImpl-->getCountByObject():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //操作表名
		int tableId = JsonUtil.getIntValue(jo, "table_id", 0); //操作表中的id
		
		StringBuffer sql = new StringBuffer();
		sql.append("where table_name = '" + tableName +"'");
		sql.append(" and table_id = '" + tableId +"'");
		sql.append(" and status = " +ConstantsUtil.STATUS_NORMAL);
		int count = 0;
		count = SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, sql.toString()));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查看表：",tableName,"，表id为：",tableId,"，查询得到的总数是：", count, "条").toString(), "getCountByObject()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("message", count);
		return message.getMap();
	}

	@Override
	public Map<String, Object> getCountByUser(JSONObject jo, UserBean user,
			HttpRequestInfoBean request){
		logger.info("CommentServiceImpl-->getCountByUser():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		int uid = JsonUtil.getIntValue(jo, "uid", user.getId()); //计算的用户id
		
		StringBuffer sql = new StringBuffer();
		sql.append(" where create_user_id = " + uid + " and status = " +ConstantsUtil.STATUS_NORMAL);
		int count = 0;
		count =  SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, sql.toString()));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查询用户ID为：", uid, "得到其评论总数是：", count, "条").toString(), "getCountByUser()", ConstantsUtil.STATUS_NORMAL, 0);
				
		message.put("isSuccess", true);
		message.put("message", count);
		return message.getMap();
	}

	@Override
	public Map<String, Object> deleteComment(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CommentServiceImpl-->deleteComment():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		int cid = JsonUtil.getIntValue(jo, "cid");
		int createUserId = JsonUtil.getIntValue(jo, "create_user_id");
		
		if(cid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		boolean result = false;
		CommentBean commentBean = commentMapper.findById(CommentBean.class, cid);
		if(commentBean != null && commentBean.getCreateUserId() == createUserId){
//			result = commentMapper.deleteById(CommentBean.class, cid) > 0;
			result = commentMapper.updateSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(DataTableType.评论.value)), " set status = ? where id = ? ", ConstantsUtil.STATUS_DELETE, cid) > 0;
		}else{
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		}
		
		if(result){
			commentHandler.deleteComment(cid);
			commentHandler.deleteComment(commentBean.getTableId(), commentBean.getTableName());
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除评论成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除评论失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除评论失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除评论ID为", cid, "的数据", StringUtil.getSuccessOrNoStr(result)).toString(), "deleteComment()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public int getTotalComments(int userId) {
		return SqlUtil.getTotalByList(commentMapper.getTotalByUser(DataTableType.评论.value, userId));
	}
	
	@Override
	public Map<String, Object> updateCommentStatus(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CommentServiceImpl-->updateCommentStatus():jo="+jo.toString());
		String tableName = JsonUtil.getStringValue(jo, "table_name");
		int tableId = JsonUtil.getIntValue(jo, "table_id");
		boolean canComment = JsonUtil.getBooleanValue(jo, "can_comment", true);
		ResponseMap message = new ResponseMap();
		
		int createUserId = SqlUtil.getCreateUserIdByList(commentMapper.getObjectCreateUserId(tableName, tableId));
		
		checkAdmin(user, createUserId);
		
		boolean result = commentMapper.updateSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(tableName)), " set can_comment=? where id=?", canComment, tableId) > 0;
		
		if(result){
			if(DataTableType.心情.value.equalsIgnoreCase(tableName)){
				//获取心情对象
				MoodBean moodBean = moodMapper.findById(MoodBean.class, tableId);
				//删除es缓存
				elasticSearchUtil.delete(DataTableType.心情.value, tableId);
				//重新加进缓存
				new ThreadUtil().singleTask(new EsIndexAddThread<MoodBean>(moodBean));
			}else if(DataTableType.博客.value.equalsIgnoreCase(tableName)){
				//获取心情对象
				BlogBean blogBean = blogMapper.findById(BlogBean.class, tableId);
				//删除es缓存
				elasticSearchUtil.delete(DataTableType.博客.value, tableId);
				//重新加进缓存
				new ThreadUtil().singleTask(new EsIndexAddThread<BlogBean>(blogBean));
			}

			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.更新评论状态成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.更新评论状态失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.更新评论状态失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"更新表名为：", tableName ,",表ID为：", tableId, "评论状态为", canComment, "，结果更新", StringUtil.getSuccessOrNoStr(result)).toString(), "updateCommentStatus()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		message.put("isSuccess", result);
		return message.getMap();
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
	
	@Override
	public Map<String, Object> getMessageBoards(int userId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CommentServiceImpl-->getMessageBoards():userId="+ userId +",jo="+jo.toString());
		
		if(userId < 1)
			userId = user.getId();
		
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jo, "current", 0); //每页的大小
		int total = JsonUtil.getIntValue(jo, "total", 0); //每页的大小
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<Map<String, Object>> rs = commentMapper.getMessageBoards(userId, ConstantsUtil.STATUS_NORMAL, start, pageSize);
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			int pid = 0;
			int pCreateUserId = 0;
			String blockquoteAccount; //引用的用户名称
			String levelStr;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				Map<String, Object> comment = rs.get(i);
				pid = StringUtil.changeObjectToInt(comment.get("pid"));
				levelStr = StringUtil.changeNotNull(comment.get("level"));
				if(pid > 0 && StringUtil.isNotNull(levelStr)){
//					String[] levels = levelStr.split("\\|");
					Map<String, Object> items = new HashMap<>();
					comment.put("item", items);
					getLevelMap(items, StringUtil.changeObjectToInt(pid));

					/*CommentBean pCommentBean = commentMapper.findById(CommentBean.class, pid);
					if(pCommentBean != null){
						pCreateUserId = pCommentBean.getCreateUserId();
						blockquoteAccount = "";
						if(pCreateUserId > 0){
							blockquoteAccount = userHandler.getUserName(pCreateUserId);
							if(StringUtil.isNotNull(blockquoteAccount)){
								comment.put("blockquote_account", blockquoteAccount); //引用的用户名称
							}
						}
						comment.put("blockquote_content", pCommentBean.getContent()); //引用的内容
						comment.put("blockquote_time", DateUtil.DateToString(pCommentBean.getCreateTime())); //引用的时间
					}else{
						comment.put("blockquote_content", "该评论已经被删除"); //引用的用户名称
					}*/
				}
				createUserId = StringUtil.changeObjectToInt(comment.get("create_user_id"));
				comment.putAll(userHandler.getBaseUserInfo(createUserId, user, friendObject));
			}
		}
		message.put("total", SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, "where table_name='"+ DataTableType.留言.value +"' and table_id='"+ userId +"' and status="+ ConstantsUtil.STATUS_NORMAL)));
		message.put("isSuccess", true);
		message.put("message", rs);
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"分页获取留言板列表", ",表ID为：", userId, StringUtil.getSuccessOrNoStr(true)).toString(), "getMessageBoards()", 1, 0);
		return message.getMap();
	}

	private Map<String, Object> getLevelMap(Map<String, Object> items, int id){
		CommentBean commentBean = commentHandler.getComment(id);
		if(commentBean == null)
			return items;
		if(commentBean.getStatus() == ConstantsUtil.STATUS_DELETE){
			items.put("source", "该内容已经被删除");
			items.put("id", -1);
		}else{
			items.put("source", commentBean.getContent());
			items.put("id", commentBean.getId());
		}
		items.put("user_id", commentBean.getCreateUserId());
		items.put("time", DateUtil.DateToString(commentBean.getCreateTime()));
		items.put("account", userHandler.getUserName(commentBean.getCreateUserId()));
		if(commentBean.getPid() > 0 && StringUtil.isNotNull(commentBean.getLevel())){
			Map<String, Object> sItems = new HashMap<>();
			items.put("item", sItems);
			return getLevelMap(sItems, commentBean.getPid());
		}
		return items;
	}
}
