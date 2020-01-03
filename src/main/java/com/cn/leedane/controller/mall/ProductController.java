package com.cn.leedane.controller.mall;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.service.mall.S_ProductService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品接口controller
 * @author LeeDane
 * 2017年11月7日 下午4:43:56
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.mall)
public class ProductController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private S_ProductService<S_ProductBean> productService;
	/**
	 * 发布商品
	 * @return
	 */
	@RequestMapping(value = "/product", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);;
		return productService.save(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}

	/**
	 * 商品统计
	 * @return
	 */
	@RequestMapping(value = "/product/{productId}/statistics", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel statistics(@PathVariable("productId") long productId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return productService.statistics(productId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 商品推荐
	 * @return
	 */
	@RequestMapping(value = "/product/{productId}/recommend", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel recommend(@PathVariable("productId") long productId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return productService.recommend(productId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
}
