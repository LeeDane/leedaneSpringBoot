package com.cn.leedane.service.mall;

import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.utils.LayuiTableResponseModel;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	public ResponseModel add(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 自动添加拼多多推广位
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel autoCreatePdd(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws PddException;

	/**
	 * 分页获取订列表
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public LayuiTableResponseModel paging(
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
	public ResponseModel noallot(
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
	public ResponseModel allotObject(
			long seatId,
			long userId,
			UserBean user,
			HttpRequestInfoBean request) throws NoticeException;

	/**
	 *  给推广位删除用户对象
	 * @param seatId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel deleteAllot(
			long seatId,
			UserBean user,
			HttpRequestInfoBean request) throws NoticeException;


	/**
	 * 删除订单
	 * @param seatId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel delete(long seatId,
									  UserBean user, HttpRequestInfoBean request);



	/**
	 * 修改订单
	 * @param seatId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel update(
			long seatId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request) throws Exception;

}
