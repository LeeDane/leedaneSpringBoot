package com.cn.leedane.controller.mall;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.mall.MallSearchService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import com.suning.api.exception.SuningApiException;
import com.taobao.api.ApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 商城全局搜索统一入口接口controller
 * @author LeeDane
 * 2019年11月12日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.mall_search)
public class MallSearchController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private MallSearchService<IDBean> mallSearchService;

	/**
	 * 查询商品
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/product", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel search(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		Long start = System.currentTimeMillis();
		return mallSearchService.product(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)).start(start);
	}

	/**
	 * 查询商店
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/shop", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel shop(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
//		message.putAll(taobaoService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return new ResponseModel().ok();
	}

	/**
	 * 查询活动
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/activity", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel activity(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
//		message.putAll(taobaoService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return new ResponseModel().ok();
	}

	/**
	 * 查询淘宝商品
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/product/{itemId}/recommend", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel search(@PathVariable("itemId") String itemId, HttpServletRequest request) throws ApiException, SuningApiException, PddException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return mallSearchService.productRecommend(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	/**
	 * 大文本字段获取需要登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{productId}/bigfield", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel bigfield(@PathVariable("productId") String productId, HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return mallSearchService.bigfield(productId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

}
