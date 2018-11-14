package com.cn.leedane.springboot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.cn.leedane.exception.CompleteOrderDeleteException;
import com.cn.leedane.exception.IllegalOperationException;
import com.cn.leedane.exception.MobCodeErrorException;
import com.cn.leedane.exception.MustAdminLoginException;
import com.cn.leedane.exception.MustLoginException;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.exception.TestRoleException;
import com.cn.leedane.exception.user.BannedAccountException;
import com.cn.leedane.exception.user.CancelAccountException;
import com.cn.leedane.exception.user.NoActiveAccountException;
import com.cn.leedane.exception.user.NoCompleteAccountException;
import com.cn.leedane.exception.user.NoValidationEmailAccountException;
import com.cn.leedane.exception.user.StopUseAccountException;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.cn.leedane.utils.StringUtil;

/**
 * sprngmvc全局异常的处理类
 * @author LeeDane
 * 2017年3月30日 上午10:14:17
 * version 1.0
 */
@Component
public class ExceptionHandler implements HandlerExceptionResolver {
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception) {
		
		
		boolean isPageRequest = CommonUtil.isPageRequest(request, response);
		
		Map<String, Object> message = new HashMap<String, Object>();
        message.put("isSuccess", false);
        
		if(exception instanceof org.apache.shiro.authz.UnauthorizedException){//没有授权异常
			logger.error(EnumUtil.getResponseValue(ResponseCode.没有操作权限.value)); 
			if(isPageRequest){
				return new ModelAndView("forward:/403");
			}
			 message.put("message", EnumUtil.getResponseValue(ResponseCode.没有操作权限.value));
			 message.put("responseCode", ResponseCode.没有操作权限.value);
		}else if(exception instanceof UnsupportedTokenException){//不支持token异常
			logger.error(EnumUtil.getResponseValue(ResponseCode.token过期或无效.value)); 
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.token过期或无效.value));
			message.put("responseCode", ResponseCode.token过期或无效.value);
			
		}else if(exception instanceof RE404Exception){//404异常
			logger.error(EnumUtil.getResponseValue(ResponseCode.资源不存在.value)); 
			if(isPageRequest)
				return new ModelAndView("forward:/404");
			
			message.put("message", exception.getMessage());
			message.put("responseCode", ResponseCode.资源不存在.value);
		}else if(exception instanceof UnknownAccountException){//未知账户
			logger.error("对用户进行登录验证..验证未通过,未知账户");  
			if(isPageRequest)
				return new ModelAndView("redirect:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.用户名或者密码不匹配.value));
			message.put("responseCode", ResponseCode.用户名或者密码不匹配.value);
			
		}else if(exception instanceof BannedAccountException){//用户已被禁言
			logger.error("对用户进行登录验证..验证未通过,用户已经被禁言了");
			if(isPageRequest)
				return new ModelAndView("redirect:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.用户已被禁言.value));
			message.put("responseCode", ResponseCode.用户已被禁言.value);
			
		}else if(exception instanceof CancelAccountException){//用户已经注销
			logger.error("对用户进行登录验证..验证未通过,用户已经注销了");
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.用户已经注销.value));
			message.put("responseCode", ResponseCode.用户已经注销.value);
			
		}else if(exception instanceof StopUseAccountException){//用户已被禁止使用
			logger.error("对用户进行登录验证..验证未通过,用户暂时被禁止使用"); 
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.用户已被禁止使用.value));
			message.put("responseCode", ResponseCode.用户已被禁止使用.value);
			
		}else if(exception instanceof NoValidationEmailAccountException){//用户未验证邮箱
			logger.error("对用户进行登录验证..验证未通过,用户未验证邮箱"); 
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.请先验证邮箱.value));
			message.put("responseCode", ResponseCode.请先验证邮箱.value);
			
		}else if(exception instanceof NoActiveAccountException){//注册未激活账户
			logger.error("对用户进行登录验证..验证未通过,用户未激活");  
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.注册未激活账户.value));
			message.put("responseCode", ResponseCode.注册未激活账户.value);
			
		}else if(exception instanceof NoCompleteAccountException){//未完善信息
			logger.error("对用户进行登录验证..验证未通过,用户未完善信息");
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.未完善信息.value));
			message.put("responseCode", ResponseCode.未完善信息.value);
			
		}else if(exception instanceof IncorrectCredentialsException){//密码不正确
			logger.error("对用户进行登录验证..验证未通过,错误的凭证");
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.密码不正确.value));
			message.put("responseCode", ResponseCode.密码不正确.value);
			
		}else if(exception instanceof LockedAccountException){//账户已锁定
			logger.error("对用户进行登录验证..验证未通过,账户已锁定");
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.账户已锁定.value));
			message.put("responseCode", ResponseCode.账户已锁定.value);
			
		}else if(exception instanceof ExcessiveAttemptsException){//用户名或密码错误次数过多
			logger.error("对用户进行登录验证..验证未通过,错误次数过多");
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.用户名或密码错误次数过多.value));
			message.put("responseCode", ResponseCode.用户名或密码错误次数过多.value);
			
		}else if(exception instanceof AuthenticationException){//账号或密码不匹配
			logger.error("对用户进行登录验证..验证未通过,堆栈轨迹如下");
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.账号或密码不匹配.value));
			message.put("responseCode", ResponseCode.账号或密码不匹配.value);
			
		}else if(exception instanceof NullPointerException){//空指针异常
			exception.printStackTrace();
			logger.error("系统报错，空指针异常！！！");
			//mo
			if(isPageRequest)
				try {
					return new ModelAndView("forward:/null-pointer?errorMessage="+ URLEncoder.encode(exception.getMessage(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			
			if(StringUtil.isNull(exception.getMessage()))
				message.put("message", "空指针异常");
			else
				message.put("message", exception.getMessage());
			message.put("detail", exception.getMessage());
			message.put("responseCode", ResponseCode.空指针异常.value);
			
		}else if(exception instanceof DuplicateKeyException){
			logger.error("mysql表唯一约束报错！！！");
			message.put("message", "违反唯一性约束，请确定是否已经有相同数据存在！");
			message.put("detail", exception.getMessage());
			message.put("responseCode", ResponseCode.空指针异常.value);
		}else if(exception instanceof DataIntegrityViolationException){
			logger.error("mysql保存异常，某些字段长度太长！");
			if(isPageRequest)
				return new ModelAndView("forward:/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.某些字段超过其存储所需的长度.value));
			message.put("responseCode", ResponseCode.某些字段超过其存储所需的长度.value);
		}else if(exception instanceof ParameterUnspecificationException){
			logger.error("参数不规范的异常");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.参数为空或者不符合规范异常.value) +"：" + exception.getMessage());
			message.put("responseCode", ResponseCode.参数为空或者不符合规范异常.value);
		}else if(exception instanceof CompleteOrderDeleteException){
			logger.error("已经完成的订单被删除的异常");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.完成状态的订单无法删除的异常.value));
			message.put("responseCode", ResponseCode.完成状态的订单无法删除的异常.value);
		}else if(exception instanceof TestRoleException){
			logger.error("已经完成的订单被删除的异常");
			message.put("message", exception.getMessage());
			message.put("responseCode", ResponseCode.测试角色权限受限异常.value);
		}else if(exception instanceof MustLoginException){
			logger.error("必须登录才能使用该功能的异常");
			if(isPageRequest)
				return new ModelAndView("redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString());
			message.put("message", exception.getMessage());
			message.put("responseCode", ResponseCode.请先登录.value);
		}else if(exception instanceof MustAdminLoginException){
			logger.error("必须是管理员账号登录才能使用的异常");
			if(isPageRequest){
				//使用权限管理工具进行用户的退出，跳出登录，给出提示信息
		        SecurityUtils.getSubject().logout();
				ModelAndView model = new ModelAndView("redirect:/lg?errorcode=" +EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value +"&t="+ UUID.randomUUID().toString() +"&ref="+ CommonUtil.getFullPath(request));
				//model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请使用有管理员权限的账号登录.value));
				return model;
			}
			message.put("message", exception.getMessage());
			message.put("responseCode", ResponseCode.请使用有管理员权限的账号登录.value);
		}else if(exception instanceof MobCodeErrorException){
			message.put("message", EnumUtil.getResponseValue(ResponseCode.验证码验证失败.value));
			message.put("responseCode", ResponseCode.验证码验证失败.value);
		}else if(exception instanceof IllegalOperationException){
			message.put("message", exception.getMessage());
			message.put("responseCode", ResponseCode.非法操作异常.value);
		}else{
			StringPrintWriter strintPrintWriter = new StringPrintWriter();  
	        exception.printStackTrace(strintPrintWriter);
	        logger.error(strintPrintWriter.getString());
	        message.put("message", /*"服务器异常"*/strintPrintWriter.getString());//将错误信息传递给view  
		}
		
		ModelAndView mav = new ModelAndView();
		
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setAttributesMap(message);
		mav.setView(view);
		return mav;
        /*JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		//logger.info("服务器返回:"+jsonObject.toString());
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
		return null;*/
	}  
	
	
}

class StringPrintWriter extends PrintWriter{  
	  
    public StringPrintWriter(){  
        super(new StringWriter());  
    }  
     
    public StringPrintWriter(int initialSize) {  
          super(new StringWriter(initialSize));  
    }  
     
    public String getString() {  
          flush();  
          return ((StringWriter) this.out).toString();  
    }  
     
    @Override  
    public String toString() {  
        return getString();  
    }  
}  
