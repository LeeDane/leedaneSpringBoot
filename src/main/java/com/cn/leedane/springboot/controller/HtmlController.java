package com.cn.leedane.springboot.controller;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.RoleController;
import com.cn.leedane.exception.MustLoginException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.model.*;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.AddReadSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.service.AppVersionService;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Html页面的控制器
 * @author LeeDane
 * 2017年3月16日 上午11:23:13
 * Version 1.0
 */
@Controller
public class HtmlController extends BaseController{
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Autowired
	private AppVersionService<FilePathBean> appVersionService;

	@Autowired
	private MoodHandler moodHandler;

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param request
	 * @return
	 */
	/*@RequestMapping
	public String index(Model model){
		return "index";
	}*/
	
	@RequestMapping(value = {"/", ControllerBaseNameUtil.index})
	public String index1(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入系统首页页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("index", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.pt)
	public String photo(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入图册模块页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("photo", true, model, request);
	}

	@RequestMapping(ControllerBaseNameUtil.pt +"/manage")
	public String photoManage(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入图册管理模块页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("photo-manage", true, model, request);
	}
	
	@RequestMapping("/test")
	public String test(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入测试模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("test", false, model, request);
	}


	@RequestMapping("/about")
	public String about(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入关于系统首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("about-me", false, model, request);
	}

	@RequestMapping("/love")
	public String love(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入爱情模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("love", false, model, request);
	}
	
	/**
	 * 消息管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(ControllerBaseNameUtil.msg)
	public String message(Model model, @RequestParam(value = "tab", required = false) String tab, HttpServletRequest request){
		if(StringUtil.isNotNull(tab))
			model.addAttribute("tabName", tab);
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入消息管理模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("message", true, model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.fn)
	public String financial(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入记账模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("financial", true, model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.dl)
	public String download(Model model, HttpServletRequest request){
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入下载模块首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("download", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.dlvs)
	public String downloadVersion(Model model, HttpServletRequest request){
		model.addAttribute("historys", appVersionService.getAllVersions());
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入下载历史版本页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("downloadVersion", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.dt +"/{bid}")
	public String detail(
			@PathVariable(value="bid") int blogId,
				Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		//检查博客id是否存在
		model.addAttribute("bid", blogId);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入博客详情页面："+ blogId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("detail", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.lg)
	public String login(Model model, HttpServletRequest request){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        
		int errorcode = StringUtil.changeObjectToInt(request.getParameter("errorcode"));
		if(errorcode > 0){
			currentUser.logout(); 
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(errorcode));
			model.addAttribute("error", true);
		}else{
	        if(currentUser.isAuthenticated()){
	        	return "redirect:/";
	        }
		}
		model.addAttribute("publicKey",  RSAKeyUtil.getInstance().getPublicKey());
		return loginRoleCheck("login", model, request);
	}
	
	//绑定微信
	@RequestMapping(ControllerBaseNameUtil.bw)
	public String bindWechat(Model model, 
			@RequestParam(value="FromUserName") String FromUserName,
			@RequestParam(value="currentType") String currentType,
			HttpServletRequest request){
		model.addAttribute("FromUserName", FromUserName);
		model.addAttribute("currentType", currentType);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "绑定微信账号", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("bind-wechat", model, request);
		
	}
	
	@RequestMapping(ControllerBaseNameUtil.my)
	public String my(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		UserBean user = getMustLoginUserFromShiro();
		model.addAttribute("uid", user.getId());
		model.addAttribute("uaccount", userHandler.getUserName(user.getId()));
		model.addAttribute("isLoginUser", true);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看个人中心", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("my", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.my + "/{uid}")
	public String my1(@PathVariable(value="uid") int uid, Model model, HttpServletRequest request){
        checkRoleOrPermission(model, request);
        UserBean user = getMustLoginUserFromShiro();
        model.addAttribute("uid", uid);
		model.addAttribute("uaccount", userHandler.getUserName(uid));
		model.addAttribute("isLoginUser", user != null && uid == user.getId());
		//保存访客记录
		visitorService.saveVisitor(user, "web网页端", DataTableType.心情.value, uid, ConstantsUtil.STATUS_NORMAL);

		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看"+ userHandler.getUserName(uid) +"个人中心", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("my", model, request);
	}
	
	@RequestMapping("user/{uid}/"+ ControllerBaseNameUtil.board)
	public String board(@PathVariable(value="uid") int uid, Model model, HttpServletRequest request){
		//获取当前的Subject  
        //Subject currentUser = SecurityUtils.getSubject();
        checkRoleOrPermission(model, request);
        UserBean user = getMustLoginUserFromShiro();
        model.addAttribute("uid", uid);
		model.addAttribute("uaccount", userHandler.getUserName(uid));
		model.addAttribute("isLoginUser", uid == user.getId());

		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看用户"+ userHandler.getUserName(uid) + "的留言板", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("board", model, request);	
	}
	
	@RequestMapping("user/{uid}/mood/{mid}/"+ ControllerBaseNameUtil.dt)
	public String moodDetail(@PathVariable(value="uid") int uid, @PathVariable(value="mid") int mid, Model model, HttpServletRequest request){
        checkRoleOrPermission(model, request);
        UserBean user = getUserFromShiro();
        model.addAttribute("uid", uid);
		model.addAttribute("mid", mid);
		model.addAttribute("isLoginUser", user != null && uid == user.getId());

		//校验是否可以查看该心情
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = moodHandler.getMoodDetail(mid, user);
		if(!CollectionUtils.isEmpty(list) && list.size() == 1) {
			Map<String, Object> mood = list.get(0);
			int moodCreateUserId = StringUtil.changeObjectToInt(mood.get("create_user_id"));
			int status = StringUtil.changeObjectToInt(mood.get("status"));
			Subject currentUser = SecurityUtils.getSubject();
			//非登录用户只能查看共享的心情
			if(status != ConstantsUtil.STATUS_SHARE){
				if(user == null)
					throw new MustLoginException();
			}
			if(user != null && moodCreateUserId != user.getId() && !currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && status == ConstantsUtil.STATUS_SELF)
				throw new UnauthorizedException("私有信息，您无法查看！");

		}else{
			 if(mid > 0)
				 //删除es缓存
				 elasticSearchUtil.delete(DataTableType.心情.value, mid);
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该心情不存在.value));
		}

		//保存访客记录
		if(user != null && uid != user.getId())
			visitorService.saveVisitor(user, "web网页端", DataTableType.心情.value, uid, ConstantsUtil.STATUS_NORMAL);

		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看用户"+ userHandler.getUserName(uid) + "的心情ID为:"+ mid, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("mood-detail", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.pb)
	public String publishBlog(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		int blogId = 0;
		String bidStr = request.getParameter("bid");
		if(StringUtil.isNotNull(bidStr))
			blogId = StringUtil.changeObjectToInt(bidStr);
		
		//是否隐藏头部
		String noHeaderStr1 = request.getParameter("noHeader");
		boolean noHeader1 = false;
		if(StringUtil.isNotNull(noHeaderStr1)){
			noHeader1 = StringUtil.changeObjectToBoolean(noHeaderStr1);
		}
		
		/*List<String> categorys = new ArrayList<String>();
		for(BlogCategory ts: EnumUtil.BlogCategory.values()){
			categorys.add(ts.name());
		}
		model.addAttribute("categorys", categorys);*/
		model.addAttribute("blogId", blogId);
		model.addAttribute("noHeader1", noHeader1);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入发布博客页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("publish-blog", true, model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.s)
	public String search(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入搜索页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("search", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.cs)
	public String chatSquare(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入聊天广场页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("chat-square", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.mt)
	public String materialIndex(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入素材管理模块页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("material/index", true, model, request);
	}

	/**
	 * 大事件入口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(ControllerBaseNameUtil.ev)
	public String eventIndex(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入大事件模块页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("event",  model, request);
	}

	/**
	 * 大事件管理
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(ControllerBaseNameUtil.ev + "/manage")
	public String eventManage(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入大事件管理模块页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return adminLoginRoleCheck("event-manage",  model, request);
	}
	
	/**
	 * 素材添加图片
	 * @param model
	 * @param type  类型，值可以是iframe或者其他
	 * @param request
	 * @return
	 */
	@RequestMapping(ControllerBaseNameUtil.mt +"/add")
	public String materialAdd(Model model, @RequestParam(value="type", required=false) String type, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		boolean iframe = false;
		if(StringUtil.isNotNull(type) && "iframe".equals(type))
			iframe = true;
		
		model.addAttribute("iframe", iframe);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入素材添加页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("material/add", true, model, request);
	}
	
	/**
	 * 素材选择图片
	 * @param model
	 * @param type  类型，值可以是iframe或者其他
	 * @param request
	 * @return
	 */
	@RequestMapping(ControllerBaseNameUtil.mt +"/select")
	public String materialSelect(Model model, 
			@RequestParam(value="type", required=false) String type, 
			@RequestParam(value="width", required=false) Integer width,  //窗体的宽度
			@RequestParam(value="select", required=false) Integer select, //最多选择的数量
			@RequestParam(value="selectType", required=false) Integer selectType, //选择的类型，0表示全部， 1表示图片， 2表示文件
			HttpServletRequest request){
		checkRoleOrPermission(model, request);
		boolean iframe = false;
		if(StringUtil.isNotNull(type) && "iframe".equals(type))
			iframe = true;
		
		if(width < 1)
			width = 100;
		
		if(select < 1)
			select = 3;
		
		if(selectType < 1)
			selectType = 0;
		
		model.addAttribute("iframe", iframe);
		model.addAttribute("width", width);
		model.addAttribute("select", select);
		model.addAttribute("selectType", selectType);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入素材选择页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("material/select", true, model, request);
	}
	
	/**
	 * 个人设置
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/my/setting")
	public String memberList(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = getMustLoginUserFromShiro();
		//获取页面初始化的信息
		model.addAllAttributes(userService.initSetting(user, getHttpRequestInfo(request)));

		model.addAttribute("user", user);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入个人设置页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("setting", true, model, request);
	}
	@RequestMapping("403")
	public String unauthorizedRole(Model model, HttpServletRequest request){
		//设置统一的请求模式
		model.addAttribute("isDebug", LeedanePropertiesConfig.newInstance().isDebug());
		return loginRoleCheck("p403", model, request);
	}
	
	@RequestMapping("404")
	public String nonexistence(Model model, HttpServletRequest request){
		//设置统一的请求模式
		model.addAttribute("isDebug", LeedanePropertiesConfig.newInstance().isDebug());
		String errorMessage = request.getParameter("errorMessage");
		if(StringUtil.isNotNull(errorMessage)){
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", errorMessage);
		}
		return loginRoleCheck("p404", model, request);
	}
	
	/**
	 * 空指针异常
	 * @param model
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("null-pointer")
	public String nullPointer(Model model, HttpServletRequest request) throws UnsupportedEncodingException{
		//设置统一的请求模式
		model.addAttribute("isDebug", LeedanePropertiesConfig.newInstance().isDebug());
		String errorMessage = request.getParameter("errorMessage");
		if(StringUtil.isNotNull(errorMessage)){
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", URLDecoder.decode(errorMessage, "UTF-8"));
		}
		
		return loginRoleCheck("pnull-pointer", model, request);
	}

	/**
	 *
	 * @param model
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	/*@RequestMapping("photo-index")
	public String photoIndex(Model model, HttpServletRequest request) throws UnsupportedEncodingException{
		//设置统一的请求模式
		model.addAttribute("isDebug", IS_DEBUG);
		return loginRoleCheck("photo-index", model, request);
	}
	*/
	/**
	 * 获取博客的内容
	 * @return
	 */
	@RequestMapping(value = "/content", method = RequestMethod.GET)
	public String getContent(@RequestParam("blog_id") int blogId,
			Model model, HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try{
			@SuppressWarnings("unchecked")
			BlogService<BlogBean> blogService = (BlogService<BlogBean>) SpringUtil.getBean("blogService");
			checkParams(message, request);

			operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "客户端查看博客详情，博客ID："+ blogId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
			String device_width = request.getParameter("device_width");
			
			if(blogId < 1){
				printWriter(message, response, start);
				return null;
			}
			//int blog_id = 1;
			String sql = "select content, read_number from "+DataTableType.博客.value+" where status = ? and id = ?";
			List<BlogBean> r = blogService.getBlogBeans(sql, ConstantsUtil.STATUS_NORMAL, blogId);				
			if(r.size() == 1){
				final BlogBean bean = r.get(0);
				
				//把更新读的信息提交到Rabbitmq队列处理
				new Thread(new Runnable() {
					@Override
					public void run() {
						ReadBean readBean = new ReadBean();
						readBean.setTableName(DataTableType.博客.value);
						readBean.setFroms(getHttpRequestInfo(request).getIp());
						readBean.setTableId(blogId);
						readBean.setCreateTime(new Date());
						readBean.setCreateUserId(getUserFromShiro() != null ? getUserFromShiro().getId(): -1);
						readBean.setStatus(ConstantsUtil.STATUS_NORMAL);
						ISend send = new AddReadSend(readBean);
						SendMessage sendMessage = new SendMessage(send);
						sendMessage.sendMsg();
					}
				}).start();
				
				if(StringUtil.isNotNull(bean.getContent())){
					String content = bean.getContent();
					Document contentHtml = Jsoup.parse(content);
					StringBuffer imgs = new StringBuffer();
					if(contentHtml != null){ 
						Elements elements = contentHtml.select("img");
						String imgUrl = null;
						int i = 0;
						for(Element element: elements){
							imgUrl = element.attr("src");
							imgs.append(imgUrl +";");
							//element.removeAttr("src");
							
							//添加网络链接
							if(StringUtil.isLink(imgUrl)){
								//element.attr("src", "http://pic.onlyloveu.top/click_to_look_picture.png");
								element.attr("onclick", "clickImg(this, "+i+");");
								if(StringUtil.isNotNull(device_width)){
									String style = element.attr("style");
									int deviceWidth = Integer.parseInt(device_width);
									if(StringUtil.isNotNull(style)){
										Map<String, String> mapStyles = JsoupUtil.styleToMap(style);
										int oldHeight = 0, oldWidth = 0;
										if(mapStyles.containsKey("height")){
											oldHeight = StringUtil.changeObjectToInt(mapStyles.get("height").replaceAll("px", ""));
										}
										
										if(mapStyles.containsKey("width")){
											oldWidth = StringUtil.changeObjectToInt(mapStyles.get("width").replaceAll("px", ""));
										}
										
										if(oldWidth < 1){
											oldWidth = deviceWidth;
										}
										
										if(oldHeight > 0 && oldHeight < oldWidth){
											oldHeight = oldWidth / oldHeight * deviceWidth;
										}
										
										if(oldHeight < 1){
											oldHeight = deviceWidth;
										}
										
										//style样式存在，但是同时也没有宽高，系统给它一个适配屏幕的宽高
										mapStyles.put("width", "100%");
										mapStyles.put("height", oldHeight +"px");
										element.attr("style", JsoupUtil.mapToStyle(mapStyles));
									}else if(StringUtil.isNull(style)){
										int height = 0;
										String heightString = element.attr("height");
										String widthString = element.attr("width");
										int oldWidth = 0;
										if(StringUtil.isNotNull(widthString)){
											widthString = widthString.replaceAll("px", "");
											oldWidth = StringUtil.changeObjectToInt(widthString);
											if(oldWidth < 1){
												oldWidth = deviceWidth;
											}
										}else{
											oldWidth = deviceWidth;
										}
										
										int oldHeight = 0;
										if(StringUtil.isNotNull(heightString)){
											heightString = heightString.replaceAll("px", "");
											oldHeight = StringUtil.changeObjectToInt(heightString);
										}else{
											oldHeight = deviceWidth;
										}
										
										
										if(oldWidth > 0 && oldHeight >= oldWidth)
											height = oldHeight / oldWidth * deviceWidth;
										else {
											height = oldHeight;
										}
										//图片没有限制宽高，系统给它一个适配屏幕的宽高
										element.removeAttr("width");
										element.removeAttr("height");
										element.attr("style", "width: 100%;height:"+height+"px");
									}
								}
								i++;
							}
							
						}
						content = contentHtml.html();
					}
					if(imgs.toString().endsWith(";")){
						model.addAttribute("imgs", imgs.toString().substring(0, imgs.toString().length() -1));
					}else{
						model.addAttribute("imgs", imgs.toString());
					}
					model.addAttribute("device_width", device_width);
					model.addAttribute("content", content);
					return "content-page";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("content", "抱歉,服务器获取博客失败");
		return "content-page";
		
	}
	
	/**
	 * 页面中转
	 * @param model
	 * @param token  
	 * @param ref
	 * @param request
	 * @return
	 */
	@RequestMapping("/transfer")
	public String transfer(Model model, @RequestParam("userId")int userId, @RequestParam("token")String token, @RequestParam("ref")String ref, HttpServletRequest request){
		Map<String, Object> message = new HashMap<String, Object>();
		boolean result = false;
		//校验tokn
		UserBean user = checkToken(token, userId, message, result);
		if(user == null)
			throw new MustLoginException();

		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
//		currentUser.getSession().setAttribute(UserController.USER_INFO_KEY, user);
		currentUser.getSession().setAttribute("nonav", true);
		return "forward:"+ ref;
	}
	/*public static void main(String[] args) {
		String text = "dkkdkf+kkfkf+";
		System.out.println(text.replaceAll("\\+", "*jia*"));
	}*/
}
