package com.cn.leedane.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
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
import com.cn.leedane.lucene.solr.ProductSolrHandler;
import com.cn.leedane.lucene.solr.ShopSolrHandler;
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
import com.cn.leedane.utils.RelativeDateFormat;
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

	private Logger logger = Logger.getLogger(getClass());
	
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
		/**
		 * 查询的类型，目前支持
		 * 0:全部，
		 * 1:博客（正文和标题），
		 * 2:说说(正文)，
		 * 3:用户(姓名，中文名，邮件，手机号码，证件号码)
		 * 4:商店：商店详情， 名称
		 * 5:商品：商品原详情，标题，
		 */
		int type = JsonUtil.getIntValue(jsonObject, "type", 0);
		int start = JsonUtil.getIntValue(jsonObject, "start", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "rows", 0);
		String keyword = JsonUtil.getStringValue(jsonObject, "keyword"); //搜索关键字
		String sort = JsonUtil.getStringValue(jsonObject, "sort", "createTime"); //排序的字段，默认是开始时间
		boolean sortDesc = JsonUtil.getBooleanValue(jsonObject, "desc", true); //是否从大到小排序，默认是从大到小
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
			tempIds.add(ConstantsUtil.SEARCH_TYPE_SHOP);
			tempIds.add(ConstantsUtil.SEARCH_TYPE_PRODUCT);
		}else{
			tempIds.add(type);
		}
		//启动多线程
		List<Future<Map<String, Object>>> futures = new ArrayList<Future<Map<String, Object>>>();
		SingleSearchTask searchTask;
		//派发5个线程执行
		ExecutorService threadpool = Executors.newFixedThreadPool(5);
		for(int tempId: tempIds){
			searchTask = new SingleSearchTask(tempId, keyword, start, rows, sort, sortDesc);
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
		Map<String, Long> totalsMap = new HashMap<String, Long>();
		boolean platformApp = JsonUtil.getBooleanValue(jsonObject, "platformApp"); //搜索关键字
		for(Map<String, Object> response1: responses){
			//获取单个搜索类型的数据
			int tempId = StringUtil.changeObjectToInt(response1.get("tempId"));
			QueryResponse response2 = (QueryResponse) response1.get("queryResponse");
			SolrDocumentList documentList= response2.getResults();			
			Map<String,Map<String,List<String>>> highlightings = response2.getHighlighting();
			List<Map<String, Object>> ds = new ArrayList<Map<String,Object>>();
			 
			
			//这里是每条数据的结果
	        for (SolrDocument solrDocument : documentList){
	        	
	        	
	        	//获取ID域（必须，索引要要求所有的实体都有id字段）
	        	String idField = StringUtil.changeNotNull(solrDocument.getFieldValue("id"));
	            
	            Map<String, Object> map = new HashMap<String, Object>();
	            //设置高亮的字段
	            Map<String,List<String>> highlightField = highlightings.get(idField);
	            for(Entry<String, Object> m: solrDocument.entrySet()){
	            	/*if(highlightField != null && highlightField.containsKey(m.getKey())){
	            		Set<String> hls = StringUtil.getSearchHighlight(highlightField.get(m.getKey()).get(0));
	            		for(String hl: hls){
	            			String val = ((String)m.getValue());
	            			val = val.replaceAll(hl, "<font color=red >" + hl +"</font>");
	            			map.put(m.getKey(), val);
	            		}
	            	}else{
	            		map.put(m.getKey(), m.getValue());
	            	}*/
	            	map.put(m.getKey(), m.getValue());
	            }
	            
	            
	            
	            map.remove("_version_");
	        	
	        	if(solrDocument.containsKey("createTime")){
	        		map.put("createTime", DateUtil.formatLocaleTime(StringUtil.changeNotNull(solrDocument.getFieldValue("createTime")), DateUtil.DEFAULT_DATE_FORMAT));
	            }
	            
	            if(solrDocument.containsKey("registerTime")){
	            	map.put("registerTime", DateUtil.formatLocaleTime(StringUtil.changeNotNull(solrDocument.getFieldValue("registerTime")), DateUtil.DEFAULT_DATE_FORMAT));
	            }

	            //设置图片头像图片
	            int createUserId = StringUtil.changeObjectToInt(solrDocument.getFieldValue("createUserId"));
            	map.put("user_pic_path", userHandler.getUserPicPath(createUserId, "30x30"));
	            if(tempId == ConstantsUtil.SEARCH_TYPE_USER){
	            	//List<UserBean> u = response2.getBeans(UserBean.class);
	            	if(platformApp && user != null && createUserId != user.getId()){
	            		map.put("isFan", fanHandler.inAttention(user.getId(), createUserId));
    					map.put("isFriend", friendHandler.inFriend(user.getId(), createUserId));
	            	}
	            }else if(tempId == ConstantsUtil.SEARCH_TYPE_MOOD){
	            	if(StringUtil.changeObjectToBoolean(solrDocument.getFieldValue("hasImg"))){
	            		String uuid = StringUtil.changeNotNull(solrDocument.getFieldValue("uuid"));
	            		map.put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, ConstantsUtil.DEFAULT_PIC_SIZE));
	            	}
	            	map.put("account", userHandler.getUserName(createUserId));
	            }else if(tempId == ConstantsUtil.SEARCH_TYPE_BLOG){
	            	map.put("account", userHandler.getUserName(createUserId));
	            }
	        	 //对web平台处理高亮
				/*if("web".equalsIgnoreCase(platform) && !response2.getHighlighting().isEmpty()){
					Map<String, Map<String, List<String>>>  highlightings = response2.getHighlighting();
					Map<String, List<String>> msMap = highlightings.get(String.valueOf(tempId));
				}*/
	            /*logger.info("id = " +solrDocument.getFieldValue("id"));
	            logger.info("account = " +solrDocument.getFieldValue("account"));
	            logger.info("personalIntroduction = " +solrDocument.getFieldValue("personalIntroduction"));*/
	            
	            ds.add(map);
	        }
	        docsMap.put(String.valueOf(tempId), ds);
	        totalsMap.put(String.valueOf(tempId), documentList.getNumFound());
			//搜索得到的结果数
	        logger.info("Find:" + documentList.getNumFound());
		}
		//logger.info("data="+com.alibaba.fastjson.JSONArray.toJSONString(rs));
		
		//UserBean userBean = userService.findById(1);
		//UserSolrHandler.getInstance().addBean(userBean);
		//List<UserBean> userBeans = userService.getAllUsers(1);
		//UserSolrHandler.getInstance().addBeans(userBeans);
		
		//List<UserBean> userBeans4 = userService.getAllUsers(4);
		//UserSolrHandler.getInstance().addBeans(userBeans4);
		
		//List<BlogBean> blogs = blogService.getBlogBeans("select * from "+ DataTableType.博客.value +" where status=?", ConstantsUtil.STATUS_NORMAL);
		//BlogSolrHandler.getInstance().addBeans(blogs);
		
		//List<MoodBean> moods = moodService.getMoodBeans("select * from "+ DataTableType.心情.value +" where status=?", ConstantsUtil.STATUS_NORMAL);
		//MoodSolrHandler.getInstance().addBeans(moods);
		message.put("message", docsMap);
		message.put("total", totalsMap);
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
		
		checkRoleOrPermission(request);
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
		
		checkRoleOrPermission(request);
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
		
		checkRoleOrPermission(request);
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
		case ConstantsUtil.SEARCH_TYPE_SHOP:
			array = new String[2];
			array[0] = "detail";
			array[1] = "name";
			break;
		case ConstantsUtil.SEARCH_TYPE_PRODUCT:
			array = new String[2];
			array[0] = "detailSource";
			array[1] = "title";
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
		case ConstantsUtil.SEARCH_TYPE_SHOP:
			rows = ConstantsUtil.DEFAULT_SHOP_SEARCH_ROWS;
			break;
		case ConstantsUtil.SEARCH_TYPE_PRODUCT:
			rows = ConstantsUtil.DEFAULT_PRODUCT_SEARCH_ROWS;
			break;
		}
		
		return rows;
	}
	
	class SingleSearchTask implements Callable<Map<String, Object>>{
		private int tempId;
		private String keyword;
		private int start;
		private int rows;
		private String sort; //排序的字段
		private ORDER order; //是否从大到小排序
		public SingleSearchTask(int tempId, String keyword, int start, int rows, String sort, boolean sortDesc) {
			this.tempId = tempId;
			this.keyword = keyword;
			this.rows = rows;
			this.start = start;
			this.sort = sort;
			this.order =  sortDesc ? ORDER.desc: ORDER.asc;
		}

		@Override
		public Map<String, Object> call() throws Exception {
			SolrQuery query = new SolrQuery();
		    
		    //query.setFields(getSearchFields(tempId));
		    //query.setSort("price", ORDER.asc);
		    query.setStart(start);
		    query.setRows(rows > 0 ? rows : getSearchRows(tempId));
		    //query.setRows(getSearchRows(tempId));
		    //开启高亮功能
		    query.setHighlight(true);
		    // 设置高亮字段
		    setHighlightFields(query, tempId);
		    
		    // 以下两个方法主要是在高亮的关键字前后加上html代码 
		    query.setHighlightSimplePre("<"); 
		    query.setHighlightSimplePost("/>");
		    //query.setHighlight(true);
		    //query.setHighlightRequireFieldMatch(true);
		    query.set("wt", "xml");
		    query.set("hl", "true");
		    query.set("indent", "true");
		    
		    //开启高亮整个文本
		    query.set("hl.preserveMulti", "true");
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
		    	//sqlBuffer.append(" AND createUserId:1 ");
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort("createTime", ORDER.desc);
		    	map.put("queryResponse", BlogSolrHandler.getInstance().query(query));
		    }else if(tempId == ConstantsUtil.SEARCH_TYPE_MOOD){
		    	//sqlBuffer.append(" AND createUserId:1 ");
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort("createTime", ORDER.desc);
		    	map.put("queryResponse", MoodSolrHandler.getInstance().query(query));
		    }else if(tempId == ConstantsUtil.SEARCH_TYPE_USER){
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort("registerTime", ORDER.desc);
		    	QueryResponse q = UserSolrHandler.getInstance().query(query);
		    	map.put("queryResponse", q);
		    }else if(tempId == ConstantsUtil.SEARCH_TYPE_SHOP){
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort(sort, order);
		    	map.put("queryResponse", ShopSolrHandler.getInstance().query(query));
		    }else if(tempId == ConstantsUtil.SEARCH_TYPE_PRODUCT){
		    	query.setQuery(sqlBuffer.toString()); //模糊查询
		    	query.setSort(sort, ORDER.desc);
		    	map.put("queryResponse", ProductSolrHandler.getInstance().query(query));
		    }
		    return map;
		}

		/**
		 * 设置高亮的字段
		 * @param query
		 */
		private void setHighlightFields(SolrQuery query, int temId) {
			String[] searchFields = getSearchFields(temId);
			if(searchFields != null && searchFields.length > 0 ){
				for(String searchField: searchFields){
					query.addHighlightField(searchField);
				}
			}
				
		}
	}
}
