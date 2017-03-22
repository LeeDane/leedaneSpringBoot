package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 签到service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:35:15
 * Version 1.0
 */
@Transactional("txManager")
public interface SignInService <T extends IDBean>{
	
	/**
	 * 用户指定时间是否已经签到
	 * @param userId 用户ID
	 * @param dateTime 指定的日期
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean isSign(int userId, String dateTime);
	
	/**
	 * 用户历史上是否有签到记录
	 * @param userId 用户ID
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean hasHistorySign(int userId);

	
	/**
	 * 获取数据库中最新的记录
	 * @param userId
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getNewestRecore(int userId);

	/**
	 * 保存(签到),当天已经签到的直接返回false
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public boolean saveSignIn(JSONObject jo, UserBean user,
			HttpServletRequest request);


	/**
	 * 获取签到的分页记录
	 * @param jo 格式"{'uid':1, 'pageSize':5,'timeScope':1, 'start_date': '2015-12-31', 'end_date':'2016-01-18'}"
	 * 注意：timeScope大于0时，start_date和end_date将不起作用
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getSignInByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request);
	
	
}
