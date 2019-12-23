package com.cn.leedane.springboot.controller;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.RoleController;
import com.cn.leedane.handler.JuheApiHandler;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.mall.S_HomeItemHandler;
import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.handler.mall.S_ShopHandler;
import com.cn.leedane.handler.mall.S_WishHandler;
import com.cn.leedane.juheapi.JuHeException;
import com.cn.leedane.mall.taobao.api.DetailSimpleApi;
import com.cn.leedane.mapper.UserMapper;
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
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 我的资料管理Html页面的控制器
 * @author LeeDane
 * 2019年12月17日 下午7:00:32
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class MyManagelHtmlController extends BaseController{

	@Autowired
	private SystemCache systemCache;

	@Autowired
	private JuheApiHandler juheApiHandler;

	@Autowired
	private OptionHandler optionHandler;

	@Autowired
	protected UserMapper userMapper;

	@Autowired
	private VisitorService<VisitorBean> visitorService;

	/**
	 * 加载公共部分
	 * @param userBean
	 * @param model
	 */
	private void loadCommon(UserBean userBean, Model model){
		JSONArray mysettings = JSONArray.fromObject(optionHandler.getData("mysetting", true));
		model.addAttribute("mysettings", mysettings);
	}

	@RequestMapping("/my/welcome")
	public String myWelcome(Model model, HttpServletRequest request) throws JuHeException {
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的资料首页", "com.cn.leedane.springboot.controller.MallHtmlController.myWelcome", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		loadCommon(getMustLoginUserFromShiro(), model);
		model.addAttribute("jokes", juheApiHandler.getData(EnumUtil.JuheApiType.笑话精选, 10, false));
		model.addAttribute("todays", juheApiHandler.getData(EnumUtil.JuheApiType.历史今天, 10, true));
		model.addAttribute("heads", juheApiHandler.getData(EnumUtil.JuheApiType.头条新闻, 10,false));
		model.addAttribute("tabId", "my-welcome");
		return loginRoleCheck("manage/my/welcome", true, model, request);
	}

	@RequestMapping("/my/info")
	public String myInfo(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		loadCommon(getMustLoginUserFromShiro(), model);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的资料首页", "com.cn.leedane.springboot.controller.MallHtmlController.myInfo", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("tabId", "my-info");
		return loginRoleCheck("manage/my/info", true, model, request);
	}

	@RequestMapping("/my/password")
	public String myPassword(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		loadCommon(getMustLoginUserFromShiro(), model);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入修改密码首页", "com.cn.leedane.springboot.controller.MallHtmlController.myPassword", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("tabId", "my-password");
		model.addAttribute("publicKey",  RSAKeyUtil.getInstance().getPublicKey());
		return loginRoleCheck("manage/my/password", true, model, request);
	}

	@RequestMapping("/my/email")
	public String myEmail(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入邮箱修改首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("tabId", "my-email");
		model.addAttribute("oldEmail", userBean.getEmail());
		model.addAttribute("publicKey",  RSAKeyUtil.getInstance().getPublicKey());
		return loginRoleCheck("manage/my/email", true, model, request);
	}

	/**
	 * 绑定电子邮箱,在电子邮箱里的链接点击
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/my/email/bind/click", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public String bindEmailClick(@RequestParam("param") String param, @RequestParam("start") long start, @RequestParam("end") long end, Model model, HttpServletRequest request) {
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//解析获取明文
		JSONObject plainObject = CommonUtil.sm4Decrypt(param);
		boolean success;
		if(plainObject == null || plainObject.optLong("start") != start || plainObject.optLong("end") != end){
			success = false;
		}else{
			long userId = plainObject.optLong("uid");
			UserBean bindUser = userHandler.getUserBean(userId);
			if(bindUser == null){
				success = false;
			}else{
				String email = plainObject.optString("email");
				bindUser.setEmail(email);
				success = userMapper.update(bindUser) > 0;
				if(success){
					//添加ES缓存
					new ThreadUtil().singleTask(new EsIndexAddThread<UserBean>(bindUser));
					//把Redis缓存的信息删除掉
					userHandler.deleteUserDetail(bindUser.getId());
				}
			}
		}
		model.addAttribute("success", success);
		return loginRoleCheck("manage/my/email-click", model, request);
	}
}

