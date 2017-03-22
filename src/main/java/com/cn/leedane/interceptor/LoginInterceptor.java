package com.cn.leedane.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 登陆拦截器
 * @author LeeDane
 * 2016年7月12日 下午5:43:13
 * Version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor{
	Logger logger = Logger.getLogger(getClass());
	/**
	 * 必须过滤
	 */
	public static final boolean MUST_FILTER = false;
	/**
	 * 不对此次请求进行过滤
	 */
	public static final boolean NO_FILTER = true; 
	
	
	/**
	 * 系统的缓存对象
	 */
	@Autowired
	private SystemCache systemCache;
	
	public void setSystemCache(SystemCache systemCache) {
		this.systemCache = systemCache;
	}
	
	@Autowired
	private UserService<UserBean> userService;
	
	public void setUserService(UserService<UserBean> userService) {
		this.userService = userService;
	}

	@Autowired
	private UserHandler userHandler;
	
	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse request, Object arg2, Exception arg3)
			throws Exception {
		//logger.info("LoginInterceptor:afterCompletion");
		System.out.println("LoginInterceptor:afterCompletion");
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2, ModelAndView arg3) throws Exception {
		//logger.info("LoginInterceptor:postHandle");
		System.out.println("LoginInterceptor:postHandle");
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		logger.info("LoginInterceptor:preHandle");
		request.setCharacterEncoding("utf-8");
		//false将对请求进行拦截，没有下一步的操作，true会将请求提交给controller进行下一步的处理

		Map<String, Object> message = new HashMap<String, Object>();
		
		//判断是否是系统维护时间
		String maintenanceOrigin = systemCache.getCache("maintenance-period") == null ? null : (String)systemCache.getCache("maintenance-period");
		if(!StringUtil.isNull(maintenanceOrigin)){
			String maintenance = maintenanceOrigin.replaceAll("AM", "").replaceAll("PM", "");
			if(StringUtil.isNotNull(maintenance)){
				String[] dates = maintenance.split("-");
				//获取当前的小时
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				int hour = c.get(Calendar.HOUR_OF_DAY);
				
				boolean isMaintain = false; //是否维护
				if(dates.length ==2){
					int begin = StringUtil.changeObjectToInt((dates[0]));
					int end = StringUtil.changeObjectToInt((dates[1]));
					
					if(begin <= hour && hour <= end){//在系统维护的时间内
						isMaintain = true;
					}
				}else{
					if(StringUtil.changeObjectToInt(maintenance) == hour){//在系统维护的时间内
						isMaintain = true;
					}
				}
				if(isMaintain){
					message.put("isSuccess", false);
					message.put("message", "抱歉，每天"+maintenanceOrigin+"是系统维护时间");
					message.put("isAccount", true);
					printWriter(message, response);
					return LoginInterceptor.MUST_FILTER;
				}
			}
		}
		
		//先判断session中是否有该用户的信息
		UserBean user = (UserBean) request.getSession().getAttribute(ConstantsUtil.USER_SESSION);
		//由于流只能读取一次，而且之前每个action中的params就是接收的是json的参数，
		//所以这里借助acttion中的params参数，将已经读取的流转化成json后赋值给params
		
		//System.out.println("stack.hashCode():"+stack.hashCode());
		
		//session中缓存的用户
		if(user != null){
			return NO_FILTER;
		}else{
			//该链接是过滤掉的链接
			String actionPath = request.getRequestURI();
			System.out.println("请求的地址：" + actionPath);
			@SuppressWarnings("unchecked")
			List<String> filterUrls = systemCache.getCache("filterUrls") == null ? null : (List<String>)systemCache.getCache("filterUrls");
			
			if(filterUrls != null && filterUrls.size() > 0){
				for(String url: filterUrls){//遍历过滤url文件
					if(actionPath.contains(url)){  //找到需要过滤的路径
						return NO_FILTER;
					}
				}
			}
			return NO_FILTER;
		}
	}
	/**
	 * 通过原先servlet方式输出json对象。
	 * 目的：解决复杂的文本中含有特殊的字符导致struts2的json
	 * 		解析失败，给客户端返回500的bug
	 */
	private void printWriter(Map<String, Object> message, HttpServletResponse response){
		JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
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
}
