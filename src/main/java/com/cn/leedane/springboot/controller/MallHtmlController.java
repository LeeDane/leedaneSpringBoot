package com.cn.leedane.springboot.controller;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.RoleController;
import com.cn.leedane.handler.mall.S_HomeItemHandler;
import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.handler.mall.S_ShopHandler;
import com.cn.leedane.handler.mall.S_WishHandler;
import com.cn.leedane.mall.taobao.api.DetailSimpleApi;
import com.cn.leedane.mapper.mall.S_ProductMapper;
import com.cn.leedane.mapper.mall.S_ShopMapper;
import com.cn.leedane.model.CategoryBean;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.model.mall.*;
import com.cn.leedane.service.CategoryService;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.service.mall.S_HomeCarouselService;
import com.cn.leedane.service.mall.S_HomeItemService;
import com.cn.leedane.service.mall.S_ProductService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商城Html页面的控制器
 * @author LeeDane
 * 2017年5月2日 下午5:00:32
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.mall)
public class MallHtmlController extends BaseController{

	
	@Autowired
	private S_ProductService<S_ProductBean> productService;
	
	@Autowired
	private S_HomeCarouselService<S_HomeCarouselBean> homeCarouselService;
	
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
	private S_HomeItemService<S_HomeItemBean> homeItemService;
	
	@Autowired
	private S_HomeItemHandler homeItemHandler;
	
