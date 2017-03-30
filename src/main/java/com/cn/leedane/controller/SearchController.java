package com.cn.leedane.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.handler.FanHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.lucene.solr.BlogSolrHandler;
import com.cn.leedane.lucene.solr.MoodSolrHandler;
import com.cn.leedane.lucene.solr.UserSolrHandler;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.service.MoodService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * 搜索相关的controller处理类
 * @author LeeDane
 * 2016年12月21日 上午11:40:40
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.s)
public class SearchController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private MoodService<MoodBean> moodService;
	
	@Autowired
	private BlogService<BlogBean> blogService;
	
	@Autowired
	private MoodHandler moodHandler;
	
	@Autowired
	private FanHandler fanHandler;
	
	@Autowired
	private FriendHandler friendHandler;
	
	/**
	 * 执行搜索
	 */
	@RequestMapping(value="/s", method=RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> execute(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		UserBean user = getUserFromMessage(message);
		JSONObject jsonObject = getJsonFromMessage(message);
		//查询的类型，目前支持0、全部，1、博客（正文和标题），2、说说(正文)，3、用户(姓名，中文名，邮件，手机号码，证件号码)
		int type = JsonUtil.getIntValue(jsonObject, "type", 0);
		String keyword = JsonUtil.getStringValue(jsonObject, "keyword"); //搜索关键字
		//String platform = JsonUtil.getStringValue(jsonObject, "platform", "web");//平台名称
		if(StringUtil.isNull(keyword)){
			message.put("message", "请检索关键字为空");
			return message.getMap();
		}
		List<Map<String, Object>> responses = new ArrayList<Map<String, Object>>();
		List<Integer> tempIds = new ArrayList<Integer>();
		//获取全部
		if(type == 0){
			tempIds.add(ConstantsUtil.SEARCH_TYPE_BLOG);
			tempIds.add(ConstantsUtil.SEARCH_TYPE_MOOD);
			tempIds.add(ConstantsUtil.SEARCH_TYPE_USER);
		}else{
			tempIds.add(type);
		}
		//启动多线程
		List<Future<Map<String, Object>>> futures = new ArrayList<Future<Map<String, Object>>>();
		SingleSearchTask searchTask;
		//派发5个线程执行
		ExecutorService threadpool = Executors.newFixedThreadPool(5);
		for(int tempId: tempIds){
			searchTask = new SingleSearchTask(tempId, keyword, 0);
			futures.add(threadpool.submit(searchTask));
		}
		threadpool.shutdown();
		
		for(int i = 0; i < futures.size() ;i++){
			try {
				//保存每次请求执行后的结果
				if(futures.get(i).get() != null)
						responses.add(futures.get(i).get());
			} catch (InterruptedException e) {
				e.printStackTrace();
				futures.get(i).cancel(true);
			} catch (ExecutionException e) {
				e.printStackTrace();
				futures.get(i).cancel(true);
			}
		}
		
		Map<String, List<Map<String, Object>>> docsMap = new HashMap<String, List<Map<String,Object>>>();
		boolean platformApp = JsonUtil.getBooleanValue(jsonObject, "platformApp"); //搜索关键字
		for(Map<String, Object> response1: responses){
			int tempId = StringUtil.changeObjectToInt(response1.get("tempId"));
			QueryResponse response2 = (QueryResponse) response1.get("queryResponse");
			SolrDocumentList documentList= response2.getResults();
			List<Map<String, Object>> ds = new ArrayList<Map<String,Object>>();
			 
	        for (SolrDocument solrDocument : documentList){
	        	solrDocument.removeFields("_version_");
	        	
	        	if(solrDocument.containsKey("createTime")){
	        		solrDocument.setField("createTime", DateUtil.formatLocaleTime(StringUtil.changeNotNull(solrDocument.getFieldValue("createTime")), DateUtil.DEFAULT_DATE_FORMAT));
	            }
	            
	            if(solrDocument.containsKey("registerTime")){
	            	solrDocument.setField("registerTime", DateUtil.formatLocaleTime(StringUtil.changeNotNull(solrDocument.getFieldValue("registerTime")), DateUtil.DEFAULT_DATE_FORMAT));
	            }
	            Map<String, Object> map = new HashMap<String, Object>();
	            for(Entry<String, Object> m: solrDocument.entrySet()){
	            	map.put(m.getKey(), m.getValue());
	            }
	            
	            if(tempId == ConstantsUtil.SEARCH_TYPE_USER){
	            	int userId = StringUtil.changeObjectToInt(solrDocument.getFieldValue("id"));
	            	map.putAll(userHandler.getBaseUserInfo(userId));
	            	if(platformApp && user != null && userId != user.getId()){
	            		map.put("isFan", fanHandler.inAttention(user.getId(), userId));
    					map.put("isFriend", friendHandler.inFriend(user.getId(), userId));
	            	}
	            }else if(tempId == ConstantsUtil.SEARCH_TYPE_MOOD){
	            	map.putAll(userHandler.getBaseUserInfo(StringUtil.changeObjectToInt(solrDocument.getFieldValue("createUserId"))));
	            	if(StringUtil.changeObjectToBoolean(solrDocument.getFieldValue("hasImg"))){
	            		String uuid = StringUtil.changeNotNull(solrDocument.getFieldValue("uuid"));
	            		map.put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, ConstantsUtil.DEFAULT_PIC_SIZE));
	            	}
	            }else if(tempId == ConstantsUtil.SEARCH_TYPE_BLOG){
	            	map.put("account", userHandler.getUserName(StringUtil.changeObjectToInt(solrDocument.getFieldValue("createUserId"))));
	            }
	        	 //对web平台处理高亮
				/*if("web".equalsIgnoreCase(platform) && !response2.getHighlighting().isEmpty()){
					Map<String, Map<String, List<String>>>  highlightings = response2.getHighlighting();
					Map<String, List<String>> msMap = highlightings.get(String.valueOf(tempId));
				}*/
	            /*System.out.println("id = " +solrDocument.getFieldValue("id"));
	            System.out.println("account = " +solrDocument.getFieldValue("account"));
	            System.out.println("personalIntroduction = " +solrDocument.getFieldValue("personalIntroduction"));*/
	            
	            ds.add(map);
	        }
	        docsMap.put(String.valueOf(tempId), ds);
			//搜索得到的结果数
		    System.out.println("Find:" + documentList.getNumFound());
		}
		//System.out.println("data="+com.alibaba.fastjson.JSONArray.toJSONString(rs));
		
		//UserBean userBean = userService.findById(1);
		//UserSolrHandler.getInstance().addBean(userBean);
		//List<UserBean> userBeans = userService.getAllUsers(1);
		//UserSolrHandler.getInstance().addBeans(userBeans);
		
		//List<UserBean> userBeans4 = userService.getAllUsers(4);
		//UserSolrHandler.getInstance().addBeans(userBeans4);
		
		//List<BlogBean> blogs = blogService.getBlogBeans("select * from t_blog where status=?", ConstantsUtil.STATUS_NORMAL);
		//BlogSolrHandler.getInstance().addBeans(blogs);
		
		//List<MoodBean> moods = moodService.getMoodBeans("select * from t_mood where status=?", ConstantsUtil.STATUS_NORMAL);
		//MoodSolrHandler.getInstance().addBeans(moods);
		message.put("message", docsMap);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	/**
	 * 搜索用户
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> user(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(userService.search(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 搜索心情
	 * @return
	 */
	@RequestMapping(value = "/mood", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> mood(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(moodService.search(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 搜索心情
	 * @return
	 */
	@RequestMapping(value = "/blog", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> blog(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(blogService.search(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	
	/**
	 * 获取指定类型搜索的域（type不能为0，为0需要特殊处理）
	 * 查询的类型，目前支持0、全部，1、博客（正文和标题），2、说说(正文)，3、用户(姓名，中文名，邮件，手机号码,证件号码)
	 * @param tempId
	 * @return
	 */
	private String[] getSearchFields(int tempId){
		String[] array = null;
		switch (tempId) {
		case ConstantsUtil.SEARCH_TYPE_BLOG:
			array = new String[2];
			array[0] = "content";
			array[1] = "title";
			break;
		case ConstantsUtil.SEARCH_TYPE_MOOD:
			array = new String[1];
			array[0] = "content";
			break;
		case ConstantsUtil.SEARCH_TYPE_USER:
			array = new String[6];
			array[0] = "account";
			array[1] = "chinaName";
			array[2] = "email";
			array[3] = "qq";
			array[4] = "personalIntroduction";
			array[5] = "nativePlace";
			break;
		}
		return array;
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
		case ConstantsUtil.SEARCH_TYPE_BLOG:
			rows = ConstantsUtil.DEFAULT_BLOG_SEARCH_ROWS;
			break;
		case ConstantsUtil.SEARCH_TYPE_MOOD:
			rows = ConstantsUtil.DEFAULT_MOOD_SEARCH_ROWS;
			break;
		case ConstantsUtil.SEARCH_TYPE_USER:
			rows = ConstantsUtil.DEFAULT_USER_SEARCH_ROWS;
			break;
		}
		return rows;
	}
	
	class SingleSearchTask implements Callable<Map<String, Object>>{
		private int tempId;
		private String keyword;
		private int start;
		public SingleSearchTask(int tempId, String keyword, int start) {
			this.tempId = tempId;
			this.keyword = keyword;
			this.start = start;
		}

		@Override
		public Map<String, Object> call() throws Exception {
			SolrQuery query = new SolrQuery();
		    
		    //query.setFields(getSearchFields(tempId));
		    //query.setSort("price", ORDER.asc);
		    query.setStart(start);
		    query.setRows(getSearchRows(tempId));
		    
		    // 以下给两个字段开启了高亮
		    //query.addHighlightField("account"); 
		    //query.addHighlightField("personalIntroduction"); 
		    // 以下两个方法主要是在高亮的关键字前后加上html代码 
		    //query.setHighlightSimplePre("<font color='red'>"); 
		    //query.setHighlightSimplePost("</front>");
		    query.set("wt", "xml");
		    query.set("indent", "true");
		    Map<String, Object> map = new HashMap<String, Object>();
		    map.put("tempId", tempId);
		    StringBuffer sqlBuffer = new StringBuffer();
		    String[] searchFields = getSearchFields(tempId);
		    
		    for(int i = 0; searchFields.length > 0 && i <searchFields.length; i++){
		    	if(i == 0){
		    		sqlBuffer.append("(");
		    	}
		    	sqlBuffer.append(searchFields[i]);
		    	sqlBuffer.append(":");
		    	sqlBuffer.append(keyword);
		    	
		    	if(i == searchFields.length -1){
		    		sqlBuffer.append(")");
		    	}else{
		    		sqlBuffer.append(" OR ");
		    	}
		    }
		    //设置只获取状态是正常的数据
		    sqlBuffer.append(" AND status:");
	    	sqlBuffer.append(ConstantsUtil.STATUS_NORMAL +"");
		    if(tempId == ConstantsUtil.SEARCH_TYPE_BLOG){
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort("createTime", ORDER.desc);
		    	map.put("queryResponse", BlogSolrHandler.getInstance().query(query));
		    }else if(tempId == ConstantsUtil.SEARCH_TYPE_MOOD){
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort("createTime", ORDER.desc);
		    	map.put("queryResponse", MoodSolrHandler.getInstance().query(query));
		    }else if(tempId == ConstantsUtil.SEARCH_TYPE_USER){
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort("registerTime", ORDER.desc);
		    	map.put("queryResponse", UserSolrHandler.getInstance().query(query));
		    }
		    return map;
		}
	}
}
