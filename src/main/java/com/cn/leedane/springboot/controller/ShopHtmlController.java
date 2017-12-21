package com.cn.leedane.springboot.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.auth.AuthenticationException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.UserController;
import com.cn.leedane.handler.shop.S_ProductHandler;
import com.cn.leedane.handler.shop.S_ShopHandler;
import com.cn.leedane.handler.shop.S_WishHandler;
import com.cn.leedane.lucene.solr.ProductSolrHandler;
import com.cn.leedane.lucene.solr.ShopSolrHandler;
import com.cn.leedane.mapper.shop.S_ProductMapper;
import com.cn.leedane.mapper.shop.S_ShopMapper;
import com.cn.leedane.model.CategoryBean;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.model.shop.S_ProductBean;
import com.cn.leedane.model.shop.S_ShopBean;
import com.cn.leedane.service.CategoryService;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.service.shop.S_ProductService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.SolrAddThread;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 商城Html页面的控制器
 * @author LeeDane
 * 2017年5月2日 下午5:00:32
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.shop)
public class ShopHtmlController extends BaseController{
	
	@Autowired
	private UserService<UserBean> userService;
	
	@Autowired
	private S_ProductService<S_ProductBean> productService;
	
	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private S_ShopHandler shopHandler;
	
	@Autowired
	private S_WishHandler wishHandler;
	
	@Autowired
	private CommentService<CommentBean> commentService;
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Autowired
	private S_ProductMapper productMapper;
	
	@Autowired
	private S_ShopMapper shopMapper;
	
	@Autowired
	private CategoryService<CategoryBean> categoryService;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return loginRoleCheck("shop/index", model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/product/{productId}/detail")
	public String productDetail(Model model, 
			@PathVariable(value="productId") int productId, HttpServletRequest request){
				
		/*new ThreadUtil().singleTask(new SolrAddThread<S_ShopBean>(ShopSolrHandler.getInstance(), shopMapper.findById(S_ShopBean.class, 1)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 1)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 2)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 3)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 6)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 7)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 8)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 9)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 10)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 11)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 12)));
		new ThreadUtil().singleTask(new SolrAddThread<S_ProductBean>(ProductSolrHandler.getInstance(), productMapper.findById(S_ProductBean.class, 13)));*/
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
				
		model.addAttribute("product", productBean);
		List<String> ss = Arrays.asList(productBean.getMainImgLinks().split(";"));
		model.addAttribute("mainLinks", ss);
		
		int total = commentService.getTotal("t_comment", " where table_name='"+ DataTableType.商店商品.value +"' and table_id = " +productId +" and status = 1");
		model.addAttribute("commentTotal", CommonUtil.getFormatTotal(total));
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        //int wishNumber = 0;
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	//wishNumber = wishHandler.getWishNumber(user.getId());
        }
       // model.addAttribute("wishNumber", wishNumber);
        
        //处理分类
        List<String[]> categorys = new ArrayList<String[]>();
        categorys.add(new String[]{"-1", "LeeDane", "/"});
       // categorys.add(new String[]{"-1", "首页", "/shop/index"});
        Map<String, Object> relationMap = categoryService.shopCategory(productBean.getCategoryId());
        if(StringUtil.changeObjectToBoolean(relationMap.get("isSuccess"))){
        	categorys.addAll((List<String[]>)relationMap.get("message"));
        }
        model.addAttribute("categorys", categorys);
		//保存访问记录
		visitorService.saveVisitor(user, "web网页端", DataTableType.商店商品.value, productId, ConstantsUtil.STATUS_NORMAL);
		return loginRoleCheck("shop/detail", model, request);
	}
	
	@RequestMapping("/{shopId}")
	public String shop(Model model, 
			@PathVariable(value="shopId") int shopId, HttpServletRequest request){
						
		S_ShopBean shopBean = shopHandler.getNormalShopBean(shopId);
		if(shopBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商店不存在或已被删除.value));
				
		model.addAttribute("shop", shopBean);
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        }
		//保存访问记录
		visitorService.saveVisitor(user, "web网页端", DataTableType.商店.value, shopId, ConstantsUtil.STATUS_NORMAL);
		return loginRoleCheck("shop/shop", model, request);
	}
	
	/**
	 * 心愿单入口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/wish")
	public String wish(Model model, HttpServletRequest request){
		return loginRoleCheck("shop/wish", true, model, request);
	}
	
	
	/**
	 * 订单入口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/order")
	public String order(Model model, HttpServletRequest request){
		return loginRoleCheck("shop/order", true, model, request);
	}
	
	/**
	 * 分类入口
	 * @param model
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/category/{categoryId}")
	public String wish(Model model, @PathVariable(value="categoryId") int categoryId, HttpServletRequest request){
		
		//处理分类
        List<String[]> categorys = new ArrayList<String[]>();
        categorys.add(new String[]{"-1", "LeeDane", "/"});
       // categorys.add(new String[]{"-1", "首页", "/shop/index"});
        Map<String, Object> relationMap = categoryService.shopCategory(categoryId);
        if(StringUtil.changeObjectToBoolean(relationMap.get("isSuccess"))){
        	categorys.addAll((List<String[]>)relationMap.get("message"));
        }
        model.addAttribute("categorys", categorys);
        
		return loginRoleCheck("shop/category", model, request);
	}
	
	/**
	 * 店铺/商品搜索
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(ControllerBaseNameUtil.s)
	public String search(Model model, HttpServletRequest request){
		return loginRoleCheck("shop/search", model, request);
	}
	
	/**
	 * 商店管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/manager")
	public String manager(Model model, HttpServletRequest request){
		return loginRoleCheck("shop/shop-manager", true, model, request);
	}
	
	/**
	 * 商品管理
	 * @param model
	 * @param productId
	 * @param request
	 * @return
	 */
	@RequestMapping("/product/{productId}/manager")
	public String productManager(Model model, 
			@PathVariable(value="productId") int productId, HttpServletRequest request){
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
			
		String responseStr  = loginRoleCheck("shop/product-manager", true, model, request);
		
		//判断是否是创建者或者是管理员
		Map<String, Object> modelMap = model.asMap();
		if(!StringUtil.changeObjectToBoolean(modelMap.get("isAdmin")) && StringUtil.changeObjectToInt(modelMap.get("loginUserId")) != productBean.getCreateUserId()){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value));
		}
		
		model.addAttribute("product", productBean);
		
		UserBean user = (UserBean)modelMap.get("user");
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"管理商品，该商品ID是", productId, StringUtil.getSuccessOrNoStr(true)).toString(), "productManager()", 1, 0);
		//保存访问记录
		visitorService.saveVisitor(user, "web网页端", DataTableType.商店商品.value, productId, ConstantsUtil.STATUS_NORMAL);
		return responseStr;
	}
}
