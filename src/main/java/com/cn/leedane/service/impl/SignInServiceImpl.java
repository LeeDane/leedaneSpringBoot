package com.cn.leedane.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.handler.SignInHandler;
import com.cn.leedane.mapper.SignInMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.SignInBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.ScoreService;
import com.cn.leedane.service.SignInService;

/**
 * 签到service实现类
 * @author LeeDane
 * 2016年7月12日 下午2:09:36
 * Version 1.0
 */
@Service("signInService")
public class SignInServiceImpl implements SignInService<SignInBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private SignInMapper signInMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private ScoreService<ScoreBean> scoreService;
	
	@Autowired
	private SignInHandler signInHandler;
	
	/**
	 * 系统的缓存对象
	 */
	@Autowired
	private SystemCache systemCache;
	
	@Resource
	public void setOperateLogService(
			OperateLogService<OperateLogBean> operateLogService) {
		this.operateLogService = operateLogService;
	}
	
	@Override
	public boolean isSign(int userId, String dateTime) {
		return SqlUtil.getBooleanByList(this.signInMapper.isSign(userId, dateTime));
	}


	@Override
	public List<Map<String, Object>> getNewestRecore(int userId) {
		return this.signInMapper.getNewestRecore(userId);
	}

	@Override
	public boolean hasHistorySign(int userId) {	
		return SqlUtil.getBooleanByList(this.signInMapper.hasHistorySign(userId));
	}
	
	@Override
	public boolean saveSignIn(JSONObject jo, UserBean user,
			HttpServletRequest request){
		
		int uid = user.getId();
		String dateTime = DateUtil.DateToString(new Date(), "yyyy-MM-dd");
		
		SignInBean signInBean = new SignInBean();
		ScoreBean scoreBean = new ScoreBean();
		//获取用户当前总积分
		int score = 0;
		int continuous = 0;
		boolean hasHistorySign = signInHandler.hasHistorySign(uid);
		//第一次签到
		if(!hasHistorySign){
			signInBean.setContinuous(1);
			signInBean.setPid(0);  //上一条记录的id
			Object object = systemCache.getCache("first-sign-in"); 
			if(object != null){
				score = StringUtil.changeObjectToInt(object);
			}
			
		}else{
			if(isSign(uid, dateTime)) return false;
			
			score = scoreService.getTotalScore(user.getId());
			List<Map<String, Object>> yesTodayRecore = signInMapper.getYesTodayRecore(uid, new Date(), ConstantsUtil.STATUS_NORMAL);
		
			//昨天有签到记录的情况
			if(yesTodayRecore != null && yesTodayRecore.size() > 0){
				//只获取第一条
				Map<String, Object> recore = yesTodayRecore.get(0);
				continuous = StringUtil.changeObjectToInt(recore.get("continuous"));
				signInBean.setContinuous(continuous + 1);  //连续签到的天数
				signInBean.setPid(StringUtil.changeObjectToInt(recore.get("id")));  //上一条记录的id
				
			}else{
				signInBean.setContinuous(1);
				signInBean.setPid(0);  //上一条记录的id
			}
		}
		
		Date currentTime = new Date();
		
		signInBean.setCreateTime(currentTime);
		signInBean.setCreateDate(DateUtil.DateToString(currentTime, "yyyy-MM-dd"));
		signInBean.setCreateUserId(user.getId());
		signInBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		
		boolean isSave = signInMapper.save(signInBean) > 0;
		if(isSave){
			scoreBean.setTotalScore(StringUtil.getScoreBySignin(continuous, score));
			scoreBean.setScore(StringUtil.getScoreBySignin(continuous));
			scoreBean.setCreateTime(currentTime);
			scoreBean.setCreateUserId(user.getId());
			scoreBean.setScoreDesc("用户签到");
			scoreBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			scoreBean.setTableId(signInBean.getId());
			scoreBean.setTableName(DataTableType.签到.value);
			isSave = scoreService.save(scoreBean);
			//标记为已经添加
			if(isSave && !hasHistorySign){
				signInHandler.addHistorySignIn(uid);
			}
		}
		
		// 保存操作日志信息
		String subject = user.getAccount()+"签到" + StringUtil.getSuccessOrNoStr(isSave);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "saveSignIn", StringUtil.changeBooleanToInt(isSave), 0);
		return isSave;
	}

	@Override
	public List<Map<String, Object>> getSignInByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request) {
		long start = System.currentTimeMillis();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int uid = JsonUtil.getIntValue(jo, "uid", user.getId()); //操作的用户的id
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", 0); //每页的大小
		String start_date_str = JsonUtil.getStringValue(jo, "start_date"); //开始的页数
		String end_date_str = JsonUtil.getStringValue(jo, "end_date"); //结束的页数
		int timeScope = JsonUtil.getIntValue(jo, "timeScope", 0); //操作方式
		
		Date startDate, endDate;
		
		if(timeScope > 0){
			startDate = DateUtil.getBeginTime(EnumUtil.getTimeScope(String.valueOf(timeScope)));
			endDate = DateUtil.getEndTime(EnumUtil.getTimeScope(String.valueOf(timeScope)));
		}else{
			startDate = DateUtil.stringToDate(start_date_str, "yyyy-MM-dd");
			endDate = DateUtil.stringToDate(end_date_str, "yyyy-MM-dd");
		}

		logger.info("SignInServiceImpl-->getSignInByLimit():jo=" +jo.toString());
		System.out.println("获取签到历史记录：开始时间："+DateUtil.DateToString(startDate, "yyyy-MM-dd") +",结束时间：" +DateUtil.DateToString(endDate, "yyyy-MM-dd"));
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id, s.pid, s.score, s.create_user_id");
		sql.append(" , date_format(s.create_time,'%Y-%m-%d %H:%i:%s') create_time, s.continuous");
		sql.append(" from "+DataTableType.签到.value+" s inner join "+DataTableType.用户.value+" u on u.id = s.create_user_id where s.create_user_id = ? and s.status = ? and DATE(s.create_time) between ? and ? ");
		sql.append(" order by s.id desc");
		sql.append(getLimitSQL(pageSize));
		rs = signInMapper.executeSQL(sql.toString(), uid, ConstantsUtil.STATUS_NORMAL, startDate, endDate);
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看签到列表", "getSignInByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		long end = System.currentTimeMillis();
		System.out.println("获取签到列表总计耗时：" +(end - start) +"毫秒");
		return rs;
	}	
	
	/**
	 * 获取分页SQL语句
	 * @param pageSize
	 * @return
	 */
	private String getLimitSQL(int pageSize){
		if(pageSize < 1)
			return "";
		
		return " limit 0,"+pageSize +" ";
	}
}
