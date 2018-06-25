package com.cn.leedane.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;

import com.cn.leedane.exception.MustAdminLoginException;
import com.cn.leedane.exception.MustLoginException;
import com.cn.leedane.exception.TestRoleException;
import com.cn.leedane.handler.LinkManageHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.LinkManageBean;
import com.cn.leedane.model.LinkManagesBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.shiro.CustomAuthenticationToken;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.PlatformType;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.cn.leedane.utils.HttpUtil;
import com.cn.leedane.utils.StringUtil;

public class BaseController {
	private Logger logger = Logger.getLogger(getClass());

	@Resource
	protected UserService<UserBean> userService;
	
	@Resource
	protected UserHandler userHandler;
	
	@Resource
	private LinkManageHandler linkManageHandler;
	

	@Autowired
	protected OperateLogService<OperateLogBean> operateLogService;
	

    @Autowired
    private SessionDAO sessionDAO;
	
	/**
	 * 通过原先servlet方式输出json对象。
	 * 目的：解决复杂的文本中含有特殊的字符导致struts2的json
	 * 		解析失败，给客户端返回500的bug
	 */
	//请使用printWriter(Map<String, Object> message, HttpServletResponse response, long start)
	protected void printWriter(Map<String, Object> message, HttpServletResponse response){
		if(message.containsKey("json"))
			message.remove("json");
		
		if(message.containsKey("user"))
			message.remove("user");
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		logger.info("服务器返回:"+jsonObject.toString());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.append(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}
		
	}
	
	/**
	 * 通过原先servlet方式输出json对象。
	 * 目的：解决复杂的文本中含有特殊的字符导致struts2的json
	 * 		解析失败，给客户端返回500的bug
	 */
	protected void printWriter(Map<String, Object> message, HttpServletResponse response, long start){
		/*if(message.containsKey("json"))
			message.remove("json");
		
		if(message.containsKey("user"))
			message.remove("user");
		
		if(start > 0){
			long end = System.currentTimeMillis();
			message.put("consumeTime", (end - start) +"ms");
		}
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		logger.info("服务器返回:"+jsonObject.toString());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.append(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}*/
		
	}

	
	/**
	 * 校验请求参数
	 * @param request
	 * @return
	 */
	protected boolean checkParams(Map<String, Object> message, HttpServletRequest request){
		return checkLogin(request, message);
	}
	
	/**
	 * 校验是否有是测试用户在执行新增、修改、删除等操作
	 * @param roles
	 * @return
	 * @throws UnauthorizedException 不成功将抛出“不授权异常”
	 */
	protected boolean checkTestRole(Model model, String method) throws UnauthorizedException{//不授权异常
		boolean isTestRole = false;
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        isTestRole = currentUser.hasRole(ConstantsUtil.TEST_ROLE_TEXT);
		
		//测试账号无法执行添加、删除和修改等权限
		if("GET".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method)){
			return true;
		}
		
		if(isTestRole)
			throw new TestRoleException("您当前是测试角色，无法执行："+ method +"请求操作！");
		
