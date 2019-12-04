package com.cn.leedane.service.mall;

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
 * 商城相关service接口类
 * @author LeeDane
 * 2019年11月12日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface MallSearchService<T extends IDBean>{
	/**
	 * 搜索商品
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> product (JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;

	/**
	 * 搜索商店
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> shop(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 搜索活动
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> activity(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 构建淘宝分享的链接
	 * @param productId
	 * @param user
	 * @param request
	 * @return
	 */
	@Deprecated
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> buildShare(String productId, UserBean user, HttpRequestInfoBean request);

	/**
	 *  商品推荐
	 * @param productId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws ApiException
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> productRecommend(long productId, JSONObject jo, UserBean user, HttpRequestInfoBean request)  throws ApiException;

	/**
	 * 获取大字段
	 * @param productId
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> bigfield(String productId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;
}
