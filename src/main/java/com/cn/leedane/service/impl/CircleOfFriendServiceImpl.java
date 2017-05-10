package com.cn.leedane.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.handler.CircleOfFriendsHandler;
import com.cn.leedane.handler.CommentHandler;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.FanHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.handler.TransmitHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.ZanHandler;
import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.TimeLineBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.CircleOfFriendService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;
/**
 * 朋友圈service的实现类
 * @author LeeDane
 * 2016年7月12日 下午1:25:17
 * Version 1.0
 */
@Service("circleOfFriendService")
public class CircleOfFriendServiceImpl implements CircleOfFriendService<TimeLineBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private CircleOfFriendsHandler circleOfFriendsHandler;

	@Autowired
	private FriendHandler friendHandler;

	@Autowired
	private CommonHandler commonHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private MoodMapper moodMapper;
	
	@Autowired
	private CommentHandler commentHandler;
	
	@Autowired
	private TransmitHandler transmitHandler;
	
	@Autowired
	private ZanHandler zanHandler;
	
	public void setCommentHandler(CommentHandler commentHandler) {
		this.commentHandler = commentHandler;
	}
	
	public void setTransmitHandler(TransmitHandler transmitHandler) {
		this.transmitHandler = transmitHandler;
	}
	
	public void setZanHandler(ZanHandler zanHandler) {
		this.zanHandler = zanHandler;
	}
	
	@Resource
	private MoodHandler moodHandler;
	
	public void setMoodHandler(MoodHandler moodHandler) {
		this.moodHandler = moodHandler;
	}
	
	@Resource
	private FanHandler fanHandler;
	
	public void setFanHandler(FanHandler fanHandler) {
		this.fanHandler = fanHandler;
	}
	
	@Override
	public Map<String, Object> getLimit(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleOfFriendServiceImpl-->getLimit():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		/*String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		ResponseMap message = new ResponseMap();
		message.put("isSuccess", false);
		
		int start = 0;
		int end = 0;
		if(method.equalsIgnoreCase("firstloading")){
			start = 0;
			end = pageSize;
		//下刷新
		}else if(method.equalsIgnoreCase("lowloading")){
			
			start = lastId;
			end = lastId + pageSize -1;
		//上刷新
		}else if(method.equalsIgnoreCase("uploading")){
			start = 0;
			end = firstId + pageSize;
		}
		rs = circleOfFriendsHandler.getMyTimeLines(user.getId(), start, end);
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			//String account = "";
			String tabName;
			boolean hasSource;
			int tabId;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				hasSource = StringUtil.changeObjectToBoolean(rs.get(i).get("hasSource"));
				if(hasSource){
					tabName = StringUtil.changeNotNull(rs.get(i).get("tableName"));
					tabId = StringUtil.changeObjectToInt(rs.get(i).get("tableId"));
					if(StringUtil.isNotNull(tabName) && tabId >0){
						//在非获取指定表下的评论列表的情况下的前面35个字符
						rs.get(i).put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
					}
				}
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("createUserId"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				
			}	
		}
		message.put("isSuccess", true);
		message.put("message", rs);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取朋友圈列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();*/
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		String picSize = ConstantsUtil.DEFAULT_PIC_SIZE; //JsonUtil.getStringValue(jo, "pic_size"); //图像的规格(大小)		

		Set<String> fids = fanHandler.getMyAttentions(user.getId());
		if(fids == null)
			fids = new HashSet<String>();
		fids.add(String.valueOf(user.getId()));
		
		StringBuffer sql = new StringBuffer();
		ResponseMap message = new ResponseMap();
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select m.id table_id, '"+DataTableType.心情.value+"' table_name, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img,");
			sql.append(" m.read_number, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
			sql.append(buildCreateUserIdInSQL(fids));
			sql.append(" order by m.id desc limit 0,?");
			rs = moodMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select m.id table_id, '"+DataTableType.心情.value+"' table_name, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img,");
			sql.append(" m.read_number, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
			sql.append(buildCreateUserIdInSQL(fids));
			sql.append(" and m.id < ? order by m.id desc limit 0,? ");
			rs = moodMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select m.id table_id, '"+DataTableType.心情.value+"' table_name, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img,");
			sql.append(" m.read_number, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
			sql.append(buildCreateUserIdInSQL(fids));
			sql.append(" and m.id > ? limit 0,?  ");
			rs = moodMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
		}
		if(rs !=null && rs.size() > 0){
			JSONObject friendObject = friendHandler.getFromToFriends(user.getId());
			logger.info("friendObject："+friendObject.toString());
			int createUserId = 0;
			boolean hasImg ;
			String uuid;
			int moodId;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				hasImg = StringUtil.changeObjectToBoolean(rs.get(i).get("has_img"));
				uuid = StringUtil.changeNotNull(rs.get(i).get("uuid"));
				moodId = StringUtil.changeObjectToInt(rs.get(i).get("table_id"));
				if(createUserId> 0){
					if(createUserId != user.getId()){
						if(friendObject != null){
							
							rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId, user, friendObject));
						}else{
							rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
						}
					}else{
						rs.get(i).put("account", "本人");
					}
					rs.get(i).put("zan_users", zanHandler.getZanUser(moodId, DataTableType.心情.value, user, 6));
					rs.get(i).put("comment_number", commentHandler.getCommentNumber(moodId, DataTableType.心情.value));
					rs.get(i).put("transmit_number", transmitHandler.getTransmitNumber(moodId, DataTableType.心情.value));
					rs.get(i).put("zan_number", zanHandler.getZanNumber(moodId, DataTableType.心情.value));
				}
				
				//有图片的获取图片的路径
				if(hasImg && !StringUtil.isNull(uuid)){
					//logger.info("图片地："+moodHandler.getMoodImg(DataTableType.心情.value, uuid, picSize));
					rs.get(i).put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, picSize));
				}
			}	
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取朋友圈列表").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
				
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	/**
	 * 获取状态的SQL
	 * @param toUserId
	 * @param user
	 * @return
	 */
	private String getMoodStatusSQL(int toUserId, UserBean user){
		if(toUserId == user.getId()){
			return " (m.status = "+ ConstantsUtil.STATUS_NORMAL +" or m.status = "+ ConstantsUtil.STATUS_SELF +")";
		}else {
			return " m.status = "+ ConstantsUtil.STATUS_NORMAL;
		}
	}
	
	/**
	 * 构建CreateUser的SQL语句字符串
	 * @param ids
	 * @return
	 */
	private String buildCreateUserIdInSQL(Set<String> ids){
		int length = ids.size();
		if(length == 0) 
			return "";
		StringBuffer buffer = new StringBuffer();
		buffer.append(" m.create_user_id in (");
		int i = 0;
		for(String id: ids){
			if(i == length -1){
				buffer.append(id);
			}else{
				buffer.append(id + ",");
			}
			i++;
		}
		buffer.append(") ");
		
		return buffer.toString();
	}
	
}
