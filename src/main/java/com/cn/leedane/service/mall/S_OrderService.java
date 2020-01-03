package com.cn.leedane.service.mall;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.LayuiTableResponseModel;
import com.cn.leedane.utils.ResponseModel;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	public ResponseModel add(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;
	
	/**
	 * 获取用户的未处理订单数量
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel getNoDealOrderNumber(UserBean user, HttpRequestInfoBean request);

	/**
	 * 分页获取订列表
	 * @param current
	 * @param pageSize
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public LayuiTableResponseModel paging(int current,
										  int pageSize, UserBean user,
										  HttpRequestInfoBean request);

	/**
	 * 删除订单
	 * @param orderId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel delete(long orderId,
			UserBean user, HttpRequestInfoBean request);

	/**
	 * 修改订单
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel update(
			long orderId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request) throws Exception;
}
