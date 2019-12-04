package com.cn.leedane.service.impl.clock;

import com.cn.leedane.display.clock.ClockDynamicDisplay;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.clock.ClockHandler;
import com.cn.leedane.handler.clock.ClockMemberHandler;
import com.cn.leedane.mapper.clock.ClockDynamicMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockDynamicBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.clock.ClockDynamicService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务打卡service实现类
 * @author LeeDane
 * 2018年8月29日 下午5:34:53
 * version 1.0
 */
@Service("clockDynamicService")
public class ClockDynamicServiceImpl extends AdminRoleCheckService implements ClockDynamicService<ClockDynamicBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ClockDynamicMapper clockDynamicMapper;
	
	@Autowired
	private ClockMapper clockMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private ClockHandler clockHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private ClockMemberHandler clockMemberHandler;
	
	@Override
	public Map<String, Object> dynamics(long clockId, JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("ClockDynamicServiceImpl-->dynamics():jo="+jsonObject.toString());
		
		//检验任务是否存在
		ClockBean clock = clockHandler.getNormalClock(clockId);
		
		//检验是否在成员列表中
		if(!clockMemberHandler.inMember(user.getId(), clockId))
			throw new UnauthorizedException("您还未加入该任务，无法查看动态");
		
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jsonObject, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jsonObject, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jsonObject, "total", 0); //总数
		boolean publicity = true;
		if(clock.getCreateUserId() == user.getId()){
			publicity = JsonUtil.getBooleanValue(jsonObject, "publicity", true);
		}
		
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<ClockDynamicBean> dynamicBeans = clockDynamicMapper.dynamics(clockId, publicity, start, pageSize);
//		message.put("total", circleMapper.getAllCircles(user.getId(), ConstantsUtil.STATUS_NORMAL).size());
		
		List<ClockDynamicDisplay> dynamicDisplays = new ArrayList<ClockDynamicDisplay>();
		if(CollectionUtil.isNotEmpty(dynamicBeans)){
			for(ClockDynamicBean dynamicBean: dynamicBeans){
				ClockDynamicDisplay display = new ClockDynamicDisplay();
				long createUserId = dynamicBean.getCreateUserId();
				display.setAccount(userHandler.getUserName(createUserId));
				display.setClockId(clockId);
				display.setCreateTime(DateUtil.DateToString(dynamicBean.getCreateTime()));
				display.setCreateUserId(createUserId);
				display.setDesc(dynamicBean.getDynamicDesc());
				display.setId(dynamicBean.getId());
				display.setModifyTime(DateUtil.DateToString(dynamicBean.getModifyTime()));
				display.setPicPath(userHandler.getUserPicPath(createUserId, "30x30"));
				display.setStatus(dynamicBean.getStatus());
				display.setMessageType(dynamicBean.getMessageType());
				display.setSeeEachOther(clock.isSeeEachOther());
				dynamicDisplays.add(display);
			}
		}
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取任务动态列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", dynamicDisplays);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		return message.getMap();
	}
}
