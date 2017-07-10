package com.cn.leedane.service.impl.circle;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CircleSettingHandler;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.mapper.circle.CircleSettingMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CircleMemberBean;
import com.cn.leedane.model.circle.CircleSettingBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.circle.CircleSettingService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 圈子设置service实现类
 * @author LeeDane
 * 2017年7月6日 下午5:16:12
 * version 1.0
 */
@Service("circleSettingService")
public class CircleSettingServiceImpl extends AdminRoleCheckService implements CircleSettingService<CircleSettingBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CircleSettingMapper circleSettingMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private CircleMemberMapper circleMemberMapper;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private CircleHandler circleHandler;
	
	@Autowired
	private CircleSettingHandler circleSettingHandler;
	
	@Autowired
	private UserHandler userHandler;

	@Override
	public Map<String, Object> update(int circleId, int settingId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleSettingServiceImpl-->paging():jo="+json.toString());
		ResponseMap message = new ResponseMap();
		
		CircleBean circle = circleHandler.getNormalCircleBean(circleId);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));

		//标记用户是否有修改权限
    	boolean canUpdate = false;
		if(user != null){
			List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
			if(CollectionUtil.isNotEmpty(members)){
				int roleType = members.get(0).getRoleType();
				canUpdate = roleType == CircleServiceImpl.CIRCLE_CREATER || roleType == CircleServiceImpl.CIRCLE_MANAGER;
			}
		}
		
		if(!canUpdate)
			throw new UnauthenticatedException();
		SqlUtil sqlUtil = new SqlUtil();
		CircleSettingBean settingBean = (CircleSettingBean) sqlUtil.getUpdateBean(json, circleSettingHandler.getNormalSettingBean(circleId));
		
		if(settingBean == null || (settingBean.getCircleId() != circleId))
			throw new UnauthenticatedException();
		
		settingBean.setModifyTime(DateUtil.getCurrentTime());
		settingBean.setModifyUserId(user.getId());
		boolean result = circleSettingMapper.update(settingBean) > 0 ;
		if(result){
			//删除缓存
			circleSettingHandler.deleteSettingBeanCache(circleId);
			
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改圈子id为"+ circleId+"的设置").toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);		
		
		return message.getMap();
	}
}
