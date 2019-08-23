package com.cn.leedane.controller;

import com.cn.leedane.handler.FanHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.handler.RoleHandler;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.ElasticSearchRequestBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.service.MoodService;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private RoleHandler roleHandler;

	@Autowired
	private TransportClient transportClient;

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;

	/**
	 * Es执行搜索
	 */
	@RequestMapping(value="/es", method=RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> executeEs(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);

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
		int searchType = JsonUtil.getIntValue(jsonObject, "type", 0);
		int current = JsonUtil.getIntValue(jsonObject, "current", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "page_size", 10);
		int start = SqlUtil.getPageStart(current, rows, 0);
		String keyword = JsonUtil.getStringValue(jsonObject, "keyword"); //搜索关键字
		String startDate = JsonUtil.getStringValue(jsonObject, "startDate"); //开始时间
		String endDate = JsonUtil.getStringValue(jsonObject, "endDate"); //结束时间
		String sort = JsonUtil.getStringValue(jsonObject, "sort", "modify_time"); //排序的字段，默认是开始时间
		boolean sortDesc = JsonUtil.getBooleanValue(jsonObject, "desc", false); //是否从大到小排序，默认是从大到小
		int accurate = JsonUtil.getIntValue(jsonObject, "accurate", 0); //是否是精确查询，默认是模糊查询
		//String platform = JsonUtil.getStringValue(jsonObject, "platform", "web");//平台名称
		/*if(StringUtil.isNull(keyword)){
			message.put("message", "请检索关键字为空");
			return message.getMap();
		}*/
		List<Integer> tempIds = new ArrayList<Integer>();
		//获取全部
		/*if(type == 0){
			tempIds.add(ConstantsUtil.SEARCH_TYPE_BLOG);
			tempIds.add(ConstantsUtil.SEARCH_TYPE_MOOD);
			tempIds.add(ConstantsUtil.SEARCH_TYPE_USER);
			tempIds.add(ConstantsUtil.SEARCH_TYPE_OPERATE_LOG);
		}else{
			tempIds.add(type);
		}*/
		String table = null;
		String[] searchFields = new String[1];
		String[] showFields = null;
		//查询的类型，目前支持0、全部，1、博客（正文和标题），2、说说(正文)，3、用户(姓名，中文名，邮件，手机号码,证件号码)
		switch (searchType){
			case ConstantsUtil.SEARCH_TYPE_BLOG:
				table = DataTableType.博客.value;
				searchFields = new String[3];
				searchFields[0] = "title";
				searchFields[1] = "digest";
				searchFields[2] = "content";

				showFields = new String[7];
				showFields[0] = "create_time";
				showFields[1] = "create_user_id";
				showFields[2] = "digest";
				showFields[3] = "has_img";
				showFields[4] = "id";
				showFields[5] = "modify_time";
				showFields[6] = "title";

				break;
			case ConstantsUtil.SEARCH_TYPE_MOOD:
				table = DataTableType.心情.value;
				searchFields = new String[1];
				searchFields[0] = "content";

				showFields = new String[7];
				showFields[0] = "create_user_id";
				showFields[1] = "content";
				showFields[2] = "create_time";
				showFields[3] = "has_img";
				showFields[4] = "id";
				showFields[5] = "modify_time";
				showFields[6] = "imgs";

				break;
			case ConstantsUtil.SEARCH_TYPE_USER:
				table = DataTableType.用户.value;
				searchFields = new String[6];
				searchFields[0] = "account";
				searchFields[1] = "china_name";
				searchFields[2] = "email";
				searchFields[3] = "qq";
				searchFields[4] = "personal_introduction";
				searchFields[5] = "native_place";

				sort = "register_time";
				break;
			case ConstantsUtil.SEARCH_TYPE_OPERATE_LOG:
				table = DataTableType.操作日志.value;
				searchFields = new String[3];
				searchFields[0] = "browser";
				searchFields[1] = "ip";
				searchFields[2] = "subject";
				break;
			default:
				searchFields = new String[1];
		}

		ElasticSearchRequestBean requestBean = new ElasticSearchRequestBean();
		requestBean.setTable(table);
		requestBean.setSearchFields(searchFields);
		requestBean.setSearchKey(keyword);
		requestBean.setAccurate(accurate);
		requestBean.setSortField(sort);
		requestBean.setOrder(sortDesc ? SortOrder.DESC: SortOrder.ASC);
		requestBean.setStart(start);
		requestBean.setNumber(rows);
		requestBean.setUser(getUserFromShiro());
		requestBean.setStartDate(startDate);
		requestBean.setEndDate(endDate);
		requestBean.setSearchType(searchType);
		requestBean.setShowFields(showFields);
		SearchResponse response = elasticSearchUtil.getResults(requestBean);

		List<Map<String, Object>> results = new ArrayList<>();
		if(response != null && response.status() == RestStatus.OK){
			Map<String, Object> result = new HashMap<>();
			result.put("total", response.getHits().totalHits);
			List rs = new ArrayList<Map<String, Object>>();
			for (SearchHit hit : response.getHits()) {
				Map<String, Object> documentFieldMap = hit.getSourceAsMap();

				//设置图片头像图片
				if(documentFieldMap.get("create_user_id") != null){
					int createUserId = StringUtil.changeObjectToInt(documentFieldMap.get("create_user_id"));
					documentFieldMap.put("user_pic_path", userHandler.getUserPicPath(createUserId, "30x30"));
					if(searchType == ConstantsUtil.SEARCH_TYPE_USER){
						//List<UserBean> u = response2.getBeans(UserBean.class);
							/*if(platformApp && user != null && createUserId != user.getId()){
								map.put("isFan", fanHandler.inAttention(user.getId(), createUserId));
								map.put("isFriend", friendHandler.inFriend(user.getId(), createUserId));
							}*/



					}else if(searchType == ConstantsUtil.SEARCH_TYPE_MOOD){
						/*if(StringUtil.changeObjectToBoolean(documentFieldMap.get("has_img"))){
							String uuid = StringUtil.changeNotNull(documentFieldMap.get("uuid"));
							documentFieldMap.put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, ConstantsUtil.DEFAULT_PIC_SIZE));
						}*/
						documentFieldMap.put("has_img", StringUtil.isNotNull(StringUtil.changeNotNull(documentFieldMap.get("imgs"))));
						documentFieldMap.put("account", userHandler.getUserName(createUserId));
					}else if(searchType == ConstantsUtil.SEARCH_TYPE_BLOG){
						documentFieldMap.put("account", userHandler.getUserName(createUserId));
					}else if(searchType == ConstantsUtil.SEARCH_TYPE_OPERATE_LOG){
						documentFieldMap.put("account", userHandler.getUserName(createUserId));
					}
				}else{
					if(searchType == ConstantsUtil.SEARCH_TYPE_USER){
						boolean self = getUserFromShiro() == null ? false: getUserFromShiro().getId() == StringUtil.changeObjectToInt(documentFieldMap.get("id"));
						documentFieldMap = userHandler.getUserInfo(documentFieldMap, self);
						documentFieldMap.put("is_admin", isAdmin(StringUtil.changeObjectToInt(documentFieldMap.get("id"))));
					}
				}

				/*Iterator<Entry<String, Object>> iterator = documentFieldMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> next = iterator.next();
					String key = next.getKey();
					Object value = next.getValue();
					if(value instanceof Date){
						documentFieldMap.put(key, DateUtil.DateToString((Date)value));
					}

					//设置图片头像图片
					if(key.equalsIgnoreCase("create_user_id")){
						int createUserId = StringUtil.changeObjectToInt(value);
						documentFieldMap.put("user_pic_path", userHandler.getUserPicPath(createUserId, "30x30"));
						if(searchType == ConstantsUtil.SEARCH_TYPE_USER){
							//List<UserBean> u = response2.getBeans(UserBean.class);
							*//*if(platformApp && user != null && createUserId != user.getId()){
								map.put("isFan", fanHandler.inAttention(user.getId(), createUserId));
								map.put("isFriend", friendHandler.inFriend(user.getId(), createUserId));
							}*//*
						}else if(searchType == ConstantsUtil.SEARCH_TYPE_MOOD){
							if(StringUtil.changeObjectToBoolean(documentFieldMap.get("has_img"))){
								String uuid = StringUtil.changeNotNull(documentFieldMap.get("uuid"));
								documentFieldMap.put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, ConstantsUtil.DEFAULT_PIC_SIZE));
							}
							documentFieldMap.put("account", userHandler.getUserName(createUserId));
						}else if(searchType == ConstantsUtil.SEARCH_TYPE_BLOG){
							documentFieldMap.put("account", userHandler.getUserName(createUserId));
						}else if(searchType == ConstantsUtil.SEARCH_TYPE_OPERATE_LOG){
							documentFieldMap.put("account", userHandler.getUserName(createUserId));
						}
					}

				}*/
//				rs.add(hit.getSourceAsMap());

				rs.add(documentFieldMap);
			}
			result.put("hits", rs);
			message.put("message", results);
			message.put("isSuccess", true);
			results.add(result);
			//搜索得到的结果数
			logger.info("Find:" + response.getHits().totalHits);
		}else{
			message.put("message", "es检索响应失败");
		}
		return message.getMap();
	}

	/**
	 * 判断用户是否是管理员权限
	 * @param userId
	 * @return
	 */
	private boolean isAdmin(int userId){
		int roleId = LeedanePropertiesConfig.newInstance().getInt("admin.role.id");
		List<Map<String, Object>> users = roleHandler.getUsers(roleId);
		if(CollectionUtil.isNotEmpty(users)){
			for(Map<String, Object> user: users){
				if(StringUtil.changeObjectToInt(user.get("user_id")) == userId)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 搜索用户
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> user(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(userService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 搜索心情
	 * @return
	 */
	@RequestMapping(value = "/mood", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> mood(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(moodService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 搜索心情
	 * @return
	 */
	@RequestMapping(value = "/blog", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> blog(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(blogService.search(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}

}
