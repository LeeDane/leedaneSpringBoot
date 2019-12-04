package com.cn.leedane.controller.mall;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.mall.MallSearchService;
import com.cn.leedane.service.mall.S_TaobaoService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

	@Autowired
	private S_TaobaoService<IDBean> taobaoService;

	/**
	 * 查询商品
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/product", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> search(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(mallSearchService.product(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 查询商店
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/shop", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> shop(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);

//		message.putAll(taobaoService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 查询活动
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/activity", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> activity(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
//		message.putAll(taobaoService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 构建淘宝的分享链接
	 * @param request
	 * @return
	 *//*
	@RequestMapping(value = "/build/share/link", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> buildShareLink(@PathParam("taobaoId") String taobaoId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(taobaoService.buildShare(taobaoId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}*/

	/**
	 * 查询淘宝商品
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/product/{itemId}/recommend", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> search(@PathVariable("itemId") long itemId, HttpServletRequest request) throws ApiException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);

		message.putAll(mallSearchService.productRecommend(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	/**
	 * 大文本字段获取需要登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{productId}/bigfield", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> bigfield(@PathVariable("productId") String productId, HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		message.putAll(mallSearchService.bigfield(productId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}

}
