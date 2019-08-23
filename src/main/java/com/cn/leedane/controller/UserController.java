package com.cn.leedane.controller;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.WechatHandler;
import com.cn.leedane.lucene.solr.UserSolrHandler;
import com.cn.leedane.model.*;
import com.cn.leedane.service.FriendService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserTokenService;
import com.cn.leedane.shiro.CustomAuthenticationToken;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.SolrAddThread;
import com.cn.leedane.thread.single.SolrUpdateThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.PlatformType;
import com.cn.leedane.wechat.bean.WeixinCacheBean;
import com.cn.leedane.wechat.util.WeixinUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;
/**
 * 用户管理controller,restful风格接口
 * @author T5-SK
 *
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.us)
public class UserController extends BaseController{
	
	private Logger logger = Logger.getLogger(getClass());
	
//	public static final String USER_INFO_KEY = "user_info";
	
	@Autowired
	private WechatHandler wechatHandler;
	
	//好友信息
	@Autowired
	private FriendService<FriendBean> friendService; 
	
	// 操作日志
	@Autowired
	protected OperateLogService<OperateLogBean> operateLogService;
	
    
    @Autowired
	private UserTokenService<UserTokenBean> userTokenService;
	
	/**
	 * 登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})  
	public Map<String, Object> login(@RequestParam(value="account", required = false) String username,
			@RequestParam(value="pwd", required = false) String password,
									 @RequestParam(value="remember", required = false) boolean remember,
									 @RequestParam(value="code", required = false) String code,
			Model model, /*HttpSession session,*/
			HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		boolean isSuccess = false;
		checkParams(message, request);
		
		if(StringUtil.isNull(username) || StringUtil.isNull(password)){
			JSONObject json = getJsonFromMessage(message);
			username = JsonUtil.getStringValue(json, "account");
			password = JsonUtil.getStringValue(json, "pwd");
			remember = JsonUtil.getBooleanValue(json, "remember");
			code = JsonUtil.getStringValue(json, "code");
		}

		boolean isAndroidRequest = CommonUtil.isAndroidRequest(request);
		//只有网页端才检验验证码
		if(!isAndroidRequest)
			if(StringUtil.isNull(code) || !CodeUtil.checkVerifyCode(request, code)){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请输入正确验证码.value));
				message.put("responseCode", EnumUtil.ResponseCode.请输入正确验证码.value);
				return message.getMap();
			}
		
		if(StringUtil.isNull(username) || StringUtil.isNull(password)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.账号或密码为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.账号或密码为空.value);
			return message.getMap();
		}else{
			byte[] decodedData;
			
			try {
				decodedData = RSACoder.decryptByPrivateKey(password, RSAKeyUtil.getInstance().getPrivateKey());
				password = new String(decodedData, "UTF-8");
			} catch (Exception e1) {
				e1.printStackTrace();
				message.put("message", "该页面过期, 请刷新当前页面，重新操作！");
				message.put("responseCode", EnumUtil.ResponseCode.RSA加密解密异常.value);
				return message.getMap();
			}
			
			//获取登录失败的数量
			int number = userHandler.getLoginErrorNumber(username);
			if(number > 5){
				Date date = userHandler.getLoginErrorTime(username);
				if(date != null){
					//是否在禁止5分钟内
					if(DateUtil.isInMinutes(new Date(), date, 5)){
						//计算还剩下几分钟
						int minutes = DateUtil.leftMinutes(new Date(), date);
						if(minutes > 1){
							message.put("message", "由于您的账号失败连续超过5次，系统已限制您5分钟内不能登录,大概还剩余"+ minutes +"分钟");
						}else{
							message.put("message", "由于您的账号失败连续超过5次，系统已限制您5分钟内不能登录,大概还剩余"+ DateUtil.leftSeconds(new Date(), date) +"秒");
						}
						
						message.put("responseCode", EnumUtil.ResponseCode.您的账号登录失败太多次.value);
						return message.getMap();
					}
				}
			}

			CustomAuthenticationToken authenticationToken = new CustomAuthenticationToken();
			authenticationToken.setUsername(username);
			authenticationToken.setPassword(password.toCharArray());
			//这里只负责获取用户，不做校验，校验交给shiro的realm里面去做
	        UserBean user = userHandler.getUserBean(username, password);
	        authenticationToken.setUser(user);
	        //authenticationToken.setRememberMe(remember);
			
	        //获取当前的Subject  
	        Subject currentUser = SecurityUtils.getSubject();  
	        logger.info("对用户[" + username + "]进行登录验证..验证开始");  
            currentUser.login(authenticationToken);
            logger.info("对用户[" + username + "]进行登录验证..验证通过");  
	        /*try {  
	            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查  
	            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应  
	            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法  
	            logger.info("对用户[" + username + "]进行登录验证..验证开始");  
	            currentUser.login(authenticationToken);
	            logger.info("对用户[" + username + "]进行登录验证..验证通过");  
	        }catch(UnknownAccountException uae){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");  
	            redirectAttributes.addFlashAttribute("message", "未知账户");  
	        }catch(BannedAccountException ba){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,用户已经被禁言了");  
	            redirectAttributes.addFlashAttribute("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已被禁言.value));  
	        }catch(CancelAccountException ca){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,用户已经注销了");  
	            redirectAttributes.addFlashAttribute("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已经注销.value));  
	        }catch(StopUseAccountException sua){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,用户暂时被禁止使用");  
	            redirectAttributes.addFlashAttribute("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已被禁止使用.value));  
	        }catch(NoValidationEmailAccountException nveca){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,用户未验证邮箱");  
	            redirectAttributes.addFlashAttribute("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先验证邮箱.value));  
	        }catch(NoActiveAccountException naa){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,用户未激活");  
	            redirectAttributes.addFlashAttribute("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.注册未激活账户.value));  
	        }catch(NoCompleteAccountException naa){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,用户未完善信息");  
	            redirectAttributes.addFlashAttribute("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.未完善信息.value));  
	        }catch(IncorrectCredentialsException ice){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");  
	            redirectAttributes.addFlashAttribute("message", "密码不正确");  
	        }catch(LockedAccountException lae){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");  
	            redirectAttributes.addFlashAttribute("message", "账户已锁定");  
	        }catch(ExcessiveAttemptsException eae){  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");  
	            redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");  
	        }catch(AuthenticationException ae){  
	            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景  
	            logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");  
	            ae.printStackTrace();  
	            redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");  
	        } */
	        
	        //验证是否登录成功  
	        if(currentUser.isAuthenticated()){  
	            logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
	            
//	            currentUser.getSession().setAttribute(USER_INFO_KEY, user);
	            currentUser.getSession().setAttribute("hasnav", true);
	            //获取平台，如果是android就继续获取token
	            
	            Map<String, Object> userinfo = userHandler.getUserInfo(user, true);
	            if(isAndroidRequest){
	            	UserTokenBean userTokenBean = new UserTokenBean();
	            	Date overdue = DateUtil.getOverdueTime(new Date(), "7天");
	            	userTokenBean.setToken(StringUtil.getUserToken(String.valueOf(user.getId()), user.getSecretCode(), overdue));
	            	userTokenBean.setCreateTime(new Date());
	            	userTokenBean.setCreateUserId(user.getId());
	            	userTokenBean.setOverdue(overdue);
	            	userTokenBean.setStatus(ConstantsUtil.STATUS_NORMAL);
	            	ResponseMap responseMap = userTokenService.addUserToken(user, userTokenBean.getToken(), overdue, getHttpRequestInfo(request));
	            	if(StringUtil.changeObjectToBoolean(responseMap.get("isSuccess"))){
	            		userinfo.put("token", userTokenBean.getToken());
	            	}else{
	            		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.token获取异常.value));
	            		message.put("responseCode", EnumUtil.ResponseCode.token获取异常.value);
	            		return message.getMap();
	            	}
	            }
	            
				userHandler.removeLoginErrorNumber(username);
				message.put("userinfo", userinfo);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.恭喜您登录成功.value));
				message.put("responseCode", EnumUtil.ResponseCode.恭喜您登录成功.value);
				isSuccess = true;
				message.put("isSuccess", isSuccess);
				//对session进行添加管理
				/*Session session = SecurityUtils.getSubject().getSession();
				HttpRequestInfoBean requestInfoBean = getHttpRequestInfo(request);
				if(requestInfoBean != null){
					session.setAttribute("ip", requestInfoBean.getIp());
					session.setAttribute("location", requestInfoBean.getLocation());
				}
				session.setAttribute("time", DateUtil.DateToString(new Date()));
				SessionManagerUtil.getInstance().addSession(session, user.getId());*/
				Session session = SecurityUtils.getSubject().getSession();
				/*session.setAttribute("remember", remember);
				if(remember)
					session.setTimeout(-1001L);
				else*/
					session.setTimeout(1000 * 60 * 60); //设置一个小时过期
				SessionManagerUtil.getInstance().addSession(SecurityUtils.getSubject(), session, user.getId());
	        }else{  
	        	authenticationToken.clear(); 
				number = userHandler.addLoginErrorNumber(username);	
				if(number > 5){
					message.put("message", "您的账号已经连续登陆失败"+number+"次，账号已被限制5分钟");
				}else{
					message.put("message", "您的账号已经连续登陆失败"+number+"次，还剩下" +(5- number)+"次");
				}
				message.put("responseCode", EnumUtil.ResponseCode.账号或密码不匹配.value);
	        }
			
			// 保存用户登录日志信息
			String subject = user != null ? user.getAccount()+"登录系统": "账号" + username + "登录系统失败";
			this.operateLogService.saveOperateLog(user, getHttpRequestInfo(request) , new Date(), subject, "账号登录", (isSuccess ? 1: 0), EnumUtil.LogOperateType.内部接口.value);
		}
		return message.getMap();
	}
	
	/**
	 * 根据用户id获取该用户的个人中心
	 * @return
	 *//*
	@RequestMapping("/searchUserByUserIdOrAccount")
	public String searchUserByUserIdOrAccount(HttpServletRequest request){
		Map<String, Object> message = new HashMap<String, Object>();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response);
				return null;
			}
			JSONObject json = getJsonFromMessage(message);
			UserBean user = getUserFromMessage(message);
			//获取查找的用户id
			int searchUserId = JsonUtil.getIntValue(json, "searchUserId");
			//执行密码等信息的验证
			UserBean searchUser = userService.findById(searchUserId);

			if (searchUser != null) {			
				
				// 保存操作日志信息
				String subject = user.getAccount() + "查看" + searchUser.getAccount() + "个人基本信息";
				this.operateLogService.saveOperateLog(user, request, new Date(), subject, "searchUserByUserId", 1, 0);
				message.put("userinfo", userHandler.getUserInfo(searchUser, false));
				message.put("isSuccess", true);
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户不存在或请求参数不对.value));
				message.put("responseCode", EnumUtil.ResponseCode.用户不存在或请求参数不对.value);
			}
			printWriter(message, response);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response);
		return null;
	}*/
	
	/**
	 * 根据ID或者用户名称获取该用户的个人中心
	 * @return
	 */
	@RequestMapping(value="/searchByIdOrAccount", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> searchUserByUserIdOrAccount(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getMap();
		}
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.searchUserByUserIdOrAccount(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 注册用户
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/register", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> registerUser(Model model, HttpServletRequest request) throws Exception {		
		//判断是否有在线的用户，那就先取消该用户的session
		/*if(ActionContext.getContext().getSession().get(ConstantsUtil.USER_SESSION) != null) {
			removeMultSession(ConstantsUtil.USER_SESSION);
		}*/
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
			//return message.getMap();
		checkRoleOrPermission(model, request);
		JSONObject json = getJsonFromMessage(message);

		UserBean user = new UserBean();
		user.setAccount(JsonUtil.getStringValue(json, "account"));
		user.setEmail(JsonUtil.getStringValue(json, "email"));
		user.setPassword(MD5Util.compute(JsonUtil.getStringValue(json, "password")));
		Date registerTime = new Date();
		user.setRegisterTime(registerTime);
		user.setRegisterCode(StringUtil.produceRegisterCode(DateUtil.DateToString(registerTime, "YYYYMMDDHHmmss"),
				JsonUtil.getStringValue(json, "account")));
		user.setSecretCode(UUID.randomUUID().toString());
		message.putAll(userService.saveUser(user));
		//保存操作日志
		this.operateLogService.saveOperateLog(OptionUtil.adminUser, getHttpRequestInfo(request), null, user.getAccount()+"注册成功", "register", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	/**
	 * 完成注册
	 * @return
	 */
	@RequestMapping(value="/register/complete", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> completeRegister(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		/*try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			if (this.userService.updateCheckRegisterCode(JsonUtil.getStringValue(getJsonFromMessage(message), "registerCode")))
				return "completeRegisterSuccess";
			else
				return "completeRegisterFailure";
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);*/
		return message.getMap();
		
	}
	
	/**
	 * 再次发送邮箱验证信息
	 * @throws Exception 
	 */
	@RequestMapping(value="/register/againSend", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> againSendRegisterEmail(Model model, HttpServletRequest request) throws Exception{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		JSONObject json = getJsonFromMessage(message);
		//根据账号和密码找到该用户(密码需要再进行MD5加密)
		UserBean user = userService.loginUser(JsonUtil.getStringValue(json, "account"), JsonUtil.getStringValue(json, "password"));
		this.operateLogService.saveOperateLog(user, getHttpRequestInfo(request), null, user.getAccount()+"请求发送邮箱", "againSendRegisterEmail", 1, EnumUtil.LogOperateType.内部接口.value);
		if(user != null && user.getStatus() == 2){
			//生成注册码
			String newRegisterCode = StringUtil.produceRegisterCode(DateUtil.DateToString(new Date(),"YYYYMMDDHHmmss"),
					user.getAccount());
			user.setRegisterCode(newRegisterCode);
			boolean isUpdate = userService.update(user);
				if(isUpdate){
					//发送邮件
					userService.sendEmail(user);
					
					//异步修改用户solr索引
					new ThreadUtil().singleTask(new SolrUpdateThread<UserBean>(UserSolrHandler.getInstance(), user));
					//UserSolrHandler.getInstance().updateBean(user);
				}
					
				message.put("isSuccess", true);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.邮件已发送成功.value));
				message.put("responseCode", EnumUtil.ResponseCode.邮件已发送成功.value);
				return message.getMap();
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.不是未注册状态邮箱不能发注册码.value));
			message.put("responseCode", EnumUtil.ResponseCode.不是未注册状态邮箱不能发注册码.value);
			return message.getMap();
		}
	}
	
	/**
	 * 找回密码
	 * @return
	 */
	@RequestMapping(value="/findPwd", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> findPassword(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		JSONObject json = getJsonFromMessage(message);
		//获得找回密码的类型(0:邮箱,1:手机)
		if(JsonUtil.getIntValue(json, "type") == 0){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.暂时不支持手机找回密码功能.value));
			message.put("responseCode", EnumUtil.ResponseCode.暂时不支持手机找回密码功能.value);
		}else if(JsonUtil.getIntValue(json, "type") == 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.暂时不支持邮箱找回密码功能.value));
			message.put("responseCode", EnumUtil.ResponseCode.暂时不支持邮箱找回密码功能.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.未知的找回密码类型.value));
			message.put("responseCode", EnumUtil.ResponseCode.未知的找回密码类型.value);
		}
		
		return message.getMap();
		//this.operateLogService.saveOperateLog(user, request, null, user.getAccount()+"寻找密码", "findPassword", resIsSuccess? 1 : 0, 0);
	}
	
	/**
	 * 退出系统
	 * @return
	 */
	@RequestMapping(value="/logout", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> logout(Model model, HttpServletRequest request){
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), new Date(), "安全退出系统", "logout", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		ResponseMap message = new ResponseMap();
		/*HttpSession session = request.getSession();
		//判断是否有在线的用户，那就先取消该用户的session
		if(session.getAttribute(USER_INFO_KEY) != null) {
			UserBean user = (UserBean) session.getAttribute(USER_INFO_KEY);
			try {
				session.removeAttribute(USER_INFO_KEY);
				this.operateLogService.saveOperateLog(user, request, null, user.getAccount()+"退出系统", "logout", 1, 0);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.注销成功.value));
				message.put("responseCode", EnumUtil.ResponseCode.注销成功.value);
				message.put("isSuccess", true);
				SessionManagerUtil.getInstance().removeSession(user.getId());
				return message.getMap();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.注销成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.注销成功.value);
			message.put("isSuccess", true);
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);*/
		//SecurityUtils.getSecurityManager().getSession(key)
		//String username = String.valueOf(SecurityUtils.getSubject().getPrincipal());
		//Subject sb = sessionManager.get("1");

		try{
			//移除出缓存的在线用户列表
			//使用权限管理工具进行用户的退出，跳出登录，给出提示信息
			//对session进行移除
			/*Subject requestSubject = new Subject.Builder().session(SecurityUtils.getSubject().getSession()).buildSubject();
			SecurityUtils.getSecurityManager().logout(requestSubject);*/
//			SecurityUtils.getSecurityManager().logout();
//			SecurityUtils.getSubject().logout();
			SessionManagerUtil.getInstance().removeSession(SecurityUtils.getSubject().getSession(), true);
//			SecurityUtils.getSubject().logout();
//			SecurityUtils.getSecurityManager().logout();
		}catch(UnknownSessionException e){
			logger.info("UnknownSessionException ------");
		}catch(Exception e){
			logger.info("Exception ------");
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.注销成功.value));
		message.put("responseCode", EnumUtil.ResponseCode.注销成功.value);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	/**
	 * 将别人踢出系统
	 * @return
	 */
	@RequestMapping(value="/logoutOther", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> logoutOther(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();

		checkRoleOrPermission(model, request);
		message.putAll(userService.logoutOther(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 根据用户的id获取用户的base64位图像信息
	 * {"uid":2, "size":"30x30"} "order":0默认是0, tablename:"t_user"
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/head/img", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getHeadBase64StrById(Model model, HttpServletRequest request) throws Exception{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.put("message", userService.getHeadBase64StrById(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	/**
	 * 获取用户的头像路径
	 * @return
	 */
	@RequestMapping(value="/head/path", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getHeadPath(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		String picSize = JsonUtil.getStringValue(getJsonFromMessage(message), "picSize", "30x30");
		message.put("message", userHandler.getUserPicPath(getUserFromMessage(message).getId(), picSize));
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	/**
	 * 用户上传个人的头像
	 * {"base64":"hhdjshuffnfbnfds"}
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/head", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> uploadHeadBase64Str(Model model, HttpServletRequest request) throws Exception{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		UserBean user = getUserFromMessage(message);
		message.put("isSuccess", userService.uploadHeadBase64StrById(getJsonFromMessage(message), user, getHttpRequestInfo(request)));
		operateLogService.saveOperateLog(user, getHttpRequestInfo(request), null, user.getAccount()+"上传头像" + StringUtil.getSuccessOrNoStr(true), "uploadHeadBase64Str", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	/**
	 * 取得所有用户
	 * @return
	 */
	@RequestMapping(value="/users", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getAllUsers(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		String page = request.getParameter("page");
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		String sort = "";
		sort = request.getParameter("sort");
		UserBean user = OptionUtil.adminUser;
		if(user == null){
			message.put("resmessage", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value));
			return message.getMap();
		}
		
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
		
		//要是有一个为空，说明只加载
		if(StringUtil.isNull(page) || StringUtil.isNull(start) || StringUtil.isNull(limit)){
			
		}else{
			//int p = Integer.parseInt(page);
			int s = Integer.parseInt(start);
			int l = Integer.parseInt(limit);
			if(!StringUtil.isNull(sort)){
				JSONArray ja = JSONArray.fromObject(sort);
				if(ja != null){
					sort = "order by " + ja.getJSONObject(0).getString("property") + " " + ja.getJSONObject(0).getString("direction") + " ";
				}
			}
			sort = sort == null || sort.equals("") ? " " : sort + " ";
			int total = userService.total(DataTableType.用户.value, "id", " where status=1 ");
			ls = userService.find4MoreUser(sort + "limit ?,?", s, l);
			buildGetAllUserResp(ls,total);
		}
		this.operateLogService.saveOperateLog(user, getHttpRequestInfo(request), null, user.getAccount()+"查看所有用户", "getAllUsers", 1, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	/**
	 * 统计所有用户的年龄
	 * @return
	 */
	@RequestMapping(value="/statistics/age", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> statisticsUserAge(HttpServletRequest request){
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();		
		ls = userService.statisticsUserAge();
		ResponseMap message = new ResponseMap();		
		message.put("xaxis", "统计所有用户的年龄"); //X轴名称
		message.put("yaxis", "人数"); //Y轴名称
		message.put("maximum", getMaximum(ls)); //年龄段人数最多的数字
		message.put("data", ls);
//		this.operateLogService.saveOperateLog(getUserFromMessage(message), request, null, "统计所有用户的年龄", "getAllUsers", 1, 0);
		return message.getMap();
	}
	
	/**
	 * 统计所有用户的年龄段
	 * @return
	 */
	@RequestMapping(value="/statistics/ageRang", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> statisticsUserAgeRang(HttpServletRequest request){
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();		
		ls = userService.statisticsUserAgeRang();
		ResponseMap message = new ResponseMap();
		message.put("xaxis", "统计所有用户的年龄"); //X轴名称
		message.put("yaxis", "人数"); //Y轴名称
		message.put("maximum", getMaximum(ls)); //Y轴人数最多的数字
		message.put("data", ls);
		return message.getMap();
	}
	
	/**
	 * 统计所有用户的注册时间的年份
	 * @return
	 */
	@RequestMapping(value="/statistics/registerYear", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> statisticsUserRegisterByYear(HttpServletRequest request){
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();		
		ls = userService.statisticsUserRegisterByYear();	
		ResponseMap message = new ResponseMap();
		message.put("xaxis", "统计所有用户的注册时间的年份"); //X轴名称
		message.put("yaxis", "人数"); //Y轴名称
		message.put("maximum", getMaximum(ls)); //Y轴人数最多的数字
		message.put("data", ls);
		return message.getMap();
	}
	
	/**
	 * 统计所有用户的注册时间的月份
	 * @return
	 */
	@RequestMapping(value="/statistics/registerMonth", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> statisticsUserRegisterByMonth(HttpServletRequest request){
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();		
		ls = userService.statisticsUserRegisterByMonth();
		ResponseMap message = new ResponseMap();
		message.put("xaxis", "统计所有用户的注册时间的月份"); //X轴名称
		message.put("yaxis", "人数"); //Y轴名称
		message.put("maximum", getMaximum(ls)); //Y轴人数最多的数字
		message.put("data", ls);
		return message.getMap();
	}
	
	/**
	 * 统计所有用户的最近一个月的注册人数
	 * @return
	 */
	@RequestMapping(value="/statistics/registerNearMonth", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> statisticsUserRegisterByNearMonth(HttpServletRequest request){
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();		
		ls = userService.statisticsUserRegisterByNearMonth();
		ResponseMap message = new ResponseMap();
		message.put("xaxis", "统计所有用户的最近一个月的注册人数"); //X轴名称
		message.put("yaxis", "人数"); //Y轴名称
		message.put("maximum", getMaximum(ls)); //Y轴人数最多的数字
		message.put("data", ls);
		return message.getMap();
	}
	
	/**
	 * 统计所有用户的最近一周的注册人数
	 * @return
	 */
	@RequestMapping(value="/statistics/registerNearWeek", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> statisticsUserRegisterByNearWeek(HttpServletRequest request){
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();		
		ls = userService.statisticsUserRegisterByNearWeek();
		ResponseMap message = new ResponseMap();
		message.put("xaxis", "统计所有用户的最近一周的注册人数"); //X轴名称
		message.put("yaxis", "人数"); //Y轴名称
		message.put("maximum", getMaximum(ls)); //Y轴人数最多的数字
		message.put("data", ls);
		return message.getMap();
	}
	
	/**
	 * 构建获得全部用户返回响应的数据
	 */
	private void buildGetAllUserResp(List<Map<String, Object>> ls, int total){
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("rows", ls);
		message.put("total", total);	
	}
	
	/**
	 * 获得y轴中人数最多的数量
	 * @param ls
	 * @return
	 */
	private int getMaximum(List<Map<String, Object>> ls) {
		int maximum = 0;
		if(ls == null || ls.size() == 0)
			return maximum;
		for(Map<String, Object> m: ls){
			int i = StringUtil.changeObjectToInt(m.get("yaxis"));
			if(i > maximum){
				maximum = i;
			}
		}
		
		return maximum;
	}
	
	/**
	 * 获取手机注册的验证码
	 * @return
	 */
	@RequestMapping(value="/phone/register/code", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getPhoneRegisterCode(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.getPhoneRegisterCode(getJsonFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取手机登录的验证码
	 * @return
	 */
	@RequestMapping(value="/phone/login/code", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getPhoneLoginCode(@RequestParam("mobilePhone") String mobilePhone, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.getPhoneLoginCode(mobilePhone, getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 通过手机注册
	 * @return
	 */
	@RequestMapping(value="/phone/register", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> registerByPhone(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		UserBean user = userService.registerByPhone(getJsonFromMessage(message), getHttpRequestInfo(request));
		if(user == null){
			message.put("message", "用户不存在或者参数不正确");
			return message.getMap();
		}else{
			if(user.getStatus() == 4 ){
				message.put("message", "用户已经被注销,有疑问请联系客服");
				return message.getMap();
			}else if(user.getStatus() == 0){
				message.put("message", "请先登录邮箱完成注册...");
				return message.getMap();
			}else{
				
				//异步添加用户solr索引
				new ThreadUtil().singleTask(new SolrAddThread<UserBean>(UserSolrHandler.getInstance(), user));
				//UserSolrHandler.getInstance().addBean(user);
				
				message.put("userinfo", userHandler.getUserInfo(user, true));
				message.put("isSuccess", true);
				message.put("message", "登录成功，正在为您跳转...");
				return message.getMap();
			}
		}
	}
	
	/**
	 * 通过手机注册(为了测试需要提供的接口)
	 * @return
	 */
	@RequestMapping(value="/phone/register/noValidate", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> registerByPhoneNoValidate(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(userService.registerByPhoneNoValidate(getJsonFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	/**
	 * 通过手机登录(登录用post请求)
	 * @return
	 */
	@RequestMapping(value="/phone/login", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> loginByPhone(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		checkRoleOrPermission(model, request);
		
		UserBean user = userService.loginByPhone(getJsonFromMessage(message), getHttpRequestInfo(request));
		if(user == null){
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户不存在或请求参数不对.value));
		}else{
			if(user.getStatus() == ConstantsUtil.STATUS_NO_TALK){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已被禁言.value));
				message.put("responseCode", EnumUtil.ResponseCode.用户已被禁言.value);
			}else if(user.getStatus() == ConstantsUtil.STATUS_DELETE){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已经注销.value));
				message.put("responseCode", EnumUtil.ResponseCode.用户已经注销.value);
			}else if(user.getStatus() == ConstantsUtil.STATUS_DISABLE){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已被禁止使用.value));
				message.put("responseCode", EnumUtil.ResponseCode.用户已被禁止使用.value);
			}else if(user.getStatus() == ConstantsUtil.STATUS_NO_VALIDATION_EMAIL){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先验证邮箱.value));
				message.put("responseCode", EnumUtil.ResponseCode.请先验证邮箱.value);
			}else if(user.getStatus() == ConstantsUtil.STATUS_NO_ACTIVATION){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.注册未激活账户.value));
				message.put("responseCode", EnumUtil.ResponseCode.注册未激活账户.value);
			}else if(user.getStatus() == ConstantsUtil.STATUS_INFORMATION){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.未完善信息.value));
				message.put("responseCode", EnumUtil.ResponseCode.未完善信息.value);
			}else if(user.getStatus() == ConstantsUtil.STATUS_NORMAL){
				Map<String, Object> userinfo = userHandler.getUserInfo(user, true);
				
				//获取平台，如果是android就继续获取token
	            String platform = request.getHeader("platform");
	            if(StringUtil.isNotNull(platform) && PlatformType.安卓版.value.equals(platform)){
	            	UserTokenBean userTokenBean = new UserTokenBean();
	            	Date overdue = DateUtil.getOverdueTime(new Date(), "7天");
	            	userTokenBean.setToken(StringUtil.getUserToken(String.valueOf(user.getId()), user.getPassword(), overdue));
	            	userTokenBean.setCreateTime(new Date());
	            	userTokenBean.setCreateUserId(user.getId());
	            	userTokenBean.setOverdue(overdue);
	            	userTokenBean.setStatus(ConstantsUtil.STATUS_NORMAL);
	            	ResponseMap responseMap = userTokenService.addUserToken(user, userTokenBean.getToken(), overdue, getHttpRequestInfo(request));
	            	if(StringUtil.changeObjectToBoolean(responseMap.get("isSuccess"))){
	            		userinfo.put("token", userTokenBean.getToken());
	            	}else{
	            		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.token获取异常.value));
	            		message.put("responseCode", EnumUtil.ResponseCode.token获取异常.value);
	            		return message.getMap();
	            	}
	            }
	            
				userHandler.removeLoginErrorNumber(user.getAccount());
				message.put("userinfo", userinfo);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.恭喜您登录成功.value));
				message.put("responseCode", EnumUtil.ResponseCode.恭喜您登录成功.value);
				message.put("isSuccess", true);
				return message.getMap();
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.非正常登录状态.value));
				message.put("responseCode", EnumUtil.ResponseCode.非正常登录状态.value);
			}
		}
		return message.getMap();
	}
	
	
	/**
	 * 检查账号是否已经存在
	 * @return
	 */
	@RequestMapping(value="/check/account", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> checkAccount(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.checkAccount(getJsonFromMessage(message), getHttpRequestInfo(request), getUserFromMessage(message)));
		return message.getMap();
	}
	
	/**
	 * 绑定微信账号
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/wechat/bind", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> bindWechat(HttpServletRequest request,
			@RequestParam("account") String account,
			@RequestParam("password") String password,
			@RequestParam("FromUserName") String FromUserName,
			@RequestParam("code") String code,
			@RequestParam(value = "currentType", required=false) String currentType){
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(currentType))
			currentType = WeixinUtil.MODEL_MAIN_MENU;

		if(StringUtil.isNull(code) || !CodeUtil.checkVerifyCode(request, code)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请输入正确验证码.value));
			message.put("responseCode", EnumUtil.ResponseCode.请输入正确验证码.value);
			return message.getMap();
		}

		CustomAuthenticationToken authenticationToken = new CustomAuthenticationToken();
		authenticationToken.setUsername(account);
		authenticationToken.setPassword(password.toCharArray());
		//这里只负责获取用户，不做校验，校验交给shiro的realm里面去做
		//执行绑定
		UserBean user = userService.bindByWeChat(FromUserName, account, password);
        authenticationToken.setUser(user);
        authenticationToken.setRememberMe(true);
        authenticationToken.setFromUserName(FromUserName);
        //获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();  
        logger.info("对用户[" + account + "]进行微信绑定验证..验证开始");  
        currentUser.login(authenticationToken);
        logger.info("对用户[" + account + "]进行微信绑定验证..验证通过"); 
        //验证是否登录成功  
        if(currentUser.isAuthenticated()){  
            logger.info("用户[" + account + "]微信绑定认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
            WeixinCacheBean cacheBean = new WeixinCacheBean();
			cacheBean.setBindLogin(true);
			cacheBean.setCurrentType(currentType);
			cacheBean.setLastBlogId(0);
			wechatHandler.addCache(FromUserName, cacheBean);
			
			//异步修改用户solr索引
			new ThreadUtil().singleTask(new SolrUpdateThread<UserBean>(UserSolrHandler.getInstance(), user));
			//UserSolrHandler.getInstance().updateBean(user);	
			
//            currentUser.getSession().setAttribute(USER_INFO_KEY, user);
			message.put("message", user.getId());
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			message.put("isSuccess", true);		
			// 保存用户绑定日志信息
//			operateLogService.saveOperateLog(user, request, new Date(), subject, "bingWechat", 1, 0);
			operateLogService.saveOperateLog(null, getHttpRequestInfo(request), null, "绑定账号微信账号："+ FromUserName +"成功", "bindWechat", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
        }else{
        	message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.密码不正确.value));
			message.put("responseCode", EnumUtil.ResponseCode.密码不正确.value);
        }
		return message.getMap();
	}
	
	/**
	 * 获取用户的基本数据(评论数，转发数，积分)
	 * @return
	 */
	@RequestMapping(value="/user/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getUserInfoData(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.getUserInfoData(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 更新用户的基本信息
	 * @return
	 */
	@RequestMapping(value="/user/base", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateUserBase(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.updateUserBase(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 更新登录密码
	 * @return
	 */
	@RequestMapping(value = "/user/pwd", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updatePassword(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.updatePassword(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 扫码登陆验证
	 * @return
	 */
	@RequestMapping(value = "/scan/login", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> scanLogin(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.scanLogin(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 扫码登陆验证
	 * @return
	 */
	@RequestMapping(value = "/scan/check", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> checkToken(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isRemembered() || currentUser.isAuthenticated()){
//        	currentUser.getSession().setAttribute(USER_INFO_KEY, getUserFromMessage(message));
        	message.put("isSuccess", true);
        	message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.恭喜您登录成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.恭喜您登录成功.value);
        	return message.getMap();
        }
        
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.登录页面已经过期.value));
		message.put("responseCode", EnumUtil.ResponseCode.登录页面已经过期.value);
		return message.getMap();
	}
	
	/**
	 * 取消扫码登陆
	 * @return
	 */
	@RequestMapping(value = "/scan/cancel", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> CancelScanLogin(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(userService.cancelScanLogin(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * web端用户搜索
	 * @return
	 */
	@RequestMapping(value = "/websearch", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> websearch(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.webSearch(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 管理员更新用户的基本信息
	 * @return
	 */
	@RequestMapping(value = "/ad/user", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> adminUpdateUserBase(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.adminUpdateUserBase(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 管理员重置用户登录密码
	 * @return
	 */
	@RequestMapping(value = "/ad/resetPwd", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> adminResetPassword(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.adminResetPassword(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除用户
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deleteUser(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.deleteUser(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 添加用户
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"}) 
	public Map<String, Object> addUser(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.addUser(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 在线用户列表
	 * @return
	 */
	@RequestMapping(value = "/actives", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"}) 
	public Map<String, Object> actives(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.actives(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	/*@RequestMapping("/aaa")
	public String getAllUser(HttpServletRequest request){
		try {
			if(!checkParams(request, response)){
				printWriter(message, response);
				return null;
			}
			
			
			
			printWriter(message, response);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response);
		return null;
	}*/
}
