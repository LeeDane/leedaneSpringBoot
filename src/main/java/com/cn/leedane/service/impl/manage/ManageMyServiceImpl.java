package com.cn.leedane.service.impl.manage;

import com.cn.leedane.exception.OperateException;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.*;
import com.cn.leedane.model.*;
import com.cn.leedane.notice.model.Email;
import com.cn.leedane.notice.send.INoticeFactory;
import com.cn.leedane.notice.send.NoticeFactory;
import com.cn.leedane.notice.send.SmsNotice;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.ManageMyService;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import java.util.*;

/**
 * 我的管理信息的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("manageMyService")
public class ManageMyServiceImpl implements ManageMyService<IDBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private OperateLogMapper operateLogMapper;

	@Autowired
	private SmsNotice smsNotice;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private Oauth2Mapper oauth2Mapper;

	@Autowired
	private MyTagsMapper myTagsMapper;

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;

	@Autowired
	private TransportClient transportClient;

	@Autowired
	private AttentionMapper attentionMapper;

	@Autowired
	private CollectionMapper collectionMapper;

	@Autowired
	private CommonHandler commonHandler;

	@Autowired
	private LogoutMapper logoutMapper;

	@Autowired
	private OptionHandler optionHandler;

	@Override
	public ResponseModel bindEmail(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("ManageMyServiceImpl-->bindEmail():jo="+jo);
		String email = JsonUtil.getStringValue(jo, "email");
		String password = JsonUtil.getStringValue(jo, "pwd");

		ParameterUnspecificationUtil.checkNullString(email, "email must not null.");
		ParameterUnspecificationUtil.checkNullString(password, "password must not null.");
		String oldEmail = user.getEmail();
		if(StringUtil.isNotNull(oldEmail) && oldEmail.equalsIgnoreCase(email))
			return new ResponseModel().error().message("与原先的邮箱一致，不需要重新绑定。");

		try {
			byte[] decodedData = RSACoder.decryptByPrivateKey(password, RSAKeyUtil.getInstance().getPrivateKey());
			password = new String(decodedData, "UTF-8");
			if(!user.getPassword().equalsIgnoreCase(MD5Util.compute(password)))
				return new ResponseModel().error().message("登录密码验证失败，请重试");

		} catch (BadPaddingException e1) {
			e1.printStackTrace();
			return new ResponseModel().error().message("该页面过期, 请刷新当前页面，重新操作！").code(EnumUtil.ResponseCode.RSA加密解密异常.value);
		}

		Date date = new Date();
		long start = date.getTime(); //开始时间

		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.MINUTE, 60); //60分钟过期
		long end = calendar2.getTimeInMillis(); //过期时间
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("end", end);
		params.put("email", email);
		params.put("uid", user.getId());
		String cipherText = CommonUtil.sm4Encrypt(params);

		INoticeFactory factory = new NoticeFactory();
		Email emailBean = new Email();
		String content = "您需要跟LeeDane系统绑定该邮箱，如非本人操作，请勿理会或者联系<a href='"+ LeedanePropertiesConfig.getSystemUrl() +"/cc/35'>管理员</a>处理。</br>请点击链接<a href='"+ LeedanePropertiesConfig.getSystemUrl() +"/my/manage/my/email/bind/click?param="+ cipherText +"&start="+ start +"&end="+ end +"'>点击</a>以完成绑定工作， 该链接1个小时有效。";
		emailBean.setContent(content);
		//emailBean.setFrom(user);
		emailBean.setSubject("绑定电子邮箱通知");
		UserBean toUser = new UserBean();
		toUser.setEmail(email);
		Set<UserBean> replyTo = new HashSet<>();
		replyTo.add(toUser);
		emailBean.setReplyTo(replyTo);
		factory.create(EnumUtil.NoticeType.邮件).send(emailBean);

		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"想绑定新的电子邮箱，地址是：", email).toString(), "bindEmail()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
//		return message.getMap();
		return new ResponseModel().ok().message("已经发送，请注意查收！");
	}

	@Override
	public ResponseModel bindPhone(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("ManageMyServiceImpl-->bindPhone():jo="+jo);
		String phone = JsonUtil.getStringValue(jo, "phone");
		String code = JsonUtil.getStringValue(jo, "code");
		String password = JsonUtil.getStringValue(jo, "pwd");
		String type = JsonUtil.getStringValue(jo, "type");
		ParameterUnspecificationUtil.checkPhone(phone, "phone is null or not a phone.");
		ParameterUnspecificationUtil.checkNullString(code, "code is null or not a phone.");
		ParameterUnspecificationUtil.checkNullString(password, "password must not null.");
		ParameterUnspecificationUtil.checkNullString(type, "sms type must not null.");

		String oldPhone = user.getMobilePhone();
		if(StringUtil.isNotNull(oldPhone) && oldPhone.equalsIgnoreCase(phone))
			return new ResponseModel().error().message("与原先的手机号码一致，不需要重新绑定。");

		try {
			byte[] decodedData = RSACoder.decryptByPrivateKey(password, RSAKeyUtil.getInstance().getPrivateKey());
			password = new String(decodedData, "UTF-8");
			if(!user.getPassword().equalsIgnoreCase(MD5Util.compute(password)))
				return new ResponseModel().error().message("登录密码验证失败，请重试。");
		} catch (BadPaddingException e1) {
			e1.printStackTrace();
			return new ResponseModel().error().message("该页面过期, 请刷新当前页面，重新操作！").code(EnumUtil.ResponseCode.RSA加密解密异常.value);
		}

		if(!smsNotice.check(phone, code, type))
			return new ResponseModel().error().message("短信验证码校验失败，请检查是否正确或者过期。");

		//这里必须要重新获取一份UserBean对象，不然直接修改参数user会导致shiro里面的user有值，Java是值传递
		UserBean updateUser = userMapper.findById(UserBean.class, user.getId());
		updateUser.setMobilePhone(phone);
		boolean success = false;
		try{
			success = userMapper.update(updateUser) > 0;
		}catch (DuplicateKeyException e) {
			return new ResponseModel().error().error().message("绑定失败，手机号码已经被绑定！");
		}
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"想绑定新手机，号码是：", phone, ",结果是："+ StringUtil.getSuccessOrNoStr(success)).toString(), "bindPhone()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success){
			//添加ES缓存
			new ThreadUtil().singleTask(new EsIndexAddThread<UserBean>(user));
			//把Redis缓存的信息删除掉
			userHandler.deleteUserDetail(user.getId());
			user = updateUser; //由于系统没有退出，需要重新赋值
			return new ResponseModel().ok().message("绑定成功！");
		}
		return new ResponseModel().message("绑定失败，请稍后重试。");
	}

	@Override
	public ResponseModel thirdUnBind(long oid, JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->thirdUnBind():oid="+ oid +"&jo="+jo);
		ParameterUnspecificationUtil.checkLong(oid, "oid must not null.");

		Oauth2Bean oauth2Bean = oauth2Mapper.findById(Oauth2Bean.class, oid);
		ParameterUnspecificationUtil.checkObject(oauth2Bean, "oauth object must not null.");

		if(oauth2Bean.getCreateUserId() != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));

		String platform = oauth2Bean.getPlatform();
		boolean success = oauth2Mapper.delete(oauth2Bean) > 0;
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"解除绑定第三方平台：", platform , ",结果是："+ StringUtil.getSuccessOrNoStr(success)).toString(), "thirdUnBind()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("解除绑定成功！");

		return new ResponseModel().error().message("解除绑定失败，请稍后重试。");
	}

	@Override
	public ResponseModel saveTags(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->saveTags():jo="+jo);
		String tags = JsonUtil.getStringValue(jo, "tags");
		ParameterUnspecificationUtil.checkNullString(tags, "tags must not null.");

		List<MyTagsBean> tagsBeans = new ArrayList<>();
		String[] tagArrsy = tags.split(",");
		if(tagArrsy.length > 10)
			throw new OperateException("目前一个人最多只能添加10个标签。");
		for(String tag: tagArrsy){
			MyTagsBean myTagsBean = new MyTagsBean();
			myTagsBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			myTagsBean.setCreateUserId(user.getId());
			myTagsBean.setCreateTime(new Date());
			myTagsBean.setModifyUserId(user.getId());
			myTagsBean.setModifyTime(new Date());
			myTagsBean.setTagName(tag);
			tagsBeans.add(myTagsBean);
		}
		myTagsMapper.deleteTags(user.getId());
		boolean success = myTagsMapper.batchSave(tagsBeans) == tagsBeans.size();
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"保存新的标签组合：", tags , ",结果是："+ StringUtil.getSuccessOrNoStr(success)).toString(), "saveTags()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success){
			return new ResponseModel().ok().message("添加成功！");
		}
		return new ResponseModel().error().message("添加失败，请稍后重试。");
	}

	@Override
	public LayuiTableResponseModel loginHistorys(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->loginHistorys():jo="+ jsonObject);
		int current = JsonUtil.getIntValue(jsonObject, "page", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
		int start = SqlUtil.getPageStart(current -1, rows, 0);

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("operate_type", 0));
		boolQuery.must(QueryBuilders.termQuery("create_user_id", user.getId()));
		boolQuery.must(QueryBuilders.matchQuery("method", "账号登录"));
		boolQuery.must(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_NORMAL));

		SearchRequestBuilder builder = transportClient.prepareSearch(ElasticSearchUtil.getDefaultIndexName(EnumUtil.DataTableType.操作日志.value)).setTypes(EnumUtil.DataTableType.操作日志.value)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(boolQuery)
				.setFrom(start);
		//高亮显示规则
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.preTags("<span style='color:red'>");
		highlightBuilder.postTags("</span>");
		builder.highlighter(highlightBuilder);
		builder.addSort("create_time", SortOrder.DESC);

		builder.setSize(rows);
		logger.error(builder.toString());
		SearchResponse response = builder.get();

		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		if(response != null && response.status() == RestStatus.OK){
			Map<String, Object> result = new HashMap<>();
			result.put("total", response.getHits().totalHits);
			List rs = new ArrayList<Map<String, Object>>();
			for (SearchHit hit : response.getHits()) {
				Map<String, Object> documentFieldMap = hit.getSourceAsMap();
				rs.add(documentFieldMap);
			}
			//搜索得到的结果数
			logger.info("Find:" + response.getHits().totalHits);
			responseModel.setData(rs).setCount(response.getHits().totalHits).code(0);
		}else{
			responseModel.code(0);
			responseModel.setMsg("es检索响应失败");
		}

		return responseModel;
	}

	@Override
	public ResponseModel deleteLoginHistory(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->deleteLoginHistory():jo="+ jsonObject);
		long id = JsonUtil.getLongValue(jsonObject, "id", 0);
		ParameterUnspecificationUtil.checkLong(id , "id 必须大于0");

		OperateLogBean operateLogBean = operateLogMapper.findById(OperateLogBean.class, id);
		if(operateLogBean == null){
			elasticSearchUtil.delete(EnumUtil.DataTableType.操作日志.value, id);
			throw new NullPointerException("日志不存在");
		}

		//检查是否是自己的记录
		if(operateLogBean.getCreateUserId() != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		boolean result = this.operateLogMapper.deleteById(OperateLogBean.class, id) > 0;
		if(result){
			elasticSearchUtil.delete(EnumUtil.DataTableType.操作日志.value, id);
			return new ResponseModel().ok().message("删除成功！");
		}
		return new ResponseModel().error().message("删除失败！");
	}

	@Override
	public LayuiTableResponseModel attentions(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->attentions():jo="+ jsonObject);
		int current = JsonUtil.getIntValue(jsonObject, "page", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
		int start = SqlUtil.getPageStart(current -1, rows, 0);
		List<Map<String, Object>> rs = attentionMapper.getMyAttentions(user.getId(), start, rows);
		if(rs !=null && rs.size() > 0){
			String tabName;
			int tabId;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				Map<String, Object> attention = rs.get(i);
				//在非获取指定表下的评论列表的情况下的前面35个字符
				tabName = StringUtil.changeNotNull((attention.get("table_name")));
				tabId = StringUtil.changeObjectToInt(attention.get("table_id"));
				attention.put("type", EnumUtil.getTableCNName(tabName));
				attention.put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
				attention.put("link", commonHandler.getLinkByTableNameAndId(tabName, tabId, user.getId()));
			}
		}
		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		responseModel.setData(rs).setCount(SqlUtil.getTotalByList(attentionMapper.getTotalByUser(EnumUtil.DataTableType.关注.value, user.getId()))).code(0);
		return responseModel;
	}

	@Override
	public ResponseModel deleteAttention(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->deleteAttention():jo="+ jsonObject);
		long id = JsonUtil.getLongValue(jsonObject, "id", 0);
		ParameterUnspecificationUtil.checkLong(id , "id 必须大于0");

		AttentionBean attentionBean = attentionMapper.findById(AttentionBean.class, id);
		if(attentionBean == null)
			throw new NullPointerException("关注记录不存在");

		//检查是否是自己的记录
		if(attentionBean.getCreateUserId() != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		boolean result = this.attentionMapper.deleteById(AttentionBean.class, id) > 0;
		if(result)
			return new ResponseModel().ok().message("删除关注记录成功！");

		return new ResponseModel().error().message("删除关注记录失败！");
	}

	@Override
	public LayuiTableResponseModel collections(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->collections():jo="+ jsonObject);
		int current = JsonUtil.getIntValue(jsonObject, "page", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
		int start = SqlUtil.getPageStart(current -1, rows, 0);
		List<Map<String, Object>> rs = collectionMapper.getMyCollections(user.getId(), start, rows);
		if(rs !=null && rs.size() > 0){
			String tabName;
			int tabId;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				Map<String, Object> attention = rs.get(i);
				//在非获取指定表下的评论列表的情况下的前面35个字符
				tabName = StringUtil.changeNotNull((attention.get("table_name")));
				tabId = StringUtil.changeObjectToInt(attention.get("table_id"));
				attention.put("type", EnumUtil.getTableCNName(tabName));
				attention.put("source", commonHandler.getContentByTableNameAndId(tabName, tabId, user));
				attention.put("link", commonHandler.getLinkByTableNameAndId(tabName, tabId, user.getId()));
			}
		}
		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		responseModel.setData(rs).setCount(SqlUtil.getTotalByList(collectionMapper.getTotalByUser(EnumUtil.DataTableType.收藏.value, user.getId()))).code(0);
		return responseModel;
	}

	@Override
	public ResponseModel deleteCollection(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMyServiceImpl-->deleteCollection():jo="+ jsonObject);
		long id = JsonUtil.getLongValue(jsonObject, "id", 0);
		ParameterUnspecificationUtil.checkLong(id , "id 必须大于0");

		CollectionBean collectionBean = collectionMapper.findById(CollectionBean.class, id);
		if(collectionBean == null)
			throw new NullPointerException("收藏记录不存在");

		//检查是否是自己的记录
		if(collectionBean.getCreateUserId() != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		boolean result = this.collectionMapper.deleteById(CollectionBean.class, id) > 0;
		if(result)
			return new ResponseModel().ok().message("删除收藏记录成功！");

		return new ResponseModel().error().message("删除收藏记录失败！");
	}

	@Override
	public ResponseModel addLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("LogoutServiceImpl-->addLogout():json="+jo);
		LogoutBean logoutBean = new LogoutBean();
		logoutBean.setNote(JsonUtil.getStringValue(jo, "note"));
		logoutBean.setReason(JsonUtil.getStringValue(jo, "reason"));
		logoutBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		logoutBean.setCreateUserId(user.getId());
		logoutBean.setCreateTime(new Date());
		logoutBean.setModifyUserId(user.getId());
		logoutBean.setModifyTime(new Date());
		logoutBean.setOverdue(DateUtil.getOverdueTime(new Date(), "10秒钟"));
		boolean result = logoutMapper.save(logoutBean) > 0;
		//保存操作日志
		this.operateLogService.saveOperateLog(user, request, new Date(), "申请注销账号记录", "addLogout()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		if(result)
			return new ResponseModel().ok().message("申请通过！");
		else
			return new ResponseModel().error().message("申请不通过，请稍后重试！");
	}

	@Override
	public ResponseModel cancelLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("LogoutServiceImpl-->cancelLogout():json="+jo);
		LogoutBean logoutBean = logoutMapper.recode(user.getId());
		if(logoutBean == null)
			throw new NullPointerException("还没有申请注销账号记录");

		boolean result = logoutMapper.delete(logoutBean) > 0;
		//保存操作日志
		this.operateLogService.saveOperateLog(user, request, new Date(), "删除申请注销账号记录", "cancelLogout()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		if(result)
			return new ResponseModel().ok().message("删除申请记录通过！");
		else
			return new ResponseModel().error().message("删除申请记录不通过，请稍后重试！");
	}

	@Override
	public ResponseModel destroyLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("LogoutServiceImpl-->destroyLogout():json="+jo);
		LogoutBean logoutBean = logoutMapper.recode(user.getId());
		if(logoutBean == null)
			throw new NullPointerException("还没有申请注销账号记录");

		if(logoutBean.getOverdue().getTime() >= System.currentTimeMillis())
			throw new NullPointerException("还未满时间，无法销毁");
		//获取注销的数据
		List<LogoutTable> list = com.alibaba.fastjson.JSONArray.parseArray(StringUtil.changeNotNull(optionHandler.getData("logout", true)), LogoutTable.class);
		//对获取的数据进行排序
		Collections.sort(list, new Comparator<LogoutTable>(){
			@Override
			public int compare(LogoutTable o1, LogoutTable o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		if(CollectionUtil.isNotEmpty(list)){
			for(LogoutTable table: list){
				if(StringUtil.isNotNull(table.getField())){
					String deleteSql = "delete from "+ table.getTable()+ getLogoutWhereSql(table.getTable(), table.getField(), user);
//					System.out.println(deleteSql);
					logoutMapper.executeSQL(deleteSql);
				}
			}
		}
		//删除elasticsearch缓存
		long fiId = elasticSearchUtil.deleteByUser("t_mood", "create_user_id,modify_user_id", user.getId());
		elasticSearchUtil.deleteByUser("t_user", "id", user.getId());
		elasticSearchUtil.deleteByUser("t_blog", "create_user_id,modify_user_id", user.getId());

		//删除注销申请记录
		boolean result = logoutMapper.delete(logoutBean) > 0;
		//修改用户的状态
		user.setStatus(ConstantsUtil.STATUS_DELETE);
		result = userMapper.update(user) > 0;
//
		//保存操作日志
		this.operateLogService.saveOperateLog(user, request, new Date(), "注销账号", "destroyLogout()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);

		if(result){
			try{
				SessionManagerUtil.getInstance().removeSession(SecurityUtils.getSubject().getSession(), true);
			}catch(Exception e){
				logger.info("用户注销失败！");
			}
			return new ResponseModel().ok().message("该账号已经注销！");
		}else
			return new ResponseModel().error().message("该账号已经注销失败，请稍后重试！");
	}

	private String getLogoutWhereSql(String table, String field, UserBean user) {
		String[] fields = field.split(",");
		if(fields.length == 0)
			throw new IllegalArgumentException(table + "'s field must not null");
		StringBuffer buffer = new StringBuffer();
		buffer.append( " where " );
		for(String f: fields){
			buffer.append(" "+ f + " = " +user.getId() + " and");
		}
		return buffer.delete(buffer.length() - 3, buffer.length()).toString(); //删掉最后面的and
	}

	@Override
	public ResponseModel getLogout(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("LogoutServiceImpl-->getLogout():json="+jo);
		return new ResponseModel().ok().message(logoutMapper.recode(user.getId()));
	}

}