       return isTestRole;
	}
	
	/**
	 * 校验是否有单个角色
	 * @param permission
	 * @return
	 * @throws UnauthorizedException
	 */
	protected boolean checkRoleAuthor(String role) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        boolean rs = currentUser.hasRole(role);
        if(rs)
        	return rs;
		
        throw new UnauthorizedException();
	}
	
	/**
	 * 校验是否有角色(全部角色都要满足)
	 * @param request
	 * @return
	 */
	protected boolean checkAllRoleAuthor(String ... roles) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        boolean rs = currentUser.hasAllRoles(Arrays.asList(roles));
        if(rs)
        	return rs;
		
        throw new UnauthorizedException();
	}
	
	/**
	 * 校验是否有角色(只要一个满足就行,这种方式需要检验所有的role)
	 * @param roles
	 * @return
	 * @throws UnauthorizedException 不成功将抛出“不授权异常”
	 */
	protected boolean checkAnyRoleAuthor(String ... roles) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        boolean[] rs = currentUser.hasRoles(Arrays.asList(roles));
        for(boolean r: rs)
	        if(r)
	        	return r;
		
        throw new UnauthorizedException();
	}
	
	/**
	 * 校验是否有角色(只要一个满足就行,这种方式按照顺序遍历role，验证成功将直接返回)
	 * @param traverse
	 * @param roles
	 * @return
	 * @throws UnauthorizedException 不成功将抛出“不授权异常”
	 */
	protected boolean checkAnyRoleAuthor(boolean traverse, String ... roles) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        for(String role: roles){
        	if(currentUser.hasRole(role)){
        		return true;
        	}
        }
        
        throw new UnauthorizedException();
	}
	
	/**
	 * 校验是否有单个权限
	 * @param permission
	 * @return
	 * @throws UnauthorizedException
	 */
	protected boolean checkPermissionAuthor(String permission) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        boolean rs = currentUser.isPermitted(permission);
        if(rs)
        	return rs;
		
        throw new UnauthorizedException();
	}
	
	/**
	 * 校验是否有权限(全部权限都要满足)
	 * @param request
	 * @return
	 */
	protected boolean checkAllPermissionAuthor(String ... permissions) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        boolean rs = currentUser.isPermittedAll(permissions);
        if(rs)
        	return rs;
		
        throw new UnauthorizedException();
	}
	
	/**
	 * 校验是否有权限(只要一个满足就行,这种方式需要检验所有的permission)
	 * @param permissions
	 * @return
	 * @throws UnauthorizedException 不成功将抛出“不授权异常”
	 */
	protected boolean checkAnyPermissionAuthor(String ... permissions) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        boolean[] rs = currentUser.isPermitted(permissions);
        for(boolean r: rs)
	        if(!r)
	        	throw new UnauthorizedException();
        return true;
	}
	
	/**
	 * 校验是否有权限(只要一个满足就行,这种方式按照顺序遍历permission，验证成功将直接返回)
	 * @param traverse
	 * @param roles
	 * @return
	 * @throws UnauthorizedException 不成功将抛出“不授权异常”
	 */
	@Deprecated
	protected boolean checkAnyPermissionAuthor(boolean traverse, String ... permissions) throws UnauthorizedException{//不授权异常
		//获取当前的Subject  
        /*Subject currentUser = SecurityUtils.getSubject();
        for(String role: permissions){
        	if(currentUser.hasRole(role)){
        		return true;
        	}
        }*/
        
        throw new UnauthorizedException();
	}
	
	public boolean checkLogin(HttpServletRequest request, Map<String, Object> message){
		boolean result = false;
		
		//获取session
		Object sessionUserInfo = null;
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
        	sessionUserInfo = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	/* Collection<Session> sessions = sessionDAO.getActiveSessions();
        	 for(Session session:sessions){

        		 if("leedane".equals(String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)))) {
        			 session.setTimeout(0);//设置session立即失效，即将其踢出系统
        			 sessionDAO.delete(session);
        			 break;
        		 }

        	 }*/
        }
		
		UserBean user = null;
		//标记用户已经登录
		if(sessionUserInfo != null){
			result = true;
			user = (UserBean)sessionUserInfo;
			message.put("user", user);
			message.put("message", result);
			//HttpSession session = SessionManagerUtil.getInstance().getSession(user.getId());
			//if(session == null){
				//user = null;
			//}
		}
		
		//请求参数
		String params = request.getParameter("params");
		JSONObject json = new JSONObject();
		if(StringUtil.isNull(params)){
			try {
				json = convertParameterMapToJsonObject(request.getParameterMap());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			//校验用户信息
			json = JSONObject.fromObject(params);
		}
		
		if(json == null || json.isEmpty()){
			try {
				json = HttpUtil.getJsonObjectFromInputStream(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(json != null)
			logger.info("请求参数:"+json.toString());
		//if(json != null && !json.isEmpty()){
			//从请求ID获取用户信息
			/*if(user == null){
				if(json.has("id")){
					//设置为了防止过滤路径，直接在这里加载用户请求有id为默认登录用户
					try {
						user = userService.findById(JsonUtil.getIntValue(json, "id"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}*/
			
			//对于有token的再进行shiro校验
			if(user == null){			
				//user = userService.findById(1);
				user = appAuthCheck(request, message, result);
			}
			
			if(user != null){
				String returnErrorMeg = "";
				int returnErrorCode = 0;
				//获取登录用户的状态
				int status = user.getStatus();
				boolean canDo = false;
				
				//0:被禁止 1：正常，2、注册未激活  ，3：未完善信息 ， 4：被禁言 ，5:注销
				switch (status) {
					case ConstantsUtil.STATUS_DISABLE:
						returnErrorMeg = "账号"+user.getAccount()+"已经被禁用，有问题请联系管理员";
						returnErrorCode = EnumUtil.ResponseCode.账号已被禁用.value;
						break;
					case ConstantsUtil.STATUS_NORMAL:
						canDo = true;
						break;
					case ConstantsUtil.STATUS_NO_ACTIVATION:
						returnErrorMeg = "请先激活账号"+ user.getAccount();
						returnErrorCode = EnumUtil.ResponseCode.账号未被激活.value;
						break;
					case ConstantsUtil.STATUS_INFORMATION:
						returnErrorMeg = "请先完善账号"+ user.getAccount() +"的信息";
						returnErrorCode = EnumUtil.ResponseCode.请先完善账号信息.value;
						break;
					case ConstantsUtil.STATUS_NO_TALK:
						returnErrorMeg = "账号"+ user.getAccount()+"已经被禁言，有问题请联系管理员";
						returnErrorCode = EnumUtil.ResponseCode.账号已被禁言.value;
						break;
					case ConstantsUtil.STATUS_DELETE:
						returnErrorMeg = "账号"+ user.getAccount()+"已经被注销，有问题请联系管理员";
						returnErrorCode = EnumUtil.ResponseCode.账号已被注销.value;
						break;
					default:
						break;
				}
				
				userHandler.addLastRequestTime(user.getId());
				
				//当验证账号的状态是正常的情况，继续执行action
				if(canDo){
					message.put("user", user);
					result = true;
				}else{
					message.put("message", returnErrorMeg);
					message.put("responseCode", returnErrorCode);
				}
			}else{
				if(!result && !message.containsKey("message")){
					message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value));
					message.put("responseCode", EnumUtil.ResponseCode.请先登录.value);
				}
			}
		//}
		message.put("json", json == null ? new JSONObject(): json);
		return result;
	}
	
	/**
	 * 统一App登录验证检查
	 * @param request
	 * @param message
	 * @param user
	 * @return
	 */
	public UserBean appAuthCheck(HttpServletRequest request, Map<String, Object> message, boolean result){
		//拿到token码
		String token = request.getHeader("token");
		//int useridq = StringUtil.changeObjectToInt(request.getHeader("userid"));
		UserBean user = null;
		//校验token
		if(StringUtil.isNotNull(token)){
			//拿token到shiro进行登录校验
			//JSONObject js = JSONObject.fromObject(new String(Base64Util.decode(token.toCharArray())));
	        int userid = StringUtil.changeObjectToInt(request.getHeader("userid"));
			
	        //从token获取到密码
			//拿到username用户名称
	        CustomAuthenticationToken usernamePasswordToken = new CustomAuthenticationToken();
	        usernamePasswordToken.setUserId(userid);;
	        usernamePasswordToken.setPassword(token.toCharArray());
	        usernamePasswordToken.setToken(token);
	        usernamePasswordToken.setRememberMe(true);
	        usernamePasswordToken.setPlatformType(PlatformType.安卓版);
	        //这里只负责获取用户，不做校验，校验交给shiro的realm里面去做
	        user = userHandler.getUserBean(userid);
	        usernamePasswordToken.setUser(user);
	        
	        //获取当前的Subject  
	        Subject currentUser = SecurityUtils.getSubject();
	        logger.info("对用户[" + userid + "]进行登录验证..验证开始");  
            currentUser.login(usernamePasswordToken);  
            logger.info("对用户[" + userid + "]进行登录验证..验证通过"); 
	        /*try {  
	            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查  
	            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应  
	            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法  
	            logger.info("对用户[" + userid + "]进行登录验证..验证开始");  
	            currentUser.login(usernamePasswordToken);  
	            logger.info("对用户[" + userid + "]进行登录验证..验证通过");  
	        }catch(UnknownAccountException uae){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,未知账户");  
	            message.put("message", "未知账户");  
	        }catch(IncorrectCredentialsException ice){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,错误的凭证");  
	            message.put("message", "密码不正确");  
	        }catch(LockedAccountException lae){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,账户已锁定");  
	            message.put("message", "账户已锁定");  
	        }catch(ExcessiveAttemptsException eae){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,错误次数过多");  
	            message.put("message", "用户名或密码错误次数过多");  
	        }catch(BannedAccountException ba){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,用户已经被禁言了");  
	            message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已被禁言.value));
	        }catch(CancelAccountException ca){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,用户已经注销了");  
	            message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已经注销.value));
	        }catch(StopUseAccountException sua){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,用户暂时被禁止使用");
	            message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已被禁止使用.value));
	        }catch(NoValidationEmailAccountException nveca){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,用户未验证邮箱");
	            message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先验证邮箱.value));
	        }catch(NoActiveAccountException naa){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,用户未激活");
	            message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.注册未激活账户.value));
	        }catch(NoCompleteAccountException naa){  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,用户未完善信息");
	            message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.未完善信息.value));
	        }catch(AuthenticationException ae){  
	            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景  
	            logger.info("对用户[" + userid + "]进行登录验证..验证未通过,堆栈轨迹如下");  
	            ae.printStackTrace();  
	            message.put("message", "用户名或密码不正确");  
	        }  */
	        //验证是否登录成功  
	        if(currentUser.isAuthenticated()){  
	            logger.info("用户[" + userid + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)"); 
	            message.put("message", "恭喜您登录成功"); 
	            result = true;
	            message.put("isSuccess", result); 
	            return user;
	        }else{  
	        	usernamePasswordToken.clear();  
	        } 
		}
		
		return null;
	}
	
	/**
	 * 从message中解析json数据
	 * @param message
	 * @return
	 */
	protected JSONObject getJsonFromMessage(Map<String, Object> message){
		JSONObject json = null;
		if(!CollectionUtils.isEmpty(message) && message.containsKey("json")){
			json = (JSONObject) message.get("json");
		}
		return json;
	}
	
	/**
	 * 从message中解析user数据
	 * @param message
	 * @return
	 */
	protected UserBean getUserFromMessage(Map<String, Object> message){
		UserBean user = null;
		if(!CollectionUtils.isEmpty(message) && message.containsKey("user")){
			user = (UserBean) message.get("user");
		}
		return user;
	}
	
	/**
	 * 从shiro中解析user数据(用户可能没登录，返回null),不建议使用
	 * 请参考使用{@link BaseController#getMustLoginUserFromShiro}
	 * @param message
	 * @return
	 */
	protected UserBean getUserFromShiro(){
		UserBean user = null;
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
        	Object o = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	user = (UserBean)o;
        }
		return user;
	}
	
	/**
	 * 从shiro中解析user数据(用户必须登录，不然抛 {@link MustLoginException} 异常)
	 * @param message
	 * @return
	 */
	protected UserBean getMustLoginUserFromShiro(){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
        	return (UserBean)currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        }
        throw new MustLoginException();
	}
	
	/**
	 * 从shiro中解析管理员登录user数据(用户必须登录，不然抛 {@link MustLoginException} 异常)
	 * @param message
	 * @return
	 */
	protected UserBean getMustAdminLoginUserFromShiro(){
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
      //后台只有管理员权限才能操作
        if(currentUser.isAuthenticated() && currentUser.hasRole(RoleController.ADMIN_ROLE_CODE)){
        	return (UserBean)currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        }
        throw new MustAdminLoginException();
	}
	
	/**
	 * 从message中解析user数据
	 * @param message
	 * @return
	 */
	protected int getUserIdFromShiro(){
		UserBean user = getMustLoginUserFromShiro();
		if(user == null)
			throw new MustLoginException();
		return user.getId();
	}
	
	/**
	 * 将请求参数转化成json对象
	 * @param map
	 * @return
	 */
	private JSONObject convertParameterMapToJsonObject(Map<String, String[]> map){
		JSONObject json = new JSONObject();
		for(Entry<String, String[]> entry: map.entrySet()){
			//说明是数组
			if(entry.getValue().length > 1)
				json.put(entry.getKey(), entry.getValue());
			else 
				json.put(entry.getKey(), entry.getValue()[0]);
		}
		return json;
	}

	/**
	 * 必须进行检查角色和权限
	 * 方法只有调用该方法才做权限控制
	 * @param model
	 * @param request
	 */
	protected void checkRoleOrPermission(Model model, HttpServletRequest request){
		checkTestRole(model, request.getMethod());
		LinkManagesBean beans = linkManageHandler.getAllLinks();
		if(beans != null && CollectionUtil.isNotEmpty(beans.getLinkManageBean())){
			String uri = request.getRequestURI();
			for(LinkManageBean bean: beans.getLinkManageBean()){
				if(bean.getLink().equals(uri) || uri.matches(bean.getLink()) || ifStartWithLink(uri, bean.getLink())){
					String roleOrPermissionCodes = bean.getRoleOrPermissionCodes();
					if(bean.isRole()){
						if(StringUtil.isNotNull(roleOrPermissionCodes)){
							String[] codes = roleOrPermissionCodes.split(",");
							if(bean.isAll_())
								checkAllRoleAuthor(codes);
							else
								checkAnyRoleAuthor(codes);
						}
					}else{
						if(StringUtil.isNotNull(roleOrPermissionCodes)){
							String[] codes = roleOrPermissionCodes.split(",");
							if(bean.isAll_())
								checkAllPermissionAuthor(codes);
							else
								checkAnyPermissionAuthor(codes);
						}
					}
				}
			}
		}
	}

	/**
	 * 必须进行检查角色和权限
	 * 方法只有调用该方法才做权限控制
	 * @param uri
	 */
	protected void checkRoleOrPermission(String uri){
		LinkManagesBean beans = linkManageHandler.getAllLinks();
		if(beans != null && CollectionUtil.isNotEmpty(beans.getLinkManageBean())){
			for(LinkManageBean bean: beans.getLinkManageBean()){
				if(bean.getLink().equals(uri) || uri.matches(bean.getLink()) || ifStartWithLink(uri, bean.getLink())){
					String roleOrPermissionCodes = bean.getRoleOrPermissionCodes();
					if(bean.isRole()){
						if(StringUtil.isNotNull(roleOrPermissionCodes)){
							String[] codes = roleOrPermissionCodes.split(",");
							if(bean.isAll_())
								checkAllRoleAuthor(codes);
							else
								checkAnyRoleAuthor(codes);
						}
					}else{
						if(StringUtil.isNotNull(roleOrPermissionCodes)){
							String[] codes = roleOrPermissionCodes.split(",");
							if(bean.isAll_())
								checkAllPermissionAuthor(codes);
							else
								checkAnyPermissionAuthor(codes);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * 校验链接是否是模糊匹配的
	 * @param requestLink
	 * @param manageLink 必须以*结尾
	 * @return
	 */
	private boolean ifStartWithLink(String requestLink, String manageLink){
		if(!manageLink.endsWith("*"))
			return false;
		manageLink = manageLink.substring(0, manageLink.length() - 1);
		
		return requestLink.startsWith(manageLink);
	}
	
	/**
	 * 校验地址，不校验是否登录
	 * @param urlParse
	 * @param model
	 * @param httpSession
	 * @return
	 */
	protected String loginRoleCheck(String urlParse, Model model, HttpServletRequest request){
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
	protected String loginRoleCheck(String urlParse, boolean mustLogin, Model model, HttpServletRequest request){
		//设置统一的请求模式
		model.addAttribute("isDebug", ConstantsUtil.IS_DEBUG);
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = getUserFromShiro();
        //设置是否是登录用户
        model.addAttribute("isTestRole", currentUser.hasRole(ConstantsUtil.TEST_ROLE_TEXT));
		boolean isLogin = false;
		boolean isAdmin = false;
		boolean isStock = false; //判断用户是否有股票的权限
		if(user != null){
			isLogin = true;
			isAdmin = currentUser.hasRole(RoleController.ADMIN_ROLE_CODE);
			isStock = currentUser.hasRole(RoleController.STOCK_ROLE_CODE);
			model.addAllAttributes(userHandler.getBaseUserInfo(user.getId()));
			model.addAttribute("loginUserId", user.getId());
			model.addAttribute("user", user);
		}
		model.addAttribute("isLogin",  isLogin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("isStock", isStock);
		if(mustLogin && !isLogin){
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请先登录.value));
			return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();
		}
		
		return StringUtil.isNotNull(urlParse) ? urlParse : "404";
	}
	
	/**
	 * 校验地址，校验是否登录
	 * @param urlParse
	 * @param mustAdmin 为true表示必须是管理员身份登录，不然就跳转到登录页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String adminLoginRoleCheck(String urlParse, Model model, HttpServletRequest request){
		//设置统一的请求模式
		model.addAttribute("isDebug", ConstantsUtil.IS_DEBUG);
		UserBean userBean = getMustAdminLoginUserFromShiro();
		model.addAttribute("isLogin", true);
		model.addAttribute("account", userBean.getAccount());
		return StringUtil.isNotNull(urlParse) ? urlParse : "404";
	}
}
