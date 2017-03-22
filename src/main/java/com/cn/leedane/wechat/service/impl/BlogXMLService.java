package com.cn.leedane.wechat.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.handler.WechatHandler;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.wechat.bean.WeixinCacheBean;
import com.cn.leedane.wechat.service.BaseXMLWechatService;
import com.cn.leedane.wechat.util.MessageUtil;
import com.cn.leedane.wechat.util.WeixinUtil;

/**
 * 最新模式下的实现
 * @author LeeDane
 * 2016年4月7日 下午4:15:14
 * Version 1.0
 */
public class BlogXMLService extends BaseXMLWechatService {

	@Autowired
	private BlogService<BlogBean> blogService;
	
	@Autowired
	private WechatHandler wechatHandler;
	
	public void setWechatHandler(WechatHandler wechatHandler) {
		this.wechatHandler = wechatHandler;
	}
	
	/**
	 * 最后一条博客的ID
	 */
	private int lastBlogId;
	/**
	 * 每次取得最新博客的数量
	 */
	private int num = WeixinUtil.DEFAULT_SEARCH_NUMBER; 
	
	public BlogXMLService(){
		
	}
	
	public BlogXMLService(HttpServletRequest request, Map<String, String> map) {
		super(request, map);
	}

	
	public void setBlogService(BlogService<BlogBean> blogService) {
		this.blogService = blogService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected String execute() {
		if(wechatHandler == null){
			wechatHandler = (WechatHandler) SpringUtil.getBean("wechatHandler");
		}
		
		if(blogService == null){
			blogService = (BlogService<BlogBean>) SpringUtil.getBean("blogService");
		}
		
		WeixinCacheBean cacheBean = wechatHandler.getFromUserInfo(FromUserName);
		if(cacheBean != null){
			lastBlogId = cacheBean.getLastBlogId();
		}else{
			cacheBean =new WeixinCacheBean();
		}
			
		List<Map<String, Object>> beans = blogService.getLatestBlogById(lastBlogId, num);
		int size = beans.size();
		cacheBean.setLastBlogId(size > 0 ? StringUtil.changeObjectToInt(beans.get(size-1).get("id")): 0);
		wechatHandler.addCache(FromUserName, cacheBean);
		return MessageUtil.initBlogNewsMessage( getBasePath() ,ToUserName,FromUserName,beans);
	}
	
}
