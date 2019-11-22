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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
	public Map<String, Object> addCarousel(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeCarouselService.add(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除轮播商品
	 * @return
	 */
	@RequestMapping(value = "/carousel/{carouselId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deleteCarousel(@PathVariable("carouselId") long carouselId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeCarouselService.delete(carouselId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取首页展示轮播商品
	 * @return
	 */
	@RequestMapping(value = "/carousels", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> carousels(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(homeCarouselService.carousel());
		return message.getMap();
	}

	/**
	 * 新增分类项
	 * @return
	 */
	@RequestMapping(value = "/item", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addItem(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeItemService.addItem(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	
	/**
	 * 获取未添加的项
	 * @return
	 */
	@RequestMapping(value = "/item/noList", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> noList(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeItemService.noList(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取分类项
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getItem(@PathVariable("itemId") long itemId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeItemService.getItem(itemId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改分类项
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateItem(@PathVariable("itemId") long itemId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeItemService.updateItem(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除分类商品
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deleteCategory(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(homeItemService.deleteItem(itemId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取每一项的匹配分类
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/matching", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> matching(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(homeItemService.matchingCategory(itemId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	/**
	 * 修改某一项的子分类
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/category", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateCategry(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(homeItemService.updateCategory(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 添加某一项的商品
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/product", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addProduct(@PathVariable("itemId") long itemId,HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(homeItemService.addProduct(itemId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除某一项的商品
	 * @return
	 */
	@RequestMapping(value = "/item/{itemId}/product/{productId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deleteProduct(@PathVariable("itemId") long itemId, @PathVariable("productId") long productId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(homeItemService.deleteProduct(itemId, productId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取分类项列表
	 * @return
	 */
	@RequestMapping(value = "/items", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getItems(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(homeItemService.getItems(getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 新增店铺
	 * @return
	 */
	@RequestMapping(value = "/shop", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addShop(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeShopService.add(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除商店
	 * @return
	 */
	@RequestMapping(value = "/shop/{shopId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deleteShop(@PathVariable("shopId") long shopId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeShopService.delete(shopId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取首页展示商店列表
	 * @return
	 */
	@RequestMapping(value = "/shops", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> shops(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		checkRoleOrPermission(model, request);;
		message.putAll(homeShopService.shops());
		return message.getMap();
	}
}
