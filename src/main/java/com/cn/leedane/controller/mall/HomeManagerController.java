package com.cn.leedane.controller.mall;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.mall.S_HomeCarouselBean;
import com.cn.leedane.model.mall.S_HomeItemBean;
import com.cn.leedane.model.mall.S_HomeShopBean;
import com.cn.leedane.service.mall.S_HomeCarouselService;
import com.cn.leedane.service.mall.S_HomeItemService;
import com.cn.leedane.service.mall.S_HomeShopService;
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
@RequestMapping(value = ControllerBaseNameUtil.homeManager)
public class HomeManagerController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private S_HomeCarouselService<S_HomeCarouselBean> homeCarouselService;
	
	@Autowired
	private S_HomeItemService<S_HomeItemBean> homeItemService;
	
	@Autowired
	private S_HomeShopService<S_HomeShopBean> homeShopService;
	
	/**
	 * 新增轮播商品
	 * @return
	 */
	@RequestMapping(value = "/carousel", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel addCarousel(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		return homeCarouselService.add(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 删除轮播商品
	 * @return
	 */
	@RequestMapping(value = "/carousel/{carouselId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel deleteCarousel(@PathVariable("carouselId") long carouselId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		
		checkRoleOrPermission(model, request);
		return homeCarouselService.delete(carouselId, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取首页展示轮播商品
	 * @return
	 */
	@RequestMapping(value = "/carousels", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel carousels(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return homeCarouselService.carousel();
	}

	/**
	 * 新增分类项
	 * @return
	 */
	@RequestMapping(value = "/item", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel addItem(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return homeItemService.addItem(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	
	/**
	 * 获取未添加的项
	 * @return
	 */
	@RequestMapping(value = "/item/noList", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel noList(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return homeItemService.noList(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取分类项
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel getItem(@PathVariable("itemId") long itemId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return homeItemService.getItem(itemId, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 修改分类项
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public ResponseModel updateItem(@PathVariable("itemId") long itemId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return homeItemService.updateItem(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 删除分类商品
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel deleteCategory(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return homeItemService.deleteItem(itemId, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取每一项的匹配分类
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/matching", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel matching(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return homeItemService.matchingCategory(itemId, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	/**
	 * 修改某一项的子分类
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/category", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public ResponseModel updateCategry(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return homeItemService.updateCategory(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 添加某一项的商品
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/product", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel addProduct(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return homeItemService.addProduct(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 删除某一项的商品
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/product/{productId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel deleteProduct(@PathVariable("itemId") long itemId, @PathVariable("productId") long productId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		return homeItemService.deleteProduct(itemId, productId, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取分类项列表
	 * @return
	 */
	@RequestMapping(value = "/items", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel getItems(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return homeItemService.getItems(getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 新增店铺
	 * @return
	 */
	@RequestMapping(value = "/shop", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel addShop(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return homeShopService.add(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 删除商店
	 * @return
	 */
	@RequestMapping(value = "/shop/{shopId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel deleteShop(@PathVariable("shopId") long shopId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return homeShopService.delete(shopId, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取首页展示商店列表
	 * @return
	 */
	@RequestMapping(value = "/shops", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel shops(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		checkRoleOrPermission(model, request);
		return homeShopService.shops();
	}
}
