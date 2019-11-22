package com.cn.leedane.service.stock;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 股票购买记录service接口类
 * @author LeeDane
 * 2018年6月22日 下午5:36:35
 * version 1.0
 */
@Transactional
public interface StockBuyService <T extends IDBean>{
	
	/**
	 * 添加股票购买记录
	 * @param stockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> add(long stockId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 更新股票购买记录
	 * @param stockId
	 * @param stockBuyId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(long stockId, long stockBuyId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除股票购买记录
	 * @param stockId
	 * @param stockBuyId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(long stockId, long stockBuyId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
}
