package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
/**
 * 积分的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:35:07
 * Version 1.0
 */
@Transactional
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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTotalScore(long userId);

	/**
	 * 分页获得积分历史列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel getLimit(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取当前用户的总积分
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel getTotalScore(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 减少分数
	 * @param reduceScore
	 * @param desc
	 * @param tableName
	 * @param tableId
	 * @param user
	 * @return
	 */
	public ResponseModel reduceScore(int reduceScore, String desc, String tableName, long tableId, UserBean user);
	
}
