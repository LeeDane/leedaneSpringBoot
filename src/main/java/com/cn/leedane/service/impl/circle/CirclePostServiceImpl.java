package com.cn.leedane.service.impl.circle;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CirclePostHandler;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.circle.CirclePostService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
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
	Logger logger = Logger.getLogger(getClass());
	
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
}
