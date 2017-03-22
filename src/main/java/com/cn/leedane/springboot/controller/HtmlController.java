package com.cn.leedane.springboot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	public String index(Model model, HttpSession httpSession){
		return "index";
	}*/
	
	@RequestMapping("/")
	public String index1(Model model, HttpSession httpSession, HttpServletRequest request){
		//首页不需要验证是否登录
		return loginRoleCheck("index", model, httpSession, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpSession httpSession, HttpServletRequest request){
		return index1(model, httpSession, request);
	}
	
	@RequestMapping("/pt")
	public String photo(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("photo", true, model, httpSession, request);
	}
	
	@RequestMapping("/fn")
	public String financial(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("financial", true, model, httpSession, request);
	}
	
	@RequestMapping("/dl")
	public String download(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("download", model, httpSession, request);
	}
	
	@RequestMapping(value="/dt/{bid}")
	public String detail(
			@PathVariable(value="bid") int blogId,
				Model model, HttpSession httpSession, HttpServletRequest request){
		model.addAttribute("bid", blogId);
		return loginRoleCheck("detail", model, httpSession, request);
	}
	
	@RequestMapping("/lg")
	public String login(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("login", model, httpSession, request);
	}
	
	@RequestMapping("/my")
	public String my(Model model, HttpSession httpSession, HttpServletRequest request){
		Object o = httpSession.getAttribute(UserController.USER_INFO_KEY);
		if(o != null){
			UserBean user = (UserBean)o;
			model.addAttribute("uid", user.getId());
			model.addAttribute("isLoginUser", true);
		}
		
		return loginRoleCheck("my", true, model, httpSession, request);
	}
	
	@RequestMapping("/my/{uid}")
	public String my1(@PathVariable(value="uid") int uid, Model model, HttpSession httpSession, HttpServletRequest request){
		Object o = httpSession.getAttribute(UserController.USER_INFO_KEY);
		if(o != null){
			UserBean user = (UserBean)o;
			model.addAttribute("uid", uid);
			model.addAttribute("isLoginUser", uid == user.getId());
		}
		
		return loginRoleCheck("my", true, model, httpSession, request);
	}
	
	@RequestMapping("/pb")
	public String publishBlog(Model model, HttpSession httpSession, HttpServletRequest request){
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
		
		return loginRoleCheck("publish-blog", true, model, httpSession, request);
	}
	
	@RequestMapping("/s")
	public String search(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("search", model, httpSession, request);
	}
	
	@RequestMapping("/cs")
	public String chatSquare(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("chat-square", model, httpSession, request);
	}
	
	/**
	 * 校验地址，不校验是否登录
	 * @param urlParse
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck(urlParse, false, model, httpSession, request);
	}
	
	/**
	 * 校验地址，校验是否登录
	 * @param urlParse
	 * @param mustLogin 为true表示必须登录，不然就跳转到登录页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, boolean mustLogin, Model model, HttpSession httpSession, HttpServletRequest request){
		Object o = httpSession.getAttribute(UserController.USER_INFO_KEY);
		boolean isLogin = false;
		boolean isAdmin = false;
		if(o != null){
			isLogin = true;
			UserBean user = (UserBean)o;
			isAdmin = user.isAdmin();
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
