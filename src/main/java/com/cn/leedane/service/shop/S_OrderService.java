package com.cn.leedane.service.shop;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 订单service接口类
 * @author LeeDane
 * 2017年12月7日 下午11:27:11
 * version 1.0
 */
@Transactional
public interface S_OrderService <T extends IDBean>{
	/**
	 * 添加订单
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取用户的未处理订单数量
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> getNoDealOrderNumber(UserBean user, HttpServletRequest request);

	/**
	 * 分页获取订列表
	 * @param current
	 * @param pageSize
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(int current,
			int pageSize, UserBean user,
			HttpServletRequest request);

	/**
	 * 删除订单
	 * @param orderId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int orderId,
			UserBean user, HttpServletRequest request);

	/**
	 * 修改订单
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(
			int orderId, 
			JSONObject json, UserBean user,
			HttpServletRequest request);
	
	
}
