package com.cn.leedane.springboot.controller;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.controller.BaseController;
import com.cn.leedane.handler.JuheApiHandler;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.handler.mall.S_PromotionSeatHandler;
import com.cn.leedane.juheapi.JuHeException;
import com.cn.leedane.mapper.LogoutMapper;
import com.cn.leedane.mapper.MyTagsMapper;
import com.cn.leedane.mapper.Oauth2Mapper;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.mapper.mall.ReferrerMapper;
import com.cn.leedane.mapper.mall.ReferrerRecordMapper;
import com.cn.leedane.model.LogoutBean;
import com.cn.leedane.model.Oauth2Bean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.model.mall.S_ReferrerBean;
import com.cn.leedane.model.mall.S_ReferrerRecordBean;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;
import com.cn.leedane.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private Oauth2Mapper oauth2Mapper;

	@Autowired
	private MyTagsMapper myTagsMapper;

	@Autowired
	private ReferrerMapper referrerMapper;

	@Autowired
	private ReferrerRecordMapper referrerRecordMapper;

	@Autowired
	private S_PromotionSeatHandler promotionSeatHandler;

	@Autowired
	private LogoutMapper logoutMapper;

	/**
	 * 加载公共部分
	 * @param userBean
	 * @param model
	 * @param tabId
	 */
	private void loadCommon(UserBean userBean, Model model, String tabId){
		JSONArray mysettings = JSONArray.fromObject(optionHandler.getData("mysetting", true));
		model.addAttribute("mysettings", mysettings);
		model.addAttribute("tabId", tabId);
		model.addAttribute("user", userBean);
	}

	@RequestMapping("/my/welcome")
	public String myWelcome(Model model, HttpServletRequest request) throws JuHeException {
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的资料首页", "com.cn.leedane.springboot.controller.MallHtmlController.myWelcome", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		loadCommon(getMustLoginUserFromShiro(), model, "my-welcome");
		model.addAttribute("jokes", juheApiHandler.getData(EnumUtil.JuheApiType.笑话精选, 10, false));
		model.addAttribute("todays", juheApiHandler.getData(EnumUtil.JuheApiType.历史今天, 10, true));
		model.addAttribute("heads", juheApiHandler.getData(EnumUtil.JuheApiType.头条新闻, 10,false));
		return loginRoleCheck("manage/my/welcome", true, model, request);
	}

	@RequestMapping("/my/info")
	public String myInfo(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		UserBean user = getMustLoginUserFromShiro();
		loadCommon(user, model, "my-info");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的资料首页", "com.cn.leedane.springboot.controller.MallHtmlController.myInfo", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/my/info", true, model, request);
	}

	@RequestMapping("/my/password")
	public String myPassword(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		loadCommon(getMustLoginUserFromShiro(), model, "my-password");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入修改密码首页", "com.cn.leedane.springboot.controller.MallHtmlController.myPassword", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("publicKey",  RSAKeyUtil.getInstance().getPublicKey());
		return loginRoleCheck("manage/my/password", true, model, request);
	}

	@RequestMapping("/my/email")
	public String myEmail(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "my-email");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入邮箱修改首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("oldEmail", userBean.getEmail());
		model.addAttribute("publicKey",  RSAKeyUtil.getInstance().getPublicKey());
		return loginRoleCheck("manage/my/email", true, model, request);
	}

	@RequestMapping("/my/phone")
	public String myPhone(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "my-phone");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入手机号码绑定首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("publicKey",  RSAKeyUtil.getInstance().getPublicKey());
		return loginRoleCheck("manage/my/phone", true, model, request);
	}

	@RequestMapping("/my/tags")
	public String myTags(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "my-tags");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入手机号码绑定首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("tags", myTagsMapper.getTags(userBean.getId()));
		return loginRoleCheck("manage/my/tags", true, model, request);
	}

	/**
	 * 推广位管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/mall/promotion/seat")
	public String mallPromotionSeat(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "mall-promotion");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的推广位管理首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		//获取推广位列表
		model.addAttribute("seats", promotionSeatHandler.getMySeats(userBean.getId()));
		return loginRoleCheck("manage/mall/promotion", true, model, request);
	}

	/**
	 * 推荐关系管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/mall/referrer")
	public String mallReferrer(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "mall-referrer");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入推荐关系管理首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		S_ReferrerBean referrerBean = referrerMapper.findReferrerCode(userBean.getId());
		S_ReferrerRecordBean referrerRecord = referrerRecordMapper.findReferrer(userBean.getId());
		model.addAttribute("referrer", referrerBean);
		model.addAttribute("referrerRecord", referrerRecord);
		if(referrerRecord != null){
			referrerRecord.setName(userHandler.getUserName(referrerRecord.getUserId()));
		}
		return loginRoleCheck("manage/mall/referrer", true, model, request);
	}
	/**
	 * 我的收获地址管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/mall/address")
	public String mallAddress(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "mall-address");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的地址管理首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/mall/address", true, model, request);
	}

	/**
	 * 第三方账号绑定
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/my/third")
	public String myThird(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "my-third");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入第三方绑定首页", "com.cn.leedane.springboot.controller.MallHtmlController.myEmail", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		List<Oauth2Bean> oauth2Beans = oauth2Mapper.myOauth2s(userBean.getId());
		EnumUtil.Oauth2PlatformType[] types = EnumUtil.Oauth2PlatformType.values();
		List<Oauth2Bean> oauth2s = new ArrayList<>(); //构建最终输出页面的列表
		for(EnumUtil.Oauth2PlatformType type: types){
			String value = type.value;
			if(CollectionUtil.isNotEmpty(oauth2Beans)) {
				boolean hasOauth = false;//标记是否已经授权绑定啦
				int index = 0;
				for (Oauth2Bean oauth2Bean : oauth2Beans) {
					if(oauth2Bean.getOauth2Id() > 0 && oauth2Bean.getPlatform().equalsIgnoreCase(value)){
						hasOauth = true;
						break;
					}
					index = index + 1;
				}
				if(!hasOauth){
					//不存在的情况下
					Oauth2Bean oauth2Bean = new Oauth2Bean();
					oauth2Bean.setPlatform(value);
					oauth2Bean.setOpenId("/oauth2/login/"+ value);
					oauth2s.add(oauth2Bean);
				}else{
					oauth2s.add(oauth2Beans.get(index));
				}
			}else{
				//不存在的情况下
				Oauth2Bean oauth2Bean = new Oauth2Bean();
				oauth2Bean.setPlatform(value);
				oauth2Bean.setOpenId("/oauth2/login/"+ value);
				oauth2s.add(oauth2Bean);
			}
		}
		model.addAttribute("oauth2s", oauth2s);
		return loginRoleCheck("manage/my/third", true, model, request);
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
			//这里必须要重新获取一份UserBean对象，不然直接修改参数user会导致shiro里面的user有值，Java是值传递
			UserBean updateUser = userMapper.findById(UserBean.class, userId);
			if(updateUser == null){
				success = false;
			}else{
				String email = plainObject.optString("email");
				updateUser.setEmail(email);
				success = userMapper.update(updateUser) > 0;
				if(success){
					//对当前已经登录的用户
					UserBean user = getUserFromShiro();
					if(user != null)
						user.setEmail(email); //由于系统没有退出，需要重新赋值
					//添加ES缓存
					new ThreadUtil().singleTask(new EsIndexAddThread<UserBean>(updateUser));
					//把Redis缓存的信息删除掉
					userHandler.deleteUserDetail(updateUser.getId());
				}
			}
		}
		model.addAttribute("success", success);
		return loginRoleCheck("manage/my/email-click", model, request);
	}

	/**
	 * 第三方授权绑定失败页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/my/third/oauth2/bind/error", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public String oauth2BindError(@RequestParam("message") String message, Model model, HttpServletRequest request) {
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		JSONObject object = CommonUtil.sm4Decrypt(message);
		model.addAttribute("message", object.optString("msg"));
		return loginRoleCheck("manage/my/oauth2-bind-error", model, request);
	}

	/**
	 * 登录历史
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/login/history")
	public String loginHostory(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "login-history");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的登录历史首页", "com.cn.leedane.springboot.controller.MallHtmlController.loginHostory", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/login-history", true, model, request);
	}

	/**
	 * 博客列表
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/blog/list")
	public String blogList(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "blog-list");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入博客列表页", "com.cn.leedane.springboot.controller.MallHtmlController.blogList", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/blog/list", true, model, request);
	}

	/**
	 * 博客草稿
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/blog/draft")
	public String blogDraft(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "blog-draft");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入博客草稿管理页", "com.cn.leedane.springboot.controller.MallHtmlController.blogDraft", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/blog/draft", true, model, request);
	}

	/**
	 * 安全--黑名单
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/security/black")
	public String securityBlack(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "security-black");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入安全黑名单管理页", "com.cn.leedane.springboot.controller.MallHtmlController.securityBlack", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/security/black", true, model, request);
	}

	/**
	 * 安全--实名验证
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/security/realname")
	public String securityRealname(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "security-realname");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入安全实名验证管理页", "com.cn.leedane.springboot.controller.MallHtmlController.securityRealname", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/security/realname", true, model, request);
	}

	/**
	 * 我的工具--> 抖音去水印
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/tool/douyin/remove/watermark")
	public String toolDouyinRemoveWatermark(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "tool-douyin-remove-watermark");
		model.addAttribute("path", optionHandler.getData("douyinremovewatermark", true));
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入抖音去水印管理页", "com.cn.leedane.springboot.controller.MallHtmlController.toolDouyinRemoveWatermark", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/tool/douyin-remove-watermark", true, model, request);
	}

	/**
	 * 我的工具--> 事件提醒
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/tool/event/remind")
	public String eventRemind(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "tool-event-remind");
		model.addAttribute("limit", StringUtil.changeObjectToInt(optionHandler.getData("remindSmsLimit", true)));
		model.addAttribute("sms", StringUtil.isPhone(userHandler.getUserMobilePhone(userBean.getId())));//判断用户是否可以用sms
		model.addAttribute("email", StringUtil.isEmail(userHandler.getEmail(userBean.getId())));//判断用户是否可以用email
		List<Map<String, Object>> types = new ArrayList<>();
		Map<String, Object> type = new HashMap<>();
		type.put("name", "提醒吃药");
		type.put("value", "takeMedicine");
		types.add(type);
		type = new HashMap<>();
		type.put("name", "再次提醒吃药");
		type.put("value", "againTakeMedicine");
		types.add(type);
		model.addAttribute("types", types);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入提醒吃药管理页", "com.cn.leedane.springboot.controller.MallHtmlController.eventRemind", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/tool/event-remind", true, model, request);
	}


	/**
	 * 我的云盘
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/clouddisk")
	public String clouddisk(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "clouddisk");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的云盘管理首页", "com.cn.leedane.springboot.controller.MallHtmlController.clouddisk", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/clouddisk", true, model, request);
	}

	/**
	 * 我的关注
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/attention")
	public String attention(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "attention");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的关注管理首页", "com.cn.leedane.springboot.controller.MallHtmlController.attention", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/attention", true, model, request);
	}

	/**
	 * 我的收藏
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/collection")
	public String collection(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "collection");
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入我的收藏管理首页", "com.cn.leedane.springboot.controller.MallHtmlController.collection", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/collection", true, model, request);
	}


	/**
	 * 注销账号
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model, request);
		//获取当前登录用户
		UserBean userBean = getMustLoginUserFromShiro();
		loadCommon(userBean, model, "logout");
		LogoutBean logoutBean = logoutMapper.recode(userBean.getId());
		if(logoutBean != null){
			model.addAttribute("time", DateUtil.DateToString(logoutBean.getOverdue()));
			model.addAttribute("reason", logoutBean.getReason());
			model.addAttribute("note", logoutBean.getNote());
			model.addAttribute("createTime", DateUtil.DateToString(logoutBean.getCreateTime()));
		}

		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入注销账号首页", "com.cn.leedane.springboot.controller.MallHtmlController.loginHostory", ConstantsUtil.STATUS_SELF, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("manage/logout", true, model, request);
	}

}

