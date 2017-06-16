package com.cn.leedane.service.impl.circle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.circle.CircleContributionMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleContributionBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.circle.CircleContributionService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
/**
 * 贡献值service的实现类
 * @author LeeDane
 * 2017年6月14日 下午5:27:44
 * version 1.0
 */
@Service("circleContributionService")
public class CircleContributionServiceImpl implements CircleContributionService<CircleContributionBean>{
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private CircleContributionMapper circleContributionMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public int getTotalScore(int userId) {
		return SqlUtil.getTotalByList(circleContributionMapper.executeSQL("select sum(t.score) ct from t_score t where create_user_id=?", userId));
	}

	@Override
	public Map<String, Object> paging(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleContributionServiceImpl-->paging():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		StringBuffer sql = new StringBuffer();
		
		ResponseMap message = new ResponseMap();
		//只有登录用户才能
		/*if(user == null){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先登录.value);
	        return message;
		}*/
		
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		//查找该用户所有的贡献值历史列表(该用户必须是登录用户)
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select s.id, s.score_desc, s.total_score, s.score, s.status, date_format(s.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.贡献值.value+" s where s.create_user_id = ? ");
			sql.append(" order by s.id desc limit 0,?");
			rs = circleContributionMapper.executeSQL(sql.toString(), user.getId(), pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select s.id, s.score_desc, s.total_score, s.score, s.status, date_format(s.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.贡献值.value+" s where s.create_user_id = ? ");
			sql.append(" and s.id < ? order by s.id desc limit 0,? ");
			rs = circleContributionMapper.executeSQL(sql.toString(), user.getId(), lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select s.id, s.score_desc, s.total_score, s.score, s.status, date_format(s.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" from "+DataTableType.贡献值.value+" s where s.create_user_id = ? ");
			sql.append(" and s.id > ? limit 0,?  ");
			rs = circleContributionMapper.executeSQL(sql.toString() , user.getId(), firstId, pageSize);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取贡献值记录").toString(), "getLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();		
	}

	@Override
	public Map<String, Object> getTotalScore(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleContributionServiceImpl-->getTotalScore():jsonObject=" +jo.toString() +", user=" +user.getAccount()); 		
		ResponseMap message = new ResponseMap();
		int score = getTotalScore(user.getId());
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取总贡献值").toString(), "getTotalScore()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", score);
		message.put("isSuccess", true);
		return message.getMap();		
	}

	@Override
	public Map<String, Object> reduceScore(int reduceScore, String desc, UserBean user) {
		logger.info("CircleContributionServiceImpl-->reduceScore():user=" +user.getAccount()); 		
		
		ResponseMap message = new ResponseMap();
		int sc = getTotalScore(user.getId());
		CircleContributionBean contributionBean = new CircleContributionBean();
		contributionBean.setTotalScore(sc - reduceScore);
		contributionBean.setScore(-reduceScore);
		contributionBean.setCreateTime(new Date());
		contributionBean.setCreateUserId(user.getId());
		contributionBean.setScoreDesc(desc);
		contributionBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		boolean result = circleContributionMapper.save(contributionBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, null, null, StringUtil.getStringBufferStr(user.getAccount(),"扣除贡献值").toString(), "reduceScore()", ConstantsUtil.STATUS_NORMAL, 0);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请求返回成功码.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		message.put("isSuccess", result);
		return message.getMap();
	}
}
