package com.cn.leedane.service.impl.circle;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.circle.CircleHandle;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.MemberMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.MemberBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 圈子service实现类
 * @author LeeDane
 * 2017年5月30日 下午8:13:59
 * version 1.0
 */
@Service("circleService")
public class CircleServiceImpl extends AdminRoleCheckService implements CircleService<CircleBean>{
	Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 圈子创建者状态
	 */
	public static final int CIRCLE_CREATER = 1;
	
	/**
	 * 圈子管理者状态
	 */
	public static final int CIRCLE_MANAGER = 1;
	
	/**
	 * 圈子普通成员状态
	 */
	public static final int CIRCLE_NORMAL = 1;
	
	@Autowired
	private CircleMapper circleMapper;
	
	@Autowired
	private MemberMapper memberMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private CircleHandle circleHandle;


	@Override
	public Map<String, Object> create(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->create():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		String name = JsonUtil.getStringValue(jo, "name"); //圈子的名称
		String describe = JsonUtil.getStringValue(jo, "describe");
		
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(name)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.圈子名称不能为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.圈子名称不能为空.value);
			return message.getMap();
		}
		
		Date createTime = new Date();
		
		CircleBean circleBean = new CircleBean();
		circleBean.setCreateTime(createTime);
		circleBean.setCreateUserId(user.getId());
		circleBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		circleBean.setDescribe(describe);
		
		boolean result = circleMapper.save(circleBean) > 0;
		if(result){
			//添加一条成员记录
			MemberBean member = new MemberBean();
			member.setCreateTime(createTime);
			member.setCreateUserId(user.getId());
			member.setCircleId(circleBean.getId());
			member.setMemberId(user.getId());
			member.setRoleType(CIRCLE_CREATER);
			member.setStatus(ConstantsUtil.STATUS_NORMAL);
			
			result = memberMapper.save(member) > 0;
			if(result){
				message.put("isSuccess", true);
				message.put("message", "您已成功创建名称为《"+ name +"》的圈子。");
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
			}
			
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"创建名称为：", name,"的圈子，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "create()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		int cid = JsonUtil.getIntValue(jo, "cid", 0);
		if(cid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();
		}
		CircleBean circleBean = circleMapper.findById(CircleBean.class, cid);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		//判断是否是管理员或者圈主
		if(circleBean.getCreateUserId() != user.getId() && circleHandle.getRoleCode(user, cid) != CIRCLE_MANAGER)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		
		//自动填充更新的bean
		SqlUtil sqlUtil = new SqlUtil();
		sqlUtil.getUpdateBean(jo, circleBean);
		circleBean.setModifyTime(new Date());
		circleBean.setModifyUserId(user.getId());
		
		boolean result = circleMapper.save(circleBean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int cid, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->delete():cid=" +cid +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		CircleBean circleBean = circleMapper.findById(CircleBean.class, cid);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		//判断是否是管理员或者圈主
		if(circleBean.getCreateUserId() != user.getId() && circleHandle.getRoleCode(user, cid) != CIRCLE_MANAGER)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));

		//这里是逻辑删除
		circleBean.setStatus(ConstantsUtil.STATUS_DELETE);
		boolean result = circleMapper.update(circleBean) > 0;
		if(result){
			//通知所有的成员该圈子已经被删除
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		return message.getMap();
	}
	
	
}
