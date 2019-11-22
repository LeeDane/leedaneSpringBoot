package com.cn.leedane.service.impl.circle;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.*;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CirclePostHandler;
import com.cn.leedane.handler.circle.CircleSettingHandler;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.model.circle.*;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.AddReadSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.service.*;
import com.cn.leedane.service.circle.CircleContributionService;
import com.cn.leedane.service.circle.CirclePostService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 帖子service实现类
 * @author LeeDane
 * 2017年6月20日 下午6:09:27
 * version 1.0
 */
@Service("circlePostService")
public class CirclePostServiceImpl extends AdminRoleCheckService implements CirclePostService<CirclePostBean>{
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CircleMemberMapper circleMemberMapper;
	
	@Autowired
	private CirclePostMapper circlePostMapper;
	
	@Autowired
	private CircleMapper circleMapper;
	
	@Autowired
	private CircleHandler circleHandler;
	
	@Autowired
	private CircleSettingHandler circleSettingHandler;
	
	@Autowired
	private CirclePostHandler circlePostHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private CommentHandler commentHandler;
	
	@Autowired
	private ZanHandler zanHandler;

	@Autowired
	private ReadHandler readHandler;

	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private CommentService<CommentBean> commentService;
	
	@Autowired
	private ZanService<ZanBean> zanService;
	