	@Value("${constant.mall.home.category.id}")
    public int MALL_HOME_CATEGORY_ID;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);	
		
		//初始化首页的数据
		/*List<S_HomeItemBean> categoryList = homeItemHandler.showCategoryList();
		List<S_HomeItemShowBean> homeItemShowBeans = new ArrayList<S_HomeItemShowBean>();
		for(S_HomeItemBean category: categoryList)
			homeItemShowBeans.add(homeItemHandler.getCategory(category.getId()));
		model.addAttribute("homeItemShowBeans", homeItemShowBeans);*/

		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商场模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mall/index", model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/product/{productId}/detail")
	public String productDetail(Model model, 
			@PathVariable(value="productId") String productId, HttpServletRequest request) throws Exception {
		//检查权限，通过后台配置
//		checkRoleOrPermission(model, request);
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

		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看商品详情，商品ID为："+ productId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);

		S_PlatformProductBean productBean = null;
		if(productId.startsWith("tb_")){
			String productIdTemp = productId.substring(3, productId.length());
			productBean = DetailSimpleApi.getDetailByMaterial(productIdTemp);
		}else if(productId.startsWith("jd_")){
			String productIdTemp = productId.substring(3, productId.length());
			productBean = com.cn.leedane.mall.jingdong.api.DetailSimpleApi.getDetail(productIdTemp);
		}else if(productId.startsWith("pdd_")){
			String productIdTemp = productId.substring(4, productId.length());
			productBean = com.cn.leedane.mall.pdd.api.DetailSimpleApi.getDetail(productIdTemp);
		}else{
			productBean = toPlatformProductBean(productHandler.getNormalProductBean(StringUtil.changeObjectToInt(productId)));
			if(productBean == null)
				throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));

			int total = commentService.getTotal("t_comment", " where table_name='"+ DataTableType.商店商品.value +"' and table_id = " +productId +" and status = 1");
			model.addAttribute("commentTotal", CommonUtil.getFormatTotal(total));
		}

		model.addAttribute("productSourceId", productId);
		model.addAttribute("product", productBean);
		List<String> ss = Arrays.asList(productBean.getImg().split(";"));
		model.addAttribute("mainLinks", ss);
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = getUserFromShiro();
        //int wishNumber = 0;
        /*if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	//wishNumber = wishHandler.getWishNumber(user.getId());
        }*/
       // model.addAttribute("wishNumber", wishNumber);
        
        //处理分类
        List<String[]> categorys = new ArrayList<String[]>();
        categorys.add(new String[]{"-1", "LeeDane", "/"});
       // categorys.add(new String[]{"-1", "首页", "/mall/index"});
        Map<String, Object> relationMap = categoryService.mallCategory(productBean.getCategoryId());
        if(StringUtil.changeObjectToBoolean(relationMap.get("isSuccess"))){
        	categorys.addAll((List<String[]>)relationMap.get("message"));
        }
        model.addAttribute("categorys", categorys);
		//保存访问记录
		visitorService.saveVisitor(user, "web网页端", DataTableType.商店商品.value, StringUtil.changeObjectToInt(productId), ConstantsUtil.STATUS_NORMAL);
		return loginRoleCheck("mall/detail", model, request);
	}
	
	@RequestMapping("/{shopId}")
	public String shop(Model model, 
			@PathVariable(value="shopId") int shopId, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看商店ID"+ shopId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		S_ShopBean shopBean = shopHandler.getNormalShopBean(shopId);
		if(shopBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商店不存在或已被删除.value));
				
		model.addAttribute("shop", shopBean);
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = getUserFromShiro();
		//保存访问记录
		visitorService.saveVisitor(user, "web网页端", DataTableType.商店.value, shopId, ConstantsUtil.STATUS_NORMAL);
		return loginRoleCheck("mall/shop", model, request);
	}
	
	/**
	 * 心愿单入口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/wish")
	public String wish(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入心愿单首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mall/wish", true, model, request);
	}
	
	
	/**
	 * 订单入口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/order")
	public String order(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);

		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入订单模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mall/order", true, model, request);
	}

	/**
	 * 登记购买入口(也是跳转到订单页面，只是在跳转的时候需要带上订单号)
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/order/register/{productId}")
	public String orderRegister(@PathVariable(value="productId") String productSourceId, Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入订单登记模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		String platform = EnumUtil.ProductPlatformType.淘宝.value;
		String productId = productSourceId;
		if(productSourceId.startsWith("tb_")){
			productId = productSourceId.substring(3, productSourceId.length());
		}else if(productSourceId.startsWith("jd_")){
			productId = productSourceId.substring(3, productSourceId.length());
			platform = EnumUtil.ProductPlatformType.京东.value;
		}else if(productSourceId.startsWith("pdd_")){
			productId = productSourceId.substring(4, productSourceId.length());
			platform = EnumUtil.ProductPlatformType.拼多多.value;
		}else{
			platform = EnumUtil.ProductPlatformType.系统自营.value;
		}
		model.addAttribute("productSourceId", productSourceId);
		model.addAttribute("productId", productId);
		model.addAttribute("platform", platform);
		return loginRoleCheck("mall/order", true, model, request);
	}
	
	/**
	 * 分类入口
	 * @param model
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/category/{categoryId}")
	public String category(Model model, @PathVariable(value="categoryId") int categoryId, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看商品分类ID"+ categoryId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		//处理分类
        List<String[]> categorys = new ArrayList<String[]>();
        categorys.add(new String[]{"-1", "LeeDane", "/"});
       // categorys.add(new String[]{"-1", "首页", "/shop/index"});
        Map<String, Object> relationMap = categoryService.mallCategory(categoryId);
        if(StringUtil.changeObjectToBoolean(relationMap.get("isSuccess"))){
        	categorys.addAll((List<String[]>)relationMap.get("message"));
        }
        model.addAttribute("categorys", categorys);
        
		return loginRoleCheck("mall/category", model, request);
	}
	
	/**
	 * 店铺/商品搜索
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(ControllerBaseNameUtil.s)
	public String search(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
//		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入物品搜索页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mall/search", model, request);
	}

	/**
	 * 商品管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/product/manager")
	public String productManager(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商店管理模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mall/product-manager", true, model, request);
	}

	/**
	 * 商店管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/shop/manager")
	public String shopManager(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商店管理模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mall/shop-manager", true, model, request);
	}
	
	/**
	 * 商品管理
	 * @param model
	 * @param productId
	 * @param request
	 * @return
	 */
	@RequestMapping("/product/{productId}/manage")
	public String productManage(Model model,
			@PathVariable(value="productId") String productId, HttpServletRequest request) throws ApiException, JdException {
		//检查权限，通过后台配置
		/*checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商品管理模块首页，商品ID"+ productId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		S_PlatformProductBean productBean = null;
		if(productId.startsWith("tb_")){
			productId = productId.substring(3, productId.length());
			productBean = DetailSimpleApi.getDetailByMaterial(productId);
			model.addAttribute("isThirdParty", true); //标记是第三方平台
		}else if(productId.startsWith("jd_")){
			productId = productId.substring(3, productId.length());
			productBean = com.cn.leedane.mall.jingdong.api.DetailSimpleApi.getDetail(productId);
			model.addAttribute("isThirdParty", true); //标记是第三方平台
		}else{
			model.addAttribute("isThirdParty", false); //标记不是第三方平台
			productBean = toPlatformProductBean(productHandler.getNormalProductBean(StringUtil.changeObjectToInt(productId)));
			if(productBean == null)
				throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
		}*/
			
		String responseStr  = loginRoleCheck("mall/product-detail", true, model, request);
		
		//判断是否是创建者或者是管理员
		/*Map<String, Object> modelMap = model.asMap();
		if(!StringUtil.changeObjectToBoolean(modelMap.get("isAdmin")) && StringUtil.changeObjectToInt(modelMap.get("loginUserId")) != productBean.getCreateUserId()){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value));
		}
		
		model.addAttribute("product", productBean);
		
		UserBean user = (UserBean)modelMap.get("user");
		
		//保存操作日志
		operateLogService.saveOperateLog(user, getHttpRequestInfo(request), null, StringUtil.getStringBufferStr(user.getAccount(),"管理商品，该商品ID是", productId, StringUtil.getSuccessOrNoStr(true)).toString(), "productManager()", 1, EnumUtil.LogOperateType.网页端.value);
		//保存访问记录
		visitorService.saveVisitor(user, "web网页端", DataTableType.商店商品.value, StringUtil.changeObjectToInt(productId), ConstantsUtil.STATUS_NORMAL);
		*/
		return responseStr;
	}
	
	@RequestMapping("/product/select")
	public String productSelect(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商品选择页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		String url = loginRoleCheck("mall/product-select", true, model, request);
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		//后台只有管理员权限才能操作
		if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && !currentUser.hasRole(RoleController.MALL_ROLE_CODE)){
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请使用有管理员权限的账号登录.value));
			return "redirect:/lg?errorcode=" +EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value +"&t="+ UUID.randomUUID().toString() +"&ref="+ CommonUtil.getFullPath(request);
		}
		return url;
	}
	
	@RequestMapping("/shop/select")
	public String shopSelect(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商店选择页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		String url = loginRoleCheck("mall/shop-select", true, model, request);
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		//后台只有管理员权限才能操作
		if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && !currentUser.hasRole(RoleController.MALL_ROLE_CODE)){
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请使用有管理员权限的账号登录.value));
			return "redirect:/lg?errorcode=" +EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value +"&t="+ UUID.randomUUID().toString() +"&ref="+ CommonUtil.getFullPath(request);
		}
		return url;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/home/manager")
	public String homeManager(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商场管理模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		String url = loginRoleCheck("mall/home-manager", true, model, request);
		//获取分类列表
		List<S_HomeItemBean> homeItemBeans = homeItemHandler.showCategoryList();
		Map<String, Object> categorys = categoryService.children(true, MALL_HOME_CATEGORY_ID, OptionUtil.adminUser, getHttpRequestInfo(request));
		
		List<Map<String, Object>> categoryList = new ArrayList<Map<String,Object>>();
		if(StringUtil.changeObjectToBoolean(categorys.get("isSuccess"))){
			categoryList = (List<Map<String, Object>>) categorys.get("message");
		}
		
		model.addAttribute("categorys", categoryList);
		model.addAttribute("homeItemBeans", homeItemBeans);
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		//后台只有管理员权限才能操作
		if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && !currentUser.hasRole(RoleController.MALL_ROLE_CODE)){
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请使用有管理员权限的账号登录.value));
			return "redirect:/lg?errorcode=" +EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value +"&t="+ UUID.randomUUID().toString() +"&ref="+ CommonUtil.getFullPath(request);
		}
		return url;
	}

	private S_PlatformProductBean toPlatformProductBean(S_ProductBean productBean){
		S_PlatformProductBean platformProductBean = new S_PlatformProductBean();
		if(productBean == null)
			return platformProductBean;
		platformProductBean.setShopTitle(productBean.getShop().getName());
		platformProductBean.setImg(productBean.getMainImgLinks());
		platformProductBean.setPlatform(productBean.getPlatform());
		platformProductBean.setPrice(productBean.getPrice());
		platformProductBean.setOldPrice(productBean.getOldPrice());
		platformProductBean.setCashBack(productBean.getCashBack());
		platformProductBean.setCashBackRatio(productBean.getCashBackRatio());
		platformProductBean.setCouponAmount(productBean.getCouponAmount());
		platformProductBean.setTitle(productBean.getTitle());
		platformProductBean.setId(productBean.getId());
		platformProductBean.setCategoryId(productBean.getCategoryId());
		platformProductBean.setCreateUserId(productBean.getCreateUserId());
		platformProductBean.setSubtitle(productBean.getSubtitle());
		platformProductBean.setDetail(productBean.getDetail());
		return platformProductBean;
	}

	@RequestMapping("/manage")
	public String manage(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入商城管理首页", "com.cn.leedane.springboot.controller.MallHtmlController.manage", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mall/manage", model, request);
	}
}
