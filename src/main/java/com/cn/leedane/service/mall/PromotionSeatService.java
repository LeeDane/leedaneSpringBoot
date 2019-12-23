package com.cn.leedane.service.mall;

import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 商城工具service接口类
 * @author LeeDane
 * 2019年11月12日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface PromotionSeatService<T extends IDBean>{
	/**
	 * 添加推广位
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 自动添加拼多多推广位
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> autoCreatePdd(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws PddException;

	/**
	 * 分页获取订列表
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(
									  JSONObject json,
									  UserBean user,
									  HttpRequestInfoBean request);

	/**
	 * 获取未分配的用户列表
	 * @param seatId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> noallot(
			long seatId,
			JSONObject json,
			UserBean user,
			HttpRequestInfoBean request);

	/**
	 *  给推广位分配用户对象
	 * @param seatId
	 * @param userId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> allotObject(
			long seatId,
			long userId,
			UserBean user,
			HttpRequestInfoBean request);

	/**
	 *  给推广位删除用户对象
	 * @param seatId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteAllot(
			long seatId,
			UserBean user,
			HttpRequestInfoBean request);


	/**
	 * 删除订单
	 * @param seatId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(long seatId,
									  UserBean user, HttpRequestInfoBean request);



	/**
	 * 修改订单
	 * @param seatId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(
			long seatId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request) throws Exception;

}
