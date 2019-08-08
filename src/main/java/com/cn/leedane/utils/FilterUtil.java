package com.cn.leedane.utils;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.shiro.CustomAuthenticationToken;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.OperateLogSaveThread;
import org.apache.log4j.Logger;

import com.cn.leedane.utils.sensitiveWord.SensitivewordFilter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 内容过滤工具类
 * @author LeeDane
 * 2016年7月12日 上午10:27:56
 * Version 1.0
 */
public class FilterUtil {
	private static Logger logger = Logger.getLogger(FilterUtil.class);

	/**
	 * 过滤
	 * @param content
	 * @param message
	 * @param request
	 * @return
	 */
	public static boolean filter(String content, Map<String, Object> message, HttpRequestInfoBean request){
		//检测敏感词
		SensitivewordFilter filter = new SensitivewordFilter();
		long beginTime = System.currentTimeMillis();
		Set<String> set = filter.getSensitiveWord(content, 1);
		if(set.size() > 0){
			//发送敏感信息日志
			sendOperateLog(request, content, set);
			message.put("message", "有敏感词"+set.size()+"个："+set.toString());
			message.put("responseCode", EnumUtil.ResponseCode.系统检测到有敏感词.value);
			long endTime = System.currentTimeMillis();
			logger.info("总共消耗时间为：" + (endTime - beginTime));
			return true;
		}
		
		//过滤掉emoji
		//content = EmojiUtil.filterEmoji(content);
		return false;
	}
	
	/**
	 * 过滤是否含有敏感词汇并且过滤emoji表情
	 * @param content
	 * @return
	 */
	public static boolean filter(String content){
		//检测敏感词
		SensitivewordFilter filter = new SensitivewordFilter();
		long beginTime = System.currentTimeMillis();
		Set<String> set = filter.getSensitiveWord(content, 1);
		if(set.size() > 0){
			long endTime = System.currentTimeMillis();
			logger.info("总共消耗时间为：" + (endTime - beginTime));
			return true;
		}
		
		//过滤掉emoji
		content = EmojiUtil.filterEmoji(content);
		return false;
	}
	
	/**
	 * 过滤是否含有敏感词汇
	 * @param content
	 */
	public static Set<String> getFilter(String content){
		//检测敏感词
		return new SensitivewordFilter().getSensitiveWord(content, 1);
	}

	/**
	 * 发送敏感信息日志
	 * @param request
	 * @param content
	 * @param sensitiveword
	 */
	public static void sendOperateLog(HttpRequestInfoBean request, String content, Set<String> sensitiveword){
		//获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		//对于登录用户，保存其操作记录
		if(currentUser != null){
			if(currentUser.isRemembered() || currentUser.isAuthenticated()){
				Object obj = currentUser.getPrincipal();
				if(obj != null){
					CustomAuthenticationToken token = (CustomAuthenticationToken)obj;
					UserBean user = token.getUser();
					//异步添加用户solr索引
					new ThreadUtil().singleTask(new OperateLogSaveThread(user, request, new Date(), "原始内容是: "+ content +", 内容中敏感信息是："+ sensitiveword.toString(), "--", ConstantsUtil.STATUS_NORMAL, 0));
				}
			}
		}
	}

	/**
	 * 发送敏感信息日志
	 * @param user
	 * @param content
	 * @param sensitiveword
	 */
	public static void sendOperateLogByUser(UserBean user, String content, Set<String> sensitiveword) {
		//异步添加用户solr索引
		new ThreadUtil().singleTask(new OperateLogSaveThread(user, null, new Date(), "原始内容是: " + content + ", 内容中敏感信息是：" + sensitiveword.toString(), "--", ConstantsUtil.STATUS_NORMAL, 0));
	}
}
