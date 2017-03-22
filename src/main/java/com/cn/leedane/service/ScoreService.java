package com.cn.leedane.service;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.UserBean;
/**
 * 积分的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:35:07
 * Version 1.0
 */
@Transactional("txManager")
public interface ScoreService<T extends IDBean>{
	
	/**
	 * 基础的保存实体的方法
	 * @param t
	 * @return
	 */
	public boolean save(ScoreBean t);
	
	/**
	 * 获取当前用户的总积分
	 * @param userId
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTotalScore(int userId);

	/**
	 * 分页获得积分历史列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getLimit(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取当前用户的总积分
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getTotalScore(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 减少分数
	 * @param reduceScore
	 * @param desc
	 * @param tableName
	 * @param tableId
	 * @param user
	 * @return
	 */
	public Map<String, Object> reduceScore(int reduceScore, String desc, String tableName, int tableId, UserBean user);
	
}
