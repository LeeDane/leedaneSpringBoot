package com.cn.leedane.service.mall;

import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.suning.api.exception.SuningApiException;
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
	public ResponseModel product (JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;

	/**
	 * 搜索商店
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel shop(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 搜索活动
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  ResponseModel activity(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 构建淘宝分享的链接
	 * @param productId
	 * @param user
	 * @param request
	 * @return
	 */
	@Deprecated
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel buildShare(String productId, UserBean user, HttpRequestInfoBean request);

	/**
	 *  商品推荐
	 * @param itemId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 * @throws ApiException
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  ResponseModel productRecommend(String itemId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws ApiException, PddException, SuningApiException;

	/**
	 * 获取大字段
	 * @param productId
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel bigfield(String productId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception;
}
