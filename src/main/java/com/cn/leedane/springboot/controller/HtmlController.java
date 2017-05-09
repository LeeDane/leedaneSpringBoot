package com.cn.leedane.springboot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.RoleController;
import com.cn.leedane.controller.UserController;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.AddReadSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.BlogCategory;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.cn.leedane.utils.JsoupUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * Html页面的控制器
 * @author LeeDane
 * 2017年3月16日 上午11:23:13
 * Version 1.0
 */
@Controller
public class HtmlController extends BaseController{
	
	@Autowired
	private UserService<UserBean> userService;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	/*@RequestMapping
	public String index(Model model){
		return "index";
	}*/
	
	@RequestMapping(value = {"/", ControllerBaseNameUtil.index})
	public String index1(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return loginRoleCheck("index", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.pt)
	public String photo(Model model, HttpServletRequest request){
		return loginRoleCheck("photo", true, model, request);
	}
	
	@RequestMapping("/test")
	public String test(Model model, HttpServletRequest request){
		return loginRoleCheck("test", false, model, request);
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
			
		return loginRoleCheck("message", true, model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.fn)
	public String financial(Model model, HttpServletRequest request){
		return loginRoleCheck("financial", true, model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.dl)
	public String download(Model model, HttpServletRequest request){
		return loginRoleCheck("download", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.dt +"/{bid}")
	public String detail(
			@PathVariable(value="bid") int blogId,
				Model model, HttpServletRequest request){
		
		//检查博客id是否存在
		
		model.addAttribute("bid", blogId);
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
		return loginRoleCheck("bind-wechat", model, request);
		
	}
	
	@RequestMapping(ControllerBaseNameUtil.my)
	public String my(Model model, HttpServletRequest request){
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
        	Object o = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	if(o != null){
        		UserBean user = (UserBean)o;
    			model.addAttribute("uid", user.getId());
    			model.addAttribute("isLoginUser", true);
    			return loginRoleCheck("my", true, model, request);
    		}
        }
        return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();	
	}
	
	@RequestMapping(ControllerBaseNameUtil.my + "/{uid}")
	public String my1(@PathVariable(value="uid") int uid, Model model, HttpServletRequest request){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        checkRoleOrPermission(request);
        
        if(currentUser.isAuthenticated()){
        	Object o = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	if(o != null){
    			UserBean user = (UserBean)o;
    			model.addAttribute("uid", uid);
    			model.addAttribute("isLoginUser", uid == user.getId());
    			return loginRoleCheck("my", true, model, request);
    		}
        }
		return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();	
	}
	
	@RequestMapping("user/{uid}/"+ ControllerBaseNameUtil.board)
	public String board(@PathVariable(value="uid") int uid, Model model, HttpServletRequest request){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        checkRoleOrPermission(request);
        
        if(currentUser.isAuthenticated()){
        	Object o = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	if(o != null){
    			UserBean user = (UserBean)o;
    			model.addAttribute("uid", uid);
    			model.addAttribute("uaccount", userHandler.getUserName(uid));
    			model.addAttribute("isLoginUser", uid == user.getId());
    			return loginRoleCheck("board", true, model, request);
    		}
        }
		return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();	
	}
	
	@RequestMapping("user/{uid}/mood/{mid}/"+ ControllerBaseNameUtil.dt)
	public String moodDetail(@PathVariable(value="uid") int uid, @PathVariable(value="mid") int mid, Model model, HttpServletRequest request){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        checkRoleOrPermission(request);
        
        if(currentUser.isAuthenticated()){
        	Object o = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	if(o != null){
    			UserBean user = (UserBean)o;
    			model.addAttribute("uid", uid);
    			model.addAttribute("mid", mid);
    			model.addAttribute("isLoginUser", uid == user.getId());
    			return loginRoleCheck("mood-detail", true, model, request);
    		}
        }
		return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();	
	}
	
	@RequestMapping(ControllerBaseNameUtil.pb)
	public String publishBlog(Model model, HttpServletRequest request){
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
		
		List<String> categorys = new ArrayList<String>();
		for(BlogCategory ts: EnumUtil.BlogCategory.values()){
			categorys.add(ts.name());
		}
		model.addAttribute("categorys", categorys);
		model.addAttribute("blogId", blogId);
		model.addAttribute("noHeader1", noHeader1);
		
		return loginRoleCheck("publish-blog", true, model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.s)
	public String search(Model model, HttpServletRequest request){
		return loginRoleCheck("search", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.cs)
	public String chatSquare(Model model, HttpServletRequest request){
		return loginRoleCheck("chat-square", model, request);
	}
	
	@RequestMapping("403")
	public String unauthorizedRole(Model model, HttpServletRequest request){
		return loginRoleCheck("p403", model, request);
	}
	
	@RequestMapping("404")
	public String nonexistence(Model model, HttpServletRequest request){
		String errorMessage = request.getParameter("errorMessage");
		if(StringUtil.isNotNull(errorMessage)){
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", errorMessage);
		}
		
		return loginRoleCheck("p404", model, request);
	}
	
	/**
	 * 校验地址，不校验是否登录
	 * @param urlParse
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, Model model, HttpServletRequest request){
		return loginRoleCheck(urlParse, false, model, request);
	}
	
	/**
	 * 校验地址，校验是否登录
	 * @param urlParse
	 * @param mustLogin 为true表示必须登录，不然就跳转到登录页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, boolean mustLogin, Model model, HttpServletRequest request){
		//设置统一的请求模式
		model.addAttribute("isDebug", false);
		Object o = null;
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
        	o = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        }
		
		boolean isLogin = false;
		boolean isAdmin = false;
		if(o != null){
			isLogin = true;
			UserBean user = (UserBean)o;
			isAdmin = currentUser.hasRole(RoleController.ADMIN_ROLE_CODE);
			model.addAttribute("account", user.getAccount());
			model.addAttribute("loginUserId", user.getId());
		}
		model.addAttribute("isLogin",  isLogin);
		model.addAttribute("isAdmin", isAdmin);
		if(mustLogin && !isLogin){
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请先登录.value));
			return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();
		}
		
		return StringUtil.isNotNull(urlParse) ? urlParse : "404";
	}

	
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
						ISend send = new AddReadSend(bean);
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
								//element.attr("src", "http://7xnv8i.com1.z0.glb.clouddn.com/click_to_look_picture.png");
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
					blogService.updateReadNum(blogId, bean.getReadNumber());
					return "content-page";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("content", "抱歉,服务器获取博客失败");
		return "content-page";
		
	}
}
