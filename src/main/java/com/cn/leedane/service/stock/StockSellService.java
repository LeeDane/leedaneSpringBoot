package com.cn.leedane.service.stock;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 股票卖出记录service接口类
 * @author LeeDane
 * 2018年6月22日 下午5:58:39
 * version 1.0
 */
@Transactional
public interface StockSellService <T extends IDBean>{
	
	/**
	 * 添加股票卖出记录
	 * @param stockId
	 * @param stockBuyId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> add(int stockId, int stockBuyId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 更新股票卖出记录
	 * @param stockId
	 * @param stockBuyId
	 * @param stockSellId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(int stockId, int stockBuyId, int stockSellId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除股票卖出记录
	 * @param stockId
	 * @param stockBuyId
	 * @param stockSellId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int stockId, int stockBuyId, int stockSellId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
}
