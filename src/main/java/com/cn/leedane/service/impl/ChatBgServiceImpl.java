package com.cn.leedane.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.ChatBgUserHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.ChatBgMapper;
import com.cn.leedane.mapper.ChatBgUserMapper;
import com.cn.leedane.model.ChatBgBean;
import com.cn.leedane.model.ChatBgUserBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.ChatBgService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.ScoreService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 聊天背景相关service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:21:43
 * Version 1.0
 */
@Service("chatBgService")
public class ChatBgServiceImpl implements ChatBgService<ChatBgBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private ChatBgMapper chatBgMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private ScoreService<ScoreBean> scoreService;	
	
	@Autowired
	private ChatBgUserHandler chatBgUserHandler;

	
	@Autowired
	private ChatBgUserMapper chatBgUserMapper;
	

	@Override
	public Map<String, Object> paging(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatBgServiceImpl-->paging():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		
		int lastId = JsonUtil.getIntValue(jo, "last_id", 0); //开始的页数
		int type = JsonUtil.getIntValue(jo, "type", 0); //聊天背景的类型，0：免费,1:收费, 2:全部
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //开始的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
	
		if("firstloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select c.id, c.create_user_id, c.path, c.chat_bg_desc, c.type, c.score, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.聊天背景.value+" c");
			sql.append(" where c.status=? ");
			sql.append(buildChatBgTypeSql(type));
			sql.append(" order by c.id desc limit 0,?");
			rs = chatBgMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select c.id, c.create_user_id, c.path, c.chat_bg_desc, c.type, c.score, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.聊天背景.value+" c");
			sql.append(" where c.status=? ");
			sql.append(buildChatBgTypeSql(type));
			sql.append(" and c.id < ? order by c.id desc limit 0,? ");
			rs = chatBgMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql = new StringBuffer();
			sql.append("select c.id, c.create_user_id, c.path, c.chat_bg_desc, c.type, c.score, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.聊天背景.value+" c");
			sql.append(" where c.status=? ");
			sql.append(buildChatBgTypeSql(type));
			sql.append(" and c.id > ? limit 0,? ");
			rs = chatBgMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
		}
		
		//判断是否已经下载过
		if(rs != null && rs.size() >0){
			for(int i = 0; i < rs.size();i++){
				rs.get(i).put("download", chatBgUserHandler.isDownload(user.getId(), StringUtil.changeObjectToInt(rs.get(i).get("id"))));
				rs.get(i).putAll(userHandler.getBaseUserInfo(StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"))));
			}
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取聊天背景分页列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
			
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}

	/**
	 * 构建聊天背景颜色类型SQL
	 * @param type
	 * @return
	 */
	private String buildChatBgTypeSql(int type) {
		String sql = "";
		switch(type){
			case 0: //免费
				sql = " and c.type = 0 ";
				break;
			case 1:  //收费
				sql = " and c.type = 1 ";
				break;
			case 2://全部
				sql = " and c.type in(0, 1) ";
				break;
		}
		return sql;
	}


	@Override
	public Map<String, Object> publish(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatBgServiceImpl-->send():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		String desc = JsonUtil.getStringValue(jo, "desc"); //聊天背景的描述
		String path = JsonUtil.getStringValue(jo, "path"); //聊天背景的路径	
		int type = JsonUtil.getIntValue(jo, "type", 0); //聊天背景的类型
		int score = JsonUtil.getIntValue(jo, "score", 0); //聊天背景的扣除的分数
		
		if(type == 1 && score < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请填写每次用户每次下载扣取的积分.value));
			message.put("responseCode", EnumUtil.ResponseCode.请填写每次用户每次下载扣取的积分.value);
			return message.getMap();
		}
		
		//检查是否有数据存在
		if(this.chatBgMapper.executeSQL("select id from "+DataTableType.聊天背景.value+" where create_user_id = ? and path=?", user.getId(), path).size() > 0 ){
			message.put("message", "您已发布过该聊天背景，请勿重复发布！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();
		}
		
		ChatBgBean chatBgBean = new ChatBgBean();
		chatBgBean.setChatBgDesc(desc);
		chatBgBean.setCreateTime(new Date());
		chatBgBean.setCreateUserId(user.getId());
		chatBgBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		chatBgBean.setPath(path);
		chatBgBean.setType(type);
		chatBgBean.setScore(score);
		
		boolean result = chatBgMapper.save(chatBgBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(), "发布聊天背景到平台", StringUtil.getSuccessOrNoStr(result)).toString(), "send()", ConstantsUtil.STATUS_NORMAL, 0);
		if(result){
			message.put("isSuccess", result);
			message.put("message", "发布聊天背景成功");
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		return message.getMap();
	}


	@Override
	public Map<String, Object> addChatBg(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("ChatBgServiceImpl-->verifyChatBg():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		int cid = JsonUtil.getIntValue(jo, "cid", 0); //聊天背景的ID
		
		ChatBgBean chatBg = null;
		if(cid < 1 || (chatBg = chatBgMapper.findById(ChatBgBean.class, cid)) == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		
		if(chatBg.getCreateUserId() == user.getId()){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.自己上传的聊天背景资源.value));
			message.put("isSuccess", true);
			return message.getMap();
		}
		
		RedisUtil redisUtil = RedisUtil.getInstance();
		
		String chatBgUserKey = ChatBgUserHandler.getChatBgUserKey(user.getId(), cid);
		boolean result = false;
		//还没有缓存记录
		if(!redisUtil.hasKey(chatBgUserKey)){
			System.out.println("没有缓存");
			boolean e = SqlUtil.getBooleanByList(chatBgUserMapper.hasDownload(user.getId(), cid));
			if(!e){//还没有下载记录
				redisUtil.addString(chatBgUserKey, "true");
				//保存下载记录
				ChatBgUserBean t = new ChatBgUserBean();
				t.setChatBgTableId(chatBg.getId());
				t.setCreateTime(new Date());
				t.setCreateUserId(user.getId());
				t.setStatus(ConstantsUtil.STATUS_NORMAL);
				result = chatBgUserMapper.save(t) > 0;
				if(chatBg.getType() == 0){
					message.put("message", "这个是免费的资源，不需要扣下载积分");
					message.put("isSuccess", true);
					return message.getMap();
				}
				result = saveScore(chatBg, user);
			}else{
				redisUtil.addString(chatBgUserKey, "false");
				message.put("message", "已经下载过该资源");
				message.put("isSuccess", true);
				return message.getMap();
			}
		}else{
			String val = redisUtil.getString(chatBgUserKey);
			System.out.println("已经缓存："+val);
			if(StringUtil.isNotNull(val)){
				System.out.println("缓存："+StringUtil.changeObjectToBoolean(val));
				if(!StringUtil.changeObjectToBoolean(val)){
					redisUtil.addString(chatBgUserKey, "true");
					//保存下载记录
					ChatBgUserBean t = new ChatBgUserBean();
					t.setChatBgTableId(chatBg.getId());
					t.setCreateTime(new Date());
					t.setCreateUserId(user.getId());
					t.setStatus(ConstantsUtil.STATUS_NORMAL);
					result = chatBgUserMapper.save(t) > 0;
					if(chatBg.getType() == 0){
						message.put("message", "这个是免费的资源，不需要扣下载积分");
						message.put("isSuccess", true);
						return message.getMap();
					}
					result = saveScore(chatBg, user);
				}else{
					message.put("message", "已经下载过该资源");
					message.put("isSuccess", true);
					return message.getMap();
				}
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
				message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
				return message.getMap();
			}
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(), "下载聊天背景的资源", StringUtil.getSuccessOrNoStr(result)).toString(), "verifyChatBg()", ConstantsUtil.STATUS_NORMAL, 0);
		if(result){
			message.put("isSuccess", result);
			message.put("message", "扣除下载积分成功!");
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		return message.getMap();
	}
	
	private boolean saveScore(ChatBgBean chatBg, UserBean user){
		boolean result = false;
		Date d = new Date();  //共用时间
		int bgScore = chatBg.getScore();  //下载需要处理的积分
		//扣除用户下载积分
		ScoreBean scoreBean = new ScoreBean();
		int score = 0 - bgScore;
		scoreBean.setTotalScore(scoreService.getTotalScore(user.getId()) + score);
		scoreBean.setScore(score);
		scoreBean.setCreateTime(d);
		scoreBean.setCreateUserId(user.getId());
		scoreBean.setScoreDesc("扣除下载聊天背景积分");
		scoreBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		scoreBean.setTableId(chatBg.getId());
		scoreBean.setTableName(DataTableType.聊天背景.value);
		result = scoreService.save(scoreBean);
		if(result){
			//增加用户下载资源积分
			ScoreBean scoreBean1 = new ScoreBean();
			scoreBean1.setTotalScore(scoreService.getTotalScore(chatBg.getCreateUserId()) + bgScore);
			scoreBean1.setScore(bgScore);
			scoreBean1.setCreateTime(d);
			scoreBean1.setCreateUserId(chatBg.getCreateUserId());
			scoreBean1.setScoreDesc("聊天背景资源被下载奖励");
			scoreBean1.setStatus(ConstantsUtil.STATUS_NORMAL);
			scoreBean1.setTableId(chatBg.getId());
			scoreBean1.setTableName(DataTableType.聊天背景.value);
			result = scoreService.save(scoreBean1);
			
			if(result){
				//发送通知给相应的用户
				String content = user.getAccount() +"下载您的聊天背景资源，您获得"+bgScore +"积分";
				notificationHandler.sendNotificationById(false, user, chatBg.getCreateUserId(), content, NotificationType.通知, DataTableType.积分.value, scoreBean1.getId(), null);
			}
		}
		return result;
	}

}