	@Autowired
	private CircleContributionService<CircleContributionBean> circleContributionService;
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Override
	public Map<String, Object> add(long circleId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->add(), circleId= " + circleId +",user=" +user.getAccount());
		SqlUtil sqlUtil = new SqlUtil();
		CirclePostBean circlePostBean = (CirclePostBean) sqlUtil.getBean(json, CirclePostBean.class);
		
		CircleBean circleBean = circleHandler.getNormalCircleBean(circleId, user);
		
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(circlePostBean.getTitle())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		if(StringUtil.isNull(circlePostBean.getContent()))
			circlePostBean.setContent(circlePostBean.getTitle());
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));

		String returnMsg = "您的帖子已经发布成功！";
		//判断用户的身份
		CircleMemberBean memberBean = members.get(0);
		CircleSettingBean settingBean = circleSettingHandler.getNormalSettingBean(circleId, user);
		if(circlePostBean.getPid() == 0 && settingBean.isCheckPost() && memberBean.getRoleType() == CircleServiceImpl.CIRCLE_NORMAL){
			circlePostBean.setStatus(ConstantsUtil.STATUS_AUDIT);
			returnMsg = returnMsg + "请等待圈主/圈子管理员审核！";
		}else
			circlePostBean.setStatus(ConstantsUtil.STATUS_NORMAL);

		Date createTime = new Date();
		String content = circlePostBean.getContent();
		//取180个作为摘要
		circlePostBean.setDigest(JsoupUtil.getInstance().getDigest(content, 0, 180));
		circlePostBean.setContent(MardownUtil.parseHtml(content));
		circlePostBean.setCreateTime(createTime);
		circlePostBean.setCreateUserId(user.getId());
		
		
		boolean result = circlePostMapper.save(circlePostBean) > 0;
		if(result){
			
			//先清空一下响应的用户和帖子绑定的缓存
			circlePostHandler.deleteUserCirclePosts(user.getId());
			circlePostHandler.deleteUserPostPosts(circleId, user.getId());
			
			//对用户添加贡献值
			circleContributionService.addScore(5, "发帖子《"+ circlePostBean.getTitle() +"》奖励贡献值", circleId, user);
			
			//保存帖子的访问记录
	        saveVisitLog(circlePostBean.getId(), user, request);
	        
	        //判断是否需要审核的帖子
	        
	        
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleBean.getName(), "发布帖子，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(long circleId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->update(), circleId= " + circleId +",user=" +user.getAccount());
		
		ResponseMap message = new ResponseMap();
		int postId = JsonUtil.getIntValue(json, "post_id", 0);
		if(postId < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		CircleBean circleBean = circleHandler.getNormalCircleBean(circleId, user);
		
		CirclePostBean oldPostBean = circlePostHandler.getNormalCirclePostBean(circleBean, postId, user);
		if(oldPostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		Date createTime = new Date();
		SqlUtil sqlUtil = new SqlUtil();
		CirclePostBean circlePostBean = (CirclePostBean) sqlUtil.getUpdateBean(json, oldPostBean);
		circlePostBean.setModifyUserId(user.getId());
		circlePostBean.setModifyTime(createTime);
		//取180个作为摘要
		circlePostBean.setDigest(JsoupUtil.getInstance().getDigest(circlePostBean.getContent(), 0, 180));
		boolean result = circlePostMapper.update(circlePostBean) > 0;
		if(result){
			//先清空一下响应的用户和帖子绑定的缓存
			circlePostHandler.deleteUserCirclePosts(user.getId());
			circlePostHandler.deleteUserPostPosts(circleId, user.getId());
			//清空该帖子详情的缓存
			circlePostHandler.deletePostBeanCache(postId);
			message.put("isSuccess", true);
			message.put("message", "您的帖子已经更新成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleBean.getName(), "修改帖子, 帖子Id为", postId, "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(long circleId, JSONObject json,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->paging():json=" +json.toString());
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		ResponseMap message = new ResponseMap();
		rs = circlePostMapper.paging(circleId, start, pageSize, ConstantsUtil.STATUS_NORMAL);
		if(CollectionUtil.isNotEmpty(rs)){
			int postId; 
			int createUserId;
			int pid;
			//为名字备注赋值
			for(Map<String, Object> map: rs){
				postId = StringUtil.changeObjectToInt(map.get("id"));
				createUserId = StringUtil.changeObjectToInt(map.get("create_user_id"));
				map.put("create_time", map.get("create_time"));
				map.put("zan_users", zanHandler.getZanUser(postId, DataTableType.帖子.value, user, 6));
				map.put("comment_number", commentHandler.getCommentNumber(DataTableType.帖子.value, postId));
				map.put("transmit_number", circlePostHandler.getTransmitNumber(postId));
				map.put("zan_number", zanHandler.getZanNumber(DataTableType.帖子.value, postId));
				map.putAll(userHandler.getBaseUserInfo(createUserId));
				pid = StringUtil.changeObjectToInt(map.get("pid"));
				if(pid > 0){
					CirclePostBean postBean = circlePostHandler.getNormalCirclePostBean(pid);
					String blockquoteContent = "该帖子已被删除！";
					long blockCreateUserId = 0;
					map.put("blockquote", false);
					if(postBean != null){
						blockquoteContent = postBean.getTitle();
						blockCreateUserId = postBean.getCreateUserId();
						map.put("blockquote", true);
						map.put("blockquote_account", userHandler.getUserName(blockCreateUserId));
						map.put("blockquote_time", RelativeDateFormat.format(postBean.getCreateTime()));
					}
					map.put("blockquote_content", blockquoteContent);
				}
			}	
		}
		message.put("total", SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, " m where circle_id="+ circleId)));

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看圈子id为"+circleId+"的帖子", "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> comment(long circleId, long postId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->comment(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean oldCirclePostBean = circlePostHandler.getNormalCirclePostBean(circleId, postId);
		if(oldCirclePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members)){
			ResponseMap message = new ResponseMap();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先加入该圈子.value);
			return message.getMap();
		}
		
		Map<String, Object> results = commentService.add(json, user, request);
		if(results != null && results.containsKey("isSuccess") && StringUtil.changeObjectToBoolean(results.get("isSuccess"))){
			//删除热门帖子
			circlePostHandler.deleteHotestPosts();
			
			//对评论帖子添加贡献值
			circleContributionService.addScore(1, "评论帖子《"+ oldCirclePostBean.getTitle() +"》奖励贡献值", circleId, user);
		}
		//保存帖子的访问记录
        saveVisitLog(postId, user, request);
		return results;
	}
	
	@Override
	public Map<String, Object> transmit(long circleId, long postId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->transmit(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean oldCirclePostBean = circlePostHandler.getNormalCirclePostBean(circleId, postId);
		if(oldCirclePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members)){
			ResponseMap message = new ResponseMap();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先加入该圈子.value);
			return message.getMap();
		}
		
		ResponseMap message = new ResponseMap();
		
		SqlUtil sqlUtil = new SqlUtil();
		CirclePostBean circlePostBean = (CirclePostBean) sqlUtil.getBean(json, CirclePostBean.class);
		if(StringUtil.isNull(circlePostBean.getTitle()) || StringUtil.isNull(circlePostBean.getContent())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		Date createTime = new Date();
		circlePostBean.setPid(postId);
		circlePostBean.setCircleId(oldCirclePostBean.getCircleId()); //设置圈子id
		circlePostBean.setCreateTime(createTime);
		circlePostBean.setCreateUserId(user.getId());
		circlePostBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		circlePostBean.setCanComment(true);
		circlePostBean.setCanTransmit(true);
		circlePostBean.setDigest(circlePostBean.getContent());
		
		boolean result = circlePostMapper.save(circlePostBean) > 0;
		if(result){
			circlePostHandler.addTransmit(postId);
			
			//对转发帖子添加贡献值
			circleContributionService.addScore(1, "转发帖子《"+ circlePostBean.getTitle() +"》奖励贡献值", circleId, user);
			//删除热门帖子
			circlePostHandler.deleteHotestPosts();
			
			message.put("isSuccess", true);
			message.put("message", "您已成功转发帖子！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleId, "帖子为", postId, "转发，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "transmit()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		//保存帖子的访问记录
        saveVisitLog(postId, user, request);
		return message.getMap();
	}

	@Override
	public Map<String, Object> zan(long circleId, long postId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->zan(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean oldCirclePostBean = circlePostHandler.getNormalCirclePostBean(circleId, postId);
		if(oldCirclePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members)){
			ResponseMap message = new ResponseMap();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先加入该圈子.value);
			return message.getMap();
		}
		
		Map<String, Object> results = zanService.addZan(json, user, request);
		if(results != null && results.containsKey("isSuccess") && StringUtil.changeObjectToBoolean(results.get("isSuccess"))){
			//对点赞帖子添加贡献值
			circleContributionService.addScore(1, "点赞帖子《"+ oldCirclePostBean.getTitle() +"》奖励贡献值", circleId, user);
			
			//删除热门帖子
			circlePostHandler.deleteHotestPosts();
		}
		
		//保存帖子的访问记录
        saveVisitLog(postId, user, request);
		return results;
	}
	
	@Override
	public Map<String, Object> delete(long circleId, long postId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->delete(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean circlePostBean = circlePostHandler.getNormalCirclePostBean(circleId, postId);
		if(circlePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		
		//判断用户的身份
		CircleMemberBean memberBean = members.get(0);
		if(memberBean.getMemberId() != user.getId() && memberBean.getRoleType() == CircleServiceImpl.CIRCLE_MANAGER)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		
		ResponseMap message = new ResponseMap();

		long createUserId = circlePostBean.getCreateUserId();
		String reason = JsonUtil.getStringValue(json, "reason", "没有原因");
		String content = "您的帖子《"+ circlePostBean.getTitle() +"》被管理者：\""+ user.getAccount() +"\" 删除, 原因是："+ reason;
		boolean result = circlePostMapper.deleteById(CirclePostBean.class, circlePostBean.getId()) > 0;
		if(result){
			//先清空一下响应的用户和帖子绑定的缓存
			circlePostHandler.deleteUserCirclePosts(user.getId());
			circlePostHandler.deleteUserPostPosts(circleId, user.getId());
			//清空该帖子的详情
			circlePostHandler.deletePostBeanCache(postId);
			//删除热门帖子
			circlePostHandler.deleteHotestPosts();
			//删除父帖子的转发数
			if(circlePostBean.getPid() > 0)
				circlePostHandler.deleteTransmit(circlePostBean.getPid());
			//非自己的帖子，管理员/圈子删除的，将通知用户(这里用不存在的表目的的查询通知的时候不去获取源数据)
			notificationHandler.sendNotificationById(false, user, createUserId, content, NotificationType.通知, DataTableType.不存在的表.value, postId, null);
			
			//对删除帖子减少贡献值
			if(circlePostBean.getCreateUserId() == user.getId())
				circleContributionService.reduceScore(5, "删除帖子《"+ circlePostBean.getTitle() +"》扣除贡献值", circleId, user);
			
			//删除帖子访问记录
			visitorService.deleteVisitor(user, DataTableType.帖子.value, postId);
	        
			message.put("isSuccess", true);
			message.put("message", "您已成功删除帖子！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleId, "帖子为", postId, "转发，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> initDetail(CircleBean circle, CirclePostBean post,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->initDetail(), user=" +(user != null ? user.getAccount(): "用户还未登录"));
		ResponseMap message = new ResponseMap();
		message.put("circle", circle);
		message.put("post", post);
		long postId = post.getId();
		//把更新读的信息提交到Rabbitmq队列处理
		new Thread(new Runnable() {
			@Override
			public void run() {
				ReadBean readBean = new ReadBean();
				readBean.setTableName(DataTableType.帖子.value);
				readBean.setFroms(request.getIp());
				readBean.setTableId(postId);
				readBean.setCreateTime(new Date());
				readBean.setCreateUserId(user == null ? -1 : user.getId());
				readBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				ISend send = new AddReadSend(readBean);
				SendMessage sendMessage = new SendMessage(send);
				sendMessage.sendMsg();
			}
		}).start();
		//标记用户是否有删除权限
    	boolean canDelete = false;
		if(user != null){
			canDelete = post.getCreateUserId() == user.getId();
			if(!canDelete){
				List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circle.getId(), ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isNotEmpty(members)){
					int roleType = members.get(0).getRoleType();
					canDelete = roleType == CircleServiceImpl.CIRCLE_CREATER || roleType == CircleServiceImpl.CIRCLE_MANAGER;
				}
			}
		}
    	message.put("canDelete", canDelete);
		

		message.put("create_time", DateUtil.DateToString(post.getCreateTime()));
		message.put("zan_users", zanHandler.getZanUser(postId, DataTableType.帖子.value, user, 6));
		message.put("comment_number", commentHandler.getCommentNumber(DataTableType.帖子.value, postId));
		message.put("transmit_number", circlePostHandler.getTransmitNumber(postId));
		message.put("zan_number", zanHandler.getZanNumber(DataTableType.帖子.value, postId));
		message.put("read_number", readHandler.getReadNumber(DataTableType.帖子.value, postId));
		//message.putAll(userHandler.getBaseUserInfo(post.getCreateUserId()));
		message.put("post_create_user_account", userHandler.getUserName(post.getCreateUserId()));
		message.put("post_create_user_pic_path", userHandler.getUserPicPath(post.getCreateUserId(), "30x30"));
		long pid = post.getPid();
		if(pid > 0){
			CirclePostBean postBean = circlePostHandler.getNormalCirclePostBean(pid);
			String blockquoteContent = "该帖子已被删除！";
			long blockCreateUserId = 0;
			message.put("blockquote", false);
			if(postBean != null){
				blockquoteContent = postBean.getTitle();
				blockCreateUserId = postBean.getCreateUserId();
				message.put("blockquote", true);
				message.put("blockquote_account", userHandler.getUserName(blockCreateUserId));
				message.put("blockquote_time", RelativeDateFormat.format(postBean.getCreateTime()));
			}
			message.put("blockquote_content", blockquoteContent);
		}
		message.put("setting", circleSettingHandler.getNormalSettingBean(circle.getId(), user));
		return message;
	}

	@Override
	public void saveVisitLog(long postId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->saveVisitLog() , postId= "+ postId +", --" + (user == null ? "" : user.getAccount()));
		
		visitorService.saveVisitor(user, "web网页端", DataTableType.帖子.value, postId, ConstantsUtil.STATUS_NORMAL);
	}
	
	@Override
	public Map<String, Object> noCheckTotal(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->noCheckTotal(), circleId= " + circleId +",user=" +user.getAccount());
		circleHandler.getNormalCircleBean(circleId, user);
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		
		//判断用户的身份
		CircleMemberBean memberBean = members.get(0);
		if(memberBean.getRoleType() == CircleServiceImpl.CIRCLE_NORMAL)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		
		ResponseMap message = new ResponseMap();
		message.put("message", SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, " m where circle_id="+ circleId +" and status="+ ConstantsUtil.STATUS_AUDIT)));
		message.put("isSuccess", true);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> noCheckList(long circleId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->noCheckList(), circleId= " + circleId +",user=" +user.getAccount());
		circleHandler.getNormalCircleBean(circleId, user);
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		
		//判断用户的身份
		CircleMemberBean memberBean = members.get(0);
		if(memberBean.getRoleType() == CircleServiceImpl.CIRCLE_NORMAL)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(json, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		
		List<Map<String, Object>> rs = circlePostMapper.paging(circleId, start, pageSize, ConstantsUtil.STATUS_AUDIT);
		if(CollectionUtil.isNotEmpty(rs)){
			int createUserId;
			//为名字备注赋值
			for(Map<String, Object> map: rs){
				createUserId = StringUtil.changeObjectToInt(map.get("create_user_id"));
				map.put("create_time", RelativeDateFormat.format(DateUtil.stringToDate(StringUtil.changeNotNull(map.get("create_time")))));
				map.putAll(userHandler.getBaseUserInfo(createUserId));
			}	
		}
		message.put("total", SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, " m where circle_id="+ circleId +" and status="+ ConstantsUtil.STATUS_AUDIT)));
		message.put("message", rs);
		message.put("isSuccess", true);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> check(long circleId, long postId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("CirclePostServiceImpl-->check(), circleId= " + circleId +", postId= "+ postId +",user=" +user.getAccount());
		CirclePostBean circlePostBean = circlePostHandler.getCirclePostBean(postId, user);
		if(circlePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		
		//判断用户的身份
		CircleMemberBean memberBean = members.get(0);
		if(memberBean.getRoleType() == CircleServiceImpl.CIRCLE_NORMAL)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		
		ResponseMap message = new ResponseMap();
		
		int status = JsonUtil.getIntValue(json, "status", ConstantsUtil.STATUS_DELETE);
		circlePostBean.setStatus(status);
		circlePostBean.setModifyTime(DateUtil.getCurrentTime());
		circlePostBean.setModifyUserId(user.getId());
		
		boolean result = false;
		if(status == ConstantsUtil.STATUS_DELETE){
			result = circlePostMapper.deleteById(CirclePostBean.class, postId) > 0;
		}else{
			result = circlePostMapper.update(circlePostBean) > 0;
		}
		
		String reason = JsonUtil.getStringValue(json, "reason", "");
		if(StringUtil.isNotNull(reason))
			reason = ", 原因是："+ reason;
		
		if(result){
			//清除该圈子的缓存
			circlePostHandler.deletePostBeanCache(postId);
			if(circlePostBean.getCreateUserId() != user.getId()){
				if(status == ConstantsUtil.STATUS_DELETE){
					//发送通知给用户
					notificationHandler.sendNotificationById(false, user, circlePostBean.getCreateUserId(), "您的帖子《"+ circlePostBean.getTitle() +"》已经被审核，结果是：不通过" + reason, NotificationType.通知, DataTableType.不存在的表.value, 0, null);
				}else{
					//发送通知给用户
					notificationHandler.sendNotificationById(false, user, circlePostBean.getCreateUserId(), "您的帖子《"+ circlePostBean.getTitle() +"》已经被审核，结果是：通过" + reason, NotificationType.通知, DataTableType.帖子.value, postId, null);
				}
			}
			
			message.put("isSuccess", true);
			message.put("message", "审核结果："+ StringUtil.getStatusText(status));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleId, "帖子为", postId, "审核，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "check()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
}
