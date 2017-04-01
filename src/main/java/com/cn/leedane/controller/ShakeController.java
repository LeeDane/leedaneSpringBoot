package com.cn.leedane.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.lucene.solr.UserSolrHandler;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.service.MoodService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 摇一摇相关的controller处理类
 * @author LeeDane
 * 2016年12月21日 上午11:40:20
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.sh)
public class ShakeController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private MoodService<MoodBean> moodService;
	
	@Autowired
	private BlogService<BlogBean> blogService;
	
	/**
	 * 摇一摇搜索用户
	 * @return
	 */
	@RequestMapping(value="/user", method=RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> user(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(userService.shakeSearch(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 摇一摇搜索心情
	 * @return
	 */
	@RequestMapping(value = "/mood", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> mood(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(moodService.shakeSearch(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 摇一摇搜索心情
	 * @return
	 */
	@RequestMapping(value = "/blog", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> blog(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(blogService.shakeSearch(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	
	/**
	 * 获取指定类型搜索的域（type不能为0，为0需要特殊处理）
	 * 查询的类型，目前支持0、全部，1、博客（正文和标题），2、说说(正文)，3、用户(姓名，中文名，邮件，手机号码,证件号码)
	 * @param tempId
	 * @return
	 */
	private String[] getSearchFields(int tempId){
		List<String> array = new ArrayList<String>();
		switch (tempId) {
		case 1:
			array.add("btitle");
			array.add("bcontent");
			break;
		case 2:
			array.add("mcontent");
			break;
		case 3:
			array.add("uchina_name");
			array.add("uaccount");
			array.add("ureal_name");
			array.add("umobile_phone");
			array.add("uid_card");
			array.add("uemail");
			break;
		}
		return (String[]) array.toArray();
	}
	
	/**
	 * 获取指定类型搜索的数量
	 * 查询的类型，目前支持0、全部，1、博客（正文和标题），2、说说(正文)，3、用户(姓名，中文名，邮件，手机号码,证件号码)
	 * @param type
	 * @return
	 */
	private int getSearchRows(int tempId){
		int rows = 0;
		switch (tempId) {
		case 1:
			rows = ConstantsUtil.DEFAULT_BLOG_SEARCH_ROWS;
			break;
		case 2:
			rows = ConstantsUtil.DEFAULT_MOOD_SEARCH_ROWS;
			break;
		case 3:
			rows = ConstantsUtil.DEFAULT_USER_SEARCH_ROWS;
			break;
		}
		return rows;
	}
	
	class SingleSearchTask implements Callable<QueryResponse>{
		private int tempId;
		private String keyword;
		private int start;
		public SingleSearchTask(int tempId, String keyword, int start) {
			this.tempId = tempId;
			this.keyword = keyword;
			this.start = start;
		}

		@Override
		public QueryResponse call() throws Exception {
			SolrQuery query = new SolrQuery();
		    query.setQuery(keyword);
		    query.setFields(getSearchFields(tempId));
		    //query.setSort("price", ORDER.asc);
		    query.setStart(start);
		    query.setRows(getSearchRows(tempId));
			return UserSolrHandler.getInstance().query(query);
		}
	}
}
