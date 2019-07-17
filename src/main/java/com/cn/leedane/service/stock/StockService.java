package com.cn.leedane.service.stock;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 股票service接口类
 * @author LeeDane
 * 2018年6月22日 下午3:36:44
 * version 1.0
 */
@Transactional
public interface StockService <T extends IDBean>{
	/**
	 * 股票首页的初始化数据
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> init(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	
	/**
	 * 添加股票
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> add(JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 更新股票
	 * @param stockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(int stockId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除股票
	 * @param stockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int stockId, JSONObject json, UserBean user, HttpRequestInfoBean request);
	
}
