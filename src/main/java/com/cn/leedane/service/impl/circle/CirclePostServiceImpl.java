package com.cn.leedane.service.impl.circle;
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

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CommentHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.ZanHandler;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CirclePostHandler;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.ZanBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CircleMemberBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.ZanService;
import com.cn.leedane.service.circle.CirclePostService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.RelativeDateFormat;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

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
	private CirclePostHandler circlePostHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private CommentHandler commentHandler;
	
	@Autowired
	private ZanHandler zanHandler;

	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private CommentService<CommentBean> commentService;
	
	@Autowired
	private ZanService<ZanBean> zanService;
	
	@Override
	public Map<String, Object> add(int circleId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CirclePostServiceImpl-->add(), circleId= " + circleId +",user=" +user.getAccount());
		SqlUtil sqlUtil = new SqlUtil();
		CirclePostBean circlePostBean = (com.cn.leedane.model.circle.CirclePostBean) sqlUtil.getBean(json, CirclePostBean.class);
		
		CircleBean circleBean = circleHandler.getCircleBean(circleId);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(circlePostBean.getTitle()) || StringUtil.isNull(circlePostBean.getContent())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		Date createTime = new Date();
		
		circlePostBean.setCreateTime(createTime);
		circlePostBean.setCreateUserId(user.getId());
		circlePostBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		
		boolean result = circlePostMapper.save(circlePostBean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", "您的帖子已经发布成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleBean.getName(), "发布帖子，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(int circleId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CirclePostServiceImpl-->update(), circleId= " + circleId +",user=" +user.getAccount());
		
		ResponseMap message = new ResponseMap();
		int postId = JsonUtil.getIntValue(json, "post_id", 0);
		if(postId < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		CircleBean circleBean = circleHandler.getCircleBean(circleId);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		CirclePostBean oldPostBean = circlePostHandler.getCirclePostBean(circleBean, postId, user);
		if(oldPostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		Date createTime = new Date();
		SqlUtil sqlUtil = new SqlUtil();
		CirclePostBean circlePostBean = (CirclePostBean) sqlUtil.getUpdateBean(json, oldPostBean);
		circlePostBean.setModifyUserId(user.getId());
		circlePostBean.setModifyTime(createTime);
		
		boolean result = circlePostMapper.update(circlePostBean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", "您的帖子已经更新成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleBean.getName(), "修改帖子, 帖子Id为", postId, "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(int circleId, JSONObject json,
			UserBean user, HttpServletRequest request) {
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
				map.put("create_time", RelativeDateFormat.format(DateUtil.stringToDate(StringUtil.changeNotNull(map.get("create_time")))));
				map.put("zan_users", zanHandler.getZanUser(postId, DataTableType.帖子.value, user, 6));
				map.put("comment_number", commentHandler.getCommentNumber(postId, DataTableType.帖子.value));
				map.put("transmit_number", circlePostHandler.getTransmitNumber(postId));
				map.put("zan_number", zanHandler.getZanNumber(postId, DataTableType.帖子.value));
				map.putAll(userHandler.getBaseUserInfo(createUserId));
				pid = StringUtil.changeObjectToInt(map.get("pid"));
				if(pid > 0){
					CirclePostBean postBean = circlePostHandler.getCirclePostBean(pid);
					String blockquoteContent = "该帖子已被删除！";
					int blockCreateUserId = 0;
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
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看圈子id为"+circleId+"的帖子", "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> comment(int circleId, int postId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CirclePostServiceImpl-->comment(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean oldCirclePostBean = circlePostHandler.getCirclePostBean(circleId, postId);
		if(oldCirclePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		
		return commentService.add(json, user, request);
	}
	
	@Override
	public Map<String, Object> transmit(int circleId, int postId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CirclePostServiceImpl-->transmit(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean oldCirclePostBean = circlePostHandler.getCirclePostBean(circleId, postId);
		if(oldCirclePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		
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
		
		boolean result = circlePostMapper.save(circlePostBean) > 0;
		if(result){
			circlePostHandler.addTransmit(postId);
			message.put("isSuccess", true);
			message.put("message", "您已成功转发帖子！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleId, "帖子为", postId, "转发，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "transmit()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}

	@Override
	public Map<String, Object> zan(int circleId, int postId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CirclePostServiceImpl-->zan(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean oldCirclePostBean = circlePostHandler.getCirclePostBean(circleId, postId);
		if(oldCirclePostBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//校验是否加入圈子
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		
		return zanService.addZan(json, user, request);
	}
	
	@Override
	public Map<String, Object> delete(int circleId, int postId, JSONObject json, UserBean user, HttpServletRequest request) {
		logger.info("CirclePostServiceImpl-->delete(), postId= " + postId +",user=" +user.getAccount());
		CirclePostBean circlePostBean = circlePostHandler.getCirclePostBean(circleId, postId);
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
		
		int createUserId = circlePostBean.getCreateUserId();
		String reason = JsonUtil.getStringValue(json, "reason", "没有原因");
		String content = "您的帖子《"+ circlePostBean.getTitle() +"》被管理者：\""+ user.getAccount() +"\" 删除, 原因是："+ reason;
		boolean result = circlePostMapper.delete(circlePostBean) > 0;
		if(result){
			
			//删除父帖子的转发数
			if(circlePostBean.getPid() > 0)
				circlePostHandler.deleteTransmit(circlePostBean.getPid());
			//非自己的帖子，管理员/圈子删除的，将通知用户(这里用不存在的表目的的查询通知的时候不去获取源数据)
			notificationHandler.sendNotificationById(false, user, createUserId, content, NotificationType.通知, DataTableType.不存在的表.value, postId, null);
			
			message.put("isSuccess", true);
			message.put("message", "您已成功删除帖子！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为圈子为", circleId, "帖子为", postId, "转发，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "transmit()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}
}
