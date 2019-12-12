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
	 * 分页获取订列表
	 * @param current
	 * @param pageSize
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(int current,
									  int pageSize,
									  JSONObject json,
									  UserBean user,
									  HttpRequestInfoBean request);

	/**
	 * 删除订单
	 * @param promotionSeatId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(long promotionSeatId,
									  UserBean user, HttpRequestInfoBean request);

	/**
	 * 修改订单
	 * @param promotionSeatId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> update(
			long promotionSeatId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request) throws Exception;

}
