package com.cn.leedane.service.impl.circle;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.circle.CircleClockInMapper;
import com.cn.leedane.mapper.circle.CircleContributionMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleClockInBean;
import com.cn.leedane.model.circle.CircleContributionBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.circle.CircleClockInService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.PlatformType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 圈子打卡service实现类
 * @author LeeDane
 * 2017年6月14日 下午4:29:44
 * version 1.0
 */
@Service("circleClockInService")
public class CircleClockInServiceImpl extends AdminRoleCheckService implements CircleClockInService<CircleClockInBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CircleClockInMapper circleClockInMapper;
	
	@Autowired
	private CircleContributionMapper circleContributionMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public Map<String, Object> isClockIn(UserBean user, int circleId,
			Date dateTime) {
		
		ResponseMap message = new ResponseMap();
		//判断当天是否已经打卡
		List<CircleClockInBean> circleClockInBeans = circleClockInMapper.getClockInBean(circleId, user.getId(), ConstantsUtil.STATUS_NORMAL, DateUtil.DateToString(dateTime, "yyyy-MM-dd"));
		if(CollectionUtil.isNotEmpty(circleClockInBeans)){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.今天已经打卡.value));
			message.put("responseCode", EnumUtil.ResponseCode.今天已经打卡.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.今天还未打卡.value));
			message.put("responseCode", EnumUtil.ResponseCode.今天还未打卡.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> saveClockIn(int circleId, JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleClockInServiceImpl-->saveClockIn(), user=" +user.getAccount()+", jo="+jo +", circleId="+ circleId);
		ResponseMap message = new ResponseMap();		
		Date currentTime = new Date();
		
		//判断当天是否已经打卡
		List<CircleClockInBean> circleClockInBeans = circleClockInMapper.getClockInBean(circleId, user.getId(), ConstantsUtil.STATUS_NORMAL, DateUtil.DateToString(currentTime, "yyyy-MM-dd"));
		if(CollectionUtil.isNotEmpty(circleClockInBeans)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.今天已经打卡.value));
			message.put("responseCode", EnumUtil.ResponseCode.今天已经打卡.value);
			return message.getMap();
		}
		
		CircleClockInBean circleClockInBean = new CircleClockInBean();
		circleClockInBean.setCreateUserId(user.getId());
		circleClockInBean.setCreateTime(currentTime);
		circleClockInBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		circleClockInBean.setCircleId(circleId);
		circleClockInBean.setFroms(JsonUtil.getStringValue(jo, "froms", PlatformType.网页版.value));
		//获取打卡前的贡献值
		int yesterDayTotal = SqlUtil.getTotalByList(circleContributionMapper.getTotalScore(circleId, user.getId()));
		//获取昨天是否有打卡
		Date yesterDayTime = DateUtil.getYestoday();
		List<CircleClockInBean> circleYesterdayClockInBeans = circleClockInMapper.getClockInBean(circleId, user.getId(), ConstantsUtil.STATUS_NORMAL, DateUtil.DateToString(yesterDayTime, "yyyy-MM-dd"));
		if(CollectionUtil.isNotEmpty(circleYesterdayClockInBeans)){
			circleClockInBean.setContinuous(circleYesterdayClockInBeans.get(0).getContinuous() + 1);
			circleClockInBean.setPid(circleYesterdayClockInBeans.get(0).getId());  //上一条记录的id
		}else{
			circleClockInBean.setContinuous(1);
			circleClockInBean.setPid(0);  //上一条记录的id
		}
		
		boolean result = circleClockInMapper.save(circleClockInBean) > 0 ;
		if(result){
			CircleContributionBean contributionBean = new CircleContributionBean();
			int preScore = StringUtil.getScoreBySignin(circleClockInBean.getContinuous() - 1);
			contributionBean.setScore(preScore);
			contributionBean.setTotalScore(yesterDayTotal + preScore);
			contributionBean.setCreateUserId(user.getId());
			contributionBean.setCreateTime(currentTime);
			contributionBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			contributionBean.setScoreDesc("打卡得贡献值");
			contributionBean.setCircleId(circleId);
			result = circleContributionMapper.save(contributionBean) > 0;
			if(result){
				message.put("isSuccess", true);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.贡献打卡成功.value));
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
			}
			
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		// 保存操作日志信息
		String subject = user.getAccount()+"打卡， 圈子id=" + circleId;
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "saveClockIn()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		
		return null;
	}
}
