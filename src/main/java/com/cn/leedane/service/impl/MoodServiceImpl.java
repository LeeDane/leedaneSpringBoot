package com.cn.leedane.service.impl;

import com.cn.leedane.exception.MustLoginException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.*;
import com.cn.leedane.lucene.solr.MoodSolrHandler;
import com.cn.leedane.mapper.FilePathMapper;
import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.observer.ConcreteWatched;
import com.cn.leedane.observer.ConcreteWatcher;
import com.cn.leedane.observer.Watched;
import com.cn.leedane.observer.Watcher;
import com.cn.leedane.observer.template.UpdateMoodTemplate;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.AddReadSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.service.*;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;
import com.cn.leedane.thread.single.SolrAddThread;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 心情service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:47:53
 * Version 1.0
 */
@Service("moodService")
public class MoodServiceImpl extends AdminRoleCheckService implements MoodService<MoodBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private MoodMapper moodMapper;
	
	@Autowired
	private FilePathMapper filePathMapper;
	
	@Autowired
	private FilePathService<FilePathBean> filePathService;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private UserService<UserBean> userService;

	@Autowired
	private FriendService<FriendBean> friendService;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private CommentHandler commentHandler;
	
	@Autowired
	private TransmitHandler transmitHandler;
	
	@Autowired
	private ZanHandler zanHandler;
	
	@Autowired
	private MoodHandler moodHandler;
	
	@Autowired
	private CircleOfFriendsHandler circleOfFriendsHandler;
	
	@Autowired
	private FriendHandler friendHandler;

	@Autowired
	private NotificationHandler notificationHandler;

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;

	@Autowired
	private TransportClient transportClient;
	
	@Override
	public Map<String, Object> saveMood(JSONObject jsonObject, UserBean user, int status, HttpRequestInfoBean request){
		logger.info("MoodServiceImpl-->saveMood():jsonObject=" +jsonObject.toString() +", status=" +status);
		String content = JsonUtil.getStringValue(jsonObject, "content");
		ResponseMap message = new ResponseMap();
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message, request))
			return message.getMap();
		
		if(StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
				
		MoodBean moodBean = new MoodBean();
		moodBean.setContent(content);
		String location = JsonUtil.getStringValue(jsonObject, "location");
		if(StringUtil.isNotNull(location)){
			double longitude = JsonUtil.getDoubleValue(jsonObject, "longitude");
			double latitude = JsonUtil.getDoubleValue(jsonObject, "latitude");
			moodBean.setLocation(location);
			moodBean.setLongitude(longitude);
			moodBean.setLatitude(latitude);
		}
		moodBean.setCreateTime(new Date());
		moodBean.setFroms(JsonUtil.getStringValue(jsonObject, "froms"));
		moodBean.setPublishNow(true);
		moodBean.setStatus(status);
		moodBean.setCreateUserId(user.getId());
		String uuid = UUID.randomUUID().toString() + System.currentTimeMillis();
		moodBean.setUuid(uuid);
		
		String base64Str = JsonUtil.getStringValue(jsonObject, "base64");
		
		if(!StringUtil.isNull(base64Str)){
			String[] base64s = base64Str.split("&&");

			for(int i=0; i < base64s.length; i++){
				filePathService.saveEachFile(i, base64s[i], user, uuid, DataTableType.心情.value);
			}
			moodBean.setHasImg(true);
		}
		
		boolean result = moodMapper.save(moodBean) > 0;
		if(result){
			int i = moodBean.getId();
	        if(i < 0){
	        	//通过观察者的模式发送消息通知
				Watched watched = new ConcreteWatched();       
		        Watcher watcher = new ConcreteWatcher();
		        watched.addWatcher(watcher);
		        watched.notifyWatchers(userService.findById(user.getId()), new UpdateMoodTemplate());      
	        	message.put("message", i);
		        message.put("isSuccess", true);
	        }

			new ThreadUtil().singleTask(new EsIndexAddThread<MoodBean>(moodBean));

	        //异步添加心情solr索引
//	        new ThreadUtil().singleTask(new SolrAddThread<MoodBean>(MoodSolrHandler.getInstance(), moodBean));
	        //MoodSolrHandler.getInstance().addBean(moodBean);
	        
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		// 保存发表心情日志信息
		String subject = user.getAccount() + "发表了心情" + StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "saveMood()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	
	@Override
	public Map<String, Object> updateMoodStatus(JSONObject jsonObject, int status, HttpRequestInfoBean request, UserBean user) {
		int mid = JsonUtil.getIntValue(jsonObject, "mid");
		logger.info("MoodServiceImpl-->updateMoodStatus():mid=" +mid +", status=" +status + ",jsonObject="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		boolean result = false;

		if(mid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
		
		MoodBean oldMoodBean = moodMapper.findById(MoodBean.class, mid);
		if(oldMoodBean == null){
			//删除es缓存
			elasticSearchUtil.delete(DataTableType.心情.value, mid);
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该心情不存在.value));
		}
		checkAdmin(user, oldMoodBean.getCreateUserId());

		//设置es缓存为false
		oldMoodBean.setEsIndex(false);
		oldMoodBean.setStatus(status);
		try {
			//moodMapper.executeSQL("update "+DataTableType.心情.value+" set status = ? where id = ? ", status, mid);
			result = moodMapper.updateSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(DataTableType.心情.value)), " set status = ? where id = ? ", status, mid) > 0;
			if(status == ConstantsUtil.STATUS_NORMAL){
				//通过观察者的模式发送消息通知
				Watched watched = new ConcreteWatched();       
		        Watcher watcher = new ConcreteWatcher();
		        watched.addWatcher(watcher);
		        watched.notifyWatchers(userService.findById(user.getId()), new UpdateMoodTemplate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result){
			//删除该心情的缓存
			moodHandler.delete(mid, null, null);
			message.put("isSuccess", result);
			message.put("message", "更新心情状态成功");
			//异步修改心情solr索引
//	        new ThreadUtil().singleTask(new SolrUpdateThread<MoodBean>(MoodSolrHandler.getInstance(), oldMoodBean));

			//删除es缓存
			elasticSearchUtil.delete(DataTableType.心情.value, mid);
			new ThreadUtil().singleTask(new EsIndexAddThread<MoodBean>(oldMoodBean));
			//MoodSolrHandler.getInstance().updateBean(oldMoodBean);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> deleteMood(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		int mid = JsonUtil.getIntValue(jo, "mid");
		logger.info("MoodServiceImpl-->deleteMood():mid=" +mid +",jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		if(mid < 1) 
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			
		MoodBean moodBean = moodMapper.findById(MoodBean.class, mid);
		if(moodBean == null){
			//删除es缓存
			elasticSearchUtil.delete(DataTableType.心情.value, mid);
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该心情不存在.value));
		}
		
		checkAdmin(user, moodBean.getCreateUserId());
		
		String tableUuid = moodBean.getUuid();
		//有图片的先删掉图片
		if(moodBean != null && moodBean.isHasImg()){
			List<Map<String, Object>> list = filePathMapper.executeSQL("select f.id from "+DataTableType.文件.value+" f where f.table_uuid = ? and f.table_name=?", moodBean.getUuid(), DataTableType.心情.value);
		
			if(list != null && list.size() >0){
				int fid = 0;
				for(Map<String, Object> map: list){
					fid = StringUtil.changeObjectToInt(map.get("id"));
					if(fid >0)
						filePathMapper.deleteById(FilePathBean.class, fid);
				}
				
			}
		}
		
		boolean result = moodMapper.deleteById(MoodBean.class, mid) > 0;
		// 删除心情日志信息
		String subject = user.getAccount() + "删除了心情，心情id为:" + mid+StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "deleteMood()", StringUtil.changeBooleanToInt(result) , EnumUtil.LogOperateType.内部接口.value);
	
		if(result){

			//删除es缓存
			elasticSearchUtil.delete(DataTableType.心情.value, mid);

			moodHandler.delete(mid, DataTableType.心情.value, tableUuid);
			//同时删除朋友圈的数据
			circleOfFriendsHandler.deleteMyAndFansTimeLine(user, EnumUtil.DataTableType.心情.value, mid);
			
			//异步删除心情solr索引
//	        new ThreadUtil().singleTask(new SolrDeleteThread<MoodBean>(MoodSolrHandler.getInstance(), String.valueOf(mid)));
			//MoodSolrHandler.getInstance().deleteBean(String.valueOf(mid));
			message.put("message", "该心情删除成功");
			message.put("isSuccess", result);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> rolling(JSONObject jo,
			UserBean user, HttpRequestInfoBean request){
		logger.info("MoodServiceImpl-->rolling():jo=" +jo.toString());
		int toUserId = JsonUtil.getIntValue(jo, "to_user_id", user.getId()); //
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		String picSize = ConstantsUtil.DEFAULT_PIC_SIZE; //JsonUtil.getStringValue(jo, "pic_size"); //图像的规格(大小)		
				
		StringBuffer sql = new StringBuffer();
		ResponseMap message = new ResponseMap();
		
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select m.id, m.status, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img, m.can_comment, m.can_transmit,");
			sql.append(" m.read_number, m.location, m.longitude, m.latitude, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account, m.stick ");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where "+ getMoodStatusSQL(toUserId, user) +" and ");
			sql.append(" m.create_user_id = ?");
			sql.append(" order by m.id desc limit 0,?");
			rs = moodMapper.executeSQL(sql.toString(), toUserId, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select m.id, m.status, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img, m.can_comment, m.can_transmit,");
			sql.append(" m.read_number, m.location, m.longitude, m.latitude, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account, m.stick ");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where "+ getMoodStatusSQL(toUserId, user) +" and ");
			sql.append(" m.create_user_id = ?");
			sql.append(" and m.id < ? order by m.id desc limit 0,? ");
			rs = moodMapper.executeSQL(sql.toString(), toUserId, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select m.id, m.status, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img, m.can_comment, m.can_transmit,");
			sql.append(" m.read_number, m.location, m.longitude, m.latitude, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account, m.stick");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where "+ getMoodStatusSQL(toUserId, user) +" and ");
			sql.append(" m.create_user_id = ?");
			sql.append(" and m.id > ? limit 0,?  ");
			rs = moodMapper.executeSQL(sql.toString(), toUserId, firstId, pageSize);
		}
		
		if(CollectionUtil.isNotEmpty(rs)){
			boolean hasImg ;
			String uuid;
			int moodId;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				hasImg = StringUtil.changeObjectToBoolean(rs.get(i).get("has_img"));
				uuid = StringUtil.changeNotNull(rs.get(i).get("uuid"));
				moodId = StringUtil.changeObjectToInt(rs.get(i).get("id"));
				
				rs.get(i).put("zan_users", zanHandler.getZanUser(moodId, DataTableType.心情.value, user, 6));
				rs.get(i).put("comment_number", commentHandler.getCommentNumber(moodId, DataTableType.心情.value));
				rs.get(i).put("transmit_number", transmitHandler.getTransmitNumber(moodId, DataTableType.心情.value));
				rs.get(i).put("zan_number", zanHandler.getZanNumber(moodId, DataTableType.心情.value));
				
				
				//有图片的获取图片的路径
				if(hasImg && !StringUtil.isNull(uuid)){
					rs.get(i).put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, picSize));
				}
			}	
		}
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看用户id为"+toUserId+"个人中心", "rolling()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getMoodsPaging(JSONObject jo,
			UserBean user, HttpRequestInfoBean request){
		logger.info("MoodServiceImpl-->getMoodPaging():jo=" +jo.toString());
		int toUserId = JsonUtil.getIntValue(jo, "to_user_id", user.getId()); //
//		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jo, "current", 0); //当前的索引页
		int total = JsonUtil.getIntValue(jo, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		String picSize = ConstantsUtil.DEFAULT_PIC_SIZE; //JsonUtil.getStringValue(jo, "pic_size"); //图像的规格(大小)		
		ResponseMap message = new ResponseMap();

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		//登录的用户可以自己查询所有的心情
		BoolQueryBuilder boolQueryStatus = QueryBuilders.boolQuery();
		boolQueryStatus.should(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_NORMAL));
		boolQueryStatus.should(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_SHARE));
		//当前登录用户可以查看自己私有的心情
		if(toUserId == user.getId()){
			boolQueryStatus.should(QueryBuilders.termQuery("status", ConstantsUtil.STATUS_SELF));
		}
		boolQuery.must(boolQueryStatus);
		boolQuery.must(QueryBuilders.termQuery("create_user_id", toUserId));
		SearchRequestBuilder builder = transportClient.prepareSearch(ElasticSearchUtil.getDefaultIndexName(DataTableType.心情.value)).setTypes(DataTableType.心情.value)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(boolQuery)
				.setFrom(start)
				//为0表示只获取统计数，不获取详细的文档数据
				.setSize(pageSize)
				.setFetchSource(new String[]{"id", "status", "content", "froms", "uuid", "create_user_id", "create_time", "has_img", "can_comment",
						"can_transmit", "read_number", "location", "longitude", "latitude", "share_number", "stick"}, null);
		//控制是否新增排序
        builder.addSort("stick", SortOrder.DESC);
		builder.addSort("id", SortOrder.DESC);

		logger.info(builder.toString());

		SearchResponse response = builder.get();

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : response.getHits()) {
			Map<String, HighlightField> fieldMap = hit.getHighlightFields();
			result.add(hit.getSourceAsMap());
		}

//		rs = moodMapper.getMoodPaging(user.getId(), toUserId, start, pageSize, ConstantsUtil.STATUS_NORMAL, ConstantsUtil.STATUS_SELF);
		if(CollectionUtil.isNotEmpty(result)){
			boolean hasImg ;
			String uuid;
			int moodId;
			//为名字备注赋值
			for(Map<String, Object> map: result){
				hasImg = StringUtil.changeObjectToBoolean(map.get("has_img"));
				uuid = StringUtil.changeNotNull(map.get("uuid"));
				moodId = StringUtil.changeObjectToInt(map.get("id"));
				map.put("zan_users", zanHandler.getZanUser(moodId, DataTableType.心情.value, user, 6));
				map.put("comment_number", commentHandler.getCommentNumber(moodId, DataTableType.心情.value));
				map.put("transmit_number", transmitHandler.getTransmitNumber(moodId, DataTableType.心情.value));
				map.put("zan_number", zanHandler.getZanNumber(moodId, DataTableType.心情.value));
				map.put("account", userHandler.getUserName(StringUtil.changeObjectToInt(map.get("create_user_id"))));
				//有图片的获取图片的路径
				if(hasImg && !StringUtil.isNull(uuid)){
					map.put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, picSize));
				}
			}	
		}
		message.put("total", SqlUtil.getTotalByList(moodMapper.getTotal(DataTableType.心情.value, " m where create_user_id="+ toUserId +" and "+ getMoodStatusSQL(toUserId, user))));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看用户id为"+toUserId+"个人中心", "getMoodPaging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", result);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	
	/**
	 * 获取状态的SQL
	 * @param toUserId
	 * @param user
	 * @return
	 */
	private String getMoodStatusSQL(int toUserId, UserBean user){
		if(toUserId == user.getId()){
			return " (m.status = "+ ConstantsUtil.STATUS_NORMAL +" or m.status = "+ ConstantsUtil.STATUS_SELF +")";
		}else {
			return " m.status = "+ ConstantsUtil.STATUS_NORMAL;
		}
	}
	
	@Override
	public boolean saveBase64Str(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		boolean result = false;
		logger.info("MoodServiceImpl-->saveBase64Str():jo=" +jo.toString());
		return result;
	}

	@Override
	public Map<String, Object> saveDividedMood(JSONObject jsonObject, UserBean user, int status, HttpRequestInfoBean request){
		logger.info("MoodServiceImpl-->saveDividedMood():status=" +status +",jsonObject="+jsonObject.toString());
		String content = JsonUtil.getStringValue(jsonObject, "content");
		String uuid = JsonUtil.getStringValue(jsonObject, "uuid");
		String froms = JsonUtil.getStringValue(jsonObject, "froms");
		boolean hasImg = JsonUtil.getBooleanValue(jsonObject, "hasImg");
		ResponseMap message = new ResponseMap();
		//检查uuid是否已经存在了
		if(moodMapper.executeSQL("select id from "+DataTableType.心情.value+" where table_uuid =? ", uuid).size() > 0){
			String oldUUid = uuid;
			uuid = UUID.randomUUID().toString() + System.currentTimeMillis();
			filePathMapper.updateBatchFilePath(new String[]{oldUUid}, new String[]{uuid});
		}
		
		MoodBean moodBean = new MoodBean();
		moodBean.setContent(content);
		String location = JsonUtil.getStringValue(jsonObject, "location");
		if(StringUtil.isNotNull(location)){
			double longitude = JsonUtil.getDoubleValue(jsonObject, "longitude");
			double latitude = JsonUtil.getDoubleValue(jsonObject, "latitude");
			moodBean.setLocation(location);
			moodBean.setLongitude(longitude);
			moodBean.setLatitude(latitude);
		}
		moodBean.setCreateTime(new Date());
		moodBean.setCreateUserId(user.getId());
		moodBean.setFroms(froms);
		moodBean.setHasImg(hasImg);
		moodBean.setPublishNow(true);
		moodBean.setStatus(status);
		moodBean.setUuid(uuid);
		boolean result = moodMapper.save(moodBean) > 0;

        if(result){
			message.put("isSuccess", result);
			
			//异步添加心情solr索引
	        new ThreadUtil().singleTask(new SolrAddThread<MoodBean>(MoodSolrHandler.getInstance(), moodBean));
	        //MoodSolrHandler.getInstance().addBean(moodBean);
	        
			//通过观察者的模式发送消息通知
			/*Watched watched = new ConcreteWatched();       
	        Watcher watcher = new ConcreteWatcher();
	        watched.addWatcher(watcher);
	        watched.notifyWatchers(userService.findById(user.getId()), new UpdateMoodTemplate());*/
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		return message.getMap();
	}
	
	

	@Override
	public Map<String, Object> getCountByUser(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("MoodServiceImpl-->getCountByUser():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		int uid = JsonUtil.getIntValue(jo, "uid", user.getId()); //计算的用户id
		
		StringBuffer sql = new StringBuffer();
		sql.append(" where create_user_id = " + uid + " and status = " +ConstantsUtil.STATUS_NORMAL);
		int count = 0;
		count = SqlUtil.getTotalByList(moodMapper.getTotal(DataTableType.心情.value, sql.toString()));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查询用户ID为：", uid, "得到其已经发表成功的心情总数是：", count, "条").toString(), "getCountByUser()", ConstantsUtil.STATUS_NORMAL, 0);
		ResponseMap message = new ResponseMap();
		message.put("isSuccess", true);
		message.put("message", count);
		return message.getMap();
	}

	@Override
	public Map<String, Object> detail(JSONObject jo, UserBean user,
			HttpRequestInfoBean request, String picSize){
		logger.info("MoodServiceImpl-->detail():jsonObject=" +jo.toString() +", user=" + (user != null ? user.getAccount(): "未登录用户"));
		final int mid = JsonUtil.getIntValue(jo, "mid", 0); //心情ID
		ResponseMap message = new ResponseMap();
		if(mid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));

		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = moodHandler.getMoodDetail(mid, user);
		if(!CollectionUtils.isEmpty(list) && list.size() == 1){
			Map<String, Object> mood = list.get(0);
			int moodCreateUserId = StringUtil.changeObjectToInt(mood.get("create_user_id"));
			int status = StringUtil.changeObjectToInt(mood.get("status"));
			//非登录用户只能查看共享的心情

			if(status != ConstantsUtil.STATUS_SHARE){
				if(user == null)
					throw new MustLoginException();
			}
			if(user != null && moodCreateUserId != user.getId() && !isAdmin() && status == ConstantsUtil.STATUS_SELF)
				throw new UnauthorizedException("私有信息，您无法查看！");


			message.put("isSuccess", true);
			boolean hasImg = StringUtil.changeObjectToBoolean(mood.get("has_img"));
			String uuid = StringUtil.changeNotNull(mood.get("uuid"));
			//有图片的获取图片的路径
			if(hasImg && !StringUtil.isNull(uuid)){
				mood.put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, picSize));
			}

			//从Redis缓存直接获取
			message.put("message", list);

			//把更新读的信息提交到Rabbitmq队列处理
			new Thread(new Runnable() {

				@Override
				public void run() {
					ISend send = new AddReadSend(moodMapper.findById(MoodBean.class, mid));
					SendMessage sendMessage = new SendMessage(send);
					sendMessage.sendMsg();
				}
			}).start();
		}else{
			//再次清空redis缓存
			moodHandler.delete(mid, null, null);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作对象不存在.value);
		}

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取心情ID为", mid, "的详情").toString(), "detail()", ConstantsUtil.STATUS_NORMAL, 0);

		return message.getMap();
	}

	@Override
	public Map<String, Object> sendWord(JSONObject jsonObject, UserBean user, int status,HttpRequestInfoBean request) {
		logger.info("MoodServiceImpl-->sendWord():jsonObject=" +jsonObject.toString());
		String content = JsonUtil.getStringValue(jsonObject, "content");
		//从客户端获取uuid(有图片的情况下)，为空表示无图，有图就有值
		String uuid = JsonUtil.getStringValue(jsonObject, "uuid");
				
		ResponseMap message = new ResponseMap();
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message, request))
			return message.getMap();
		
		
		//没有图片并且内容为空就报错返回
		if(StringUtil.isNull(content) && StringUtil.isNull(uuid)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		
		MoodBean moodBean = new MoodBean();
		moodBean.setContent(content);
		String location = JsonUtil.getStringValue(jsonObject, "location");
		if(StringUtil.isNotNull(location)){
			double longitude = JsonUtil.getDoubleValue(jsonObject, "longitude");
			double latitude = JsonUtil.getDoubleValue(jsonObject, "latitude");
			moodBean.setLocation(location);
			moodBean.setLongitude(longitude);
			moodBean.setLatitude(latitude);
		}
		moodBean.setCreateTime(new Date());
		moodBean.setFroms(JsonUtil.getStringValue(jsonObject, "froms"));
		moodBean.setPublishNow(true);
		moodBean.setStatus(status);
		moodBean.setCreateUserId(user.getId());
		moodBean.setUuid(uuid);
		moodBean.setCanComment(JsonUtil.getBooleanValue(jsonObject, "can_comment", true));
		moodBean.setCanTransmit(JsonUtil.getBooleanValue(jsonObject, "can_transmit", true));
		if(!StringUtil.isNull(uuid)){
			moodBean.setHasImg(true);
		}
		
		boolean result = moodMapper.save(moodBean) > 0;
		if(result){
			new ThreadUtil().singleTask(new EsIndexAddThread<MoodBean>(moodBean));

			//异步添加心情solr索引
//	        new ThreadUtil().singleTask(new SolrAddThread<MoodBean>(MoodSolrHandler.getInstance(), moodBean));
			//MoodSolrHandler.getInstance().addBean(moodBean);
			
			/*//通过观察者的模式发送消息通知
			Watched watched = new ConcreteWatched();       
	        Watcher watcher = new ConcreteWatcher();
	        watched.addWatcher(watcher);
	        watched.notifyWatchers(userService.findById(user.getId()), new UpdateMoodTemplate());*/
			TimeLineBean timeLineBean = new TimeLineBean();
			timeLineBean.setContent(moodBean.getContent());
			timeLineBean.setCreateTime(DateUtil.DateToString(new Date()));
			timeLineBean.setCreateUserId(moodBean.getCreateUserId());
			timeLineBean.setFroms(moodBean.getFroms());
			timeLineBean.setSource(null);
			timeLineBean.setHasSource(false);
			timeLineBean.setTableId(moodBean.getId());
			timeLineBean.setTableName(EnumUtil.DataTableType.心情.value);
			//更新用户的时间线
			circleOfFriendsHandler.upDateMyAndFansTimeLine(timeLineBean);
			
			//有@人通知相关人员
			Set<String> usernames = StringUtil.getAtUserName(content);
			if(usernames.size() > 0){
				//String str = "{from_user_remark}在发表的心情中@您,点击查看详情";
				notificationHandler.sendNotificationByNames(false, user, usernames, content, NotificationType.艾特我, DataTableType.心情.value, moodBean.getId(), moodBean);
			}
			message.put("isSuccess", result);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.发表心情成功.value));
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		
		// 保存发表心情日志信息
		String subject = user.getAccount() + "发表了心情" + StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "sendWord", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		
		return message.getMap();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object> sendWordAndLink(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request) {
		logger.info("MoodServiceImpl-->sendWordAndLink():jsonObject=" +jsonObject.toString());
		String content = JsonUtil.getStringValue(jsonObject, "content");
		String links = JsonUtil.getStringValue(jsonObject, "links"); //多张以“;”分开,必须
				
		ResponseMap message = new ResponseMap();
		
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message, request))
			return message.getMap();
		
		
		//没有图片链接并且内容为空就报错返回
		if(StringUtil.isNull(content) && StringUtil.isNull(links)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		boolean result = false;
		
		//生成统一的uuid
		String uuid =  user.getAccount() + "links" + UUID.randomUUID().toString();
		
		//先保存图片记录
		String[] linkArray = links.split(";");
		FilePathBean filePathBean = null;
		long[] widthAndHeight;
		int width = 0, height = 0;
		long length = 0;
		for(int i = 0; i< linkArray.length; i++){
			//判断是否是图片
			if(ImageUtil.isSupportType(linkArray[i])){
				//获取网络图片
				widthAndHeight = FileUtil.getNetWorkImgAttr(linkArray[i]);
				if(widthAndHeight.length == 3){
					width = (int) widthAndHeight[0];
					height = (int) widthAndHeight[1];
					length = widthAndHeight[2];
				}
				
				filePathBean = new FilePathBean();
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUserId(user.getId());
				filePathBean.setPicOrder(i);
				filePathBean.setPath(StringUtil.getFileName(linkArray[i]));
				filePathBean.setQiniuPath(linkArray[i]);
				filePathBean.setUploadQiniu(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setPicSize(ConstantsUtil.DEFAULT_PIC_SIZE); //source
				filePathBean.setWidth(width);
				filePathBean.setHeight(height);
				filePathBean.setLenght(length);
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(DataTableType.心情.value);
				filePathBean.setTableUuid(uuid);
				result = filePathMapper.save(filePathBean) > 0;
			}else{
				filePathBean = new FilePathBean();
				filePathBean.setCreateTime(new Date());
				filePathBean.setCreateUserId(user.getId());
				filePathBean.setPicOrder(i);
				filePathBean.setPath(StringUtil.getFileName(linkArray[i]));
				filePathBean.setQiniuPath(linkArray[i]);
				filePathBean.setUploadQiniu(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setPicSize(ConstantsUtil.DEFAULT_PIC_SIZE); //source
				filePathBean.setStatus(ConstantsUtil.STATUS_NORMAL);
				filePathBean.setTableName(DataTableType.心情.value);
				filePathBean.setTableUuid(uuid);
				result = filePathMapper.save(filePathBean) > 0;
			}
		}
		
		if(result){
			MoodBean moodBean = new MoodBean();
			moodBean.setContent(MardownUtil.parseHtml(content));
			String location = JsonUtil.getStringValue(jsonObject, "location");
			if(StringUtil.isNotNull(location)){
				double longitude = JsonUtil.getDoubleValue(jsonObject, "longitude");
				double latitude = JsonUtil.getDoubleValue(jsonObject, "latitude");
				moodBean.setLocation(location);
				moodBean.setLongitude(longitude);
				moodBean.setLatitude(latitude);
			}
			moodBean.setCreateTime(new Date());
			String froms = request.getLocation();
			if(StringUtil.isNull(froms) || "未知".equalsIgnoreCase(froms)){
				froms = StringUtil.changeNotNull(JsonUtil.getStringValue(jsonObject, "froms"));
			}
			moodBean.setFroms(froms);
			moodBean.setPublishNow(true);
			moodBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			moodBean.setCreateUserId(user.getId());
			moodBean.setCanComment(JsonUtil.getBooleanValue(jsonObject, "can_comment", true));
			moodBean.setCanTransmit(JsonUtil.getBooleanValue(jsonObject, "can_transmit", true));
			moodBean.setHasImg(true);
			moodBean.setUuid(uuid);
			
			result = moodMapper.save(moodBean) > 0;
			if(result){
				new ThreadUtil().singleTask(new EsIndexAddThread<MoodBean>(moodBean));

				//异步添加心情solr索引
//		        new ThreadUtil().singleTask(new SolrAddThread<MoodBean>(MoodSolrHandler.getInstance(), moodBean));
				//MoodSolrHandler.getInstance().addBean(moodBean);

				TimeLineBean timeLineBean = new TimeLineBean();
				timeLineBean.setContent(moodBean.getContent());
				timeLineBean.setCreateTime(DateUtil.DateToString(new Date()));
				timeLineBean.setCreateUserId(moodBean.getCreateUserId());
				timeLineBean.setFroms(moodBean.getFroms());
				timeLineBean.setSource(null);
				timeLineBean.setHasSource(false);
				timeLineBean.setTableId(moodBean.getId());
				timeLineBean.setTableName(EnumUtil.DataTableType.心情.value);
				//更新用户的时间线
				circleOfFriendsHandler.upDateMyAndFansTimeLine(timeLineBean);

				//有@人通知相关人员
				Set<String> usernames = StringUtil.getAtUserName(content);
				if(usernames.size() > 0){
					//String str = "{from_user_remark}在发表的心情中@您,点击查看详情";
					notificationHandler.sendNotificationByNames(false, user, usernames, content, NotificationType.艾特我, DataTableType.心情.value, moodBean.getId(), moodBean);
				}
				message.put("isSuccess", result);
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.发表心情成功.value));
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
				message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
			}
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.心情图片链接处理失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.心情图片链接处理失败.value);
		}
		
		// 保存发表心情日志信息
		String subject = user.getAccount() + "发表了心情" + StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "sendWordAndLink", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> detailImgs(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("MoodServiceImpl-->detailImgs():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		int mid = JsonUtil.getIntValue(jo, "mid", 0); //心情ID
		ResponseMap message = new ResponseMap();
		if(mid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			
		String tableName = JsonUtil.getStringValue(jo, "table_name"); //数据量表名
		String tableUuid = JsonUtil.getStringValue(jo, "table_uuid"); //uuid
		message.put("imgs", moodHandler.getMoodImg(tableName, tableUuid, ConstantsUtil.DEFAULT_PIC_SIZE));
		message.put("isSuccess", true);
		//list = moodHandler.getMoodIms(tableName, tableUuid);
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取心情ID为", mid, "的图像列表").toString(), "detailImgs()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}

	@Override
	public Map<String, Object> search(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("MoodServiceImpl-->search():jo="+jo.toString());
		String searchKey = JsonUtil.getStringValue(jo, "searchKey");
		ResponseMap message = new ResponseMap();
		
		if(StringUtil.isNull(searchKey)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.检索关键字不能为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.检索关键字不能为空.value);
			return message.getMap();
		}
		
		List<Map<String, Object>> rs = moodMapper.executeSQL("select id, content, uuid, froms, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, has_img , can_comment, can_transmit, stick from "+DataTableType.心情.value+" where status=? and content like '%"+searchKey+"%' order by create_time desc limit 25", ConstantsUtil.STATUS_NORMAL);
		if(rs != null && rs.size() > 0){
			int createUserId = 0;
			boolean hasImg;
			String uuid;
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				hasImg = StringUtil.changeObjectToBoolean(rs.get(i).get("has_img"));
				uuid = StringUtil.changeNotNull(rs.get(i).get("uuid"));
				
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				//有图片的获取图片的路径
				if(hasImg && !StringUtil.isNull(uuid)){
					rs.get(i).put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, ConstantsUtil.DEFAULT_PIC_SIZE));
				}
			}
		}
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> shakeSearch(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("MoodServiceImpl-->shakeSearch():jo="+jo.toString());
		int moodId = 0; //获取到的心情的ID
		ResponseMap message = new ResponseMap();
		
		moodId = moodMapper.shakeSearch(user.getId(), ConstantsUtil.STATUS_NORMAL);
		if(moodId > 0 ){
			message.put("isSuccess", true);
			List<Map<String, Object>> rs = moodHandler.getMoodDetail(moodId, user);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			if(CollectionUtil.isNotEmpty(rs)){
				Map<String, Object> m = rs.get(0);
				String uuid = StringUtil.changeNotNull(m.get("uuid"));
				if(StringUtil.changeObjectToBoolean(m.get("has_img")) && !StringUtil.isNull(uuid)){
					m.put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, ConstantsUtil.DEFAULT_PIC_SIZE));
				}
				resultMap.putAll(m);
			}
			message.put("message", resultMap);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
	
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有更多数据.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有更多数据.value);
		}
			
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr("账号为", user.getAccount() , "摇一摇搜索，得到心情Id为"+ moodId, StringUtil.getSuccessOrNoStr(moodId > 0)).toString(), "shakeSearch()", StringUtil.changeBooleanToInt(moodId > 0), 0);
		return message.getMap();
	}
	
	@Override
	public List<Map<String, Object>> executeSQL(String sql, Object... params) {
		return moodMapper.executeSQL(sql, params);
	}

	@Override
	public Map<String, Object> getTopicByLimit(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("MoodServiceImpl-->getTopicByLimit():jo=" +jo.toString());
		long start = System.currentTimeMillis();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		String picSize = ConstantsUtil.DEFAULT_PIC_SIZE; //图像的规格(大小)	
		String topic = JsonUtil.getStringValue(jo, "topic");
				
		StringBuffer sql = new StringBuffer();
		ResponseMap message = new ResponseMap();
		
		if(StringUtil.isNull(topic))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.话题不能为空.value));
		
		if(!topic.startsWith("#") && !topic.endsWith("#"))
			topic = "#" + topic + "#";
		
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select m.id, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img, m.can_comment, m.can_transmit,");
			sql.append(" m.read_number, m.location, m.longitude, m.latitude, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account, m.stick ");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
			sql.append(" m.content like '%"+topic+"%'");
			sql.append(" order by m.id desc limit 0,?");
			rs = moodMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select m.id, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img, m.can_comment, m.can_transmit,");
			sql.append(" m.read_number, m.location, m.longitude, m.latitude, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account, m.stick ");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
			sql.append(" m.content like '%"+topic+"%'");
			sql.append(" and m.id < ? order by m.id desc limit 0,? ");
			rs = moodMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select m.id, m.content, m.froms, m.uuid, m.create_user_id, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time, m.has_img, m.can_comment, m.can_transmit,");
			sql.append(" m.read_number, m.location, m.longitude, m.latitude, m.zan_number, m.comment_number, m.transmit_number, m.share_number, u.account, m.stick ");
			sql.append(" from "+DataTableType.心情.value+" m inner join "+DataTableType.用户.value+" u on u.id = m.create_user_id where m.status = ? and ");
			sql.append(" m.content like '%"+topic+"%'");
			sql.append(" and m.id > ? limit 0,?  ");
			rs = moodMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
		}
		
		if(rs !=null && rs.size() > 0){
			boolean hasImg ;
			String uuid;
			int moodId;
			int createUserId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				hasImg = StringUtil.changeObjectToBoolean(rs.get(i).get("has_img"));
				uuid = StringUtil.changeNotNull(rs.get(i).get("uuid"));
				moodId = StringUtil.changeObjectToInt(rs.get(i).get("id"));
				if(createUserId> 0){
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
					if(createUserId == user.getId()){
						rs.get(i).put("account", "本人");
					}
				}
				rs.get(i).put("zan_users", zanHandler.getZanUser(moodId, DataTableType.心情.value, user, 6));
				rs.get(i).put("comment_number", commentHandler.getCommentNumber(moodId, DataTableType.心情.value));
				rs.get(i).put("transmit_number", transmitHandler.getTransmitNumber(moodId, DataTableType.心情.value));
				rs.get(i).put("zan_number", zanHandler.getZanNumber(moodId, DataTableType.心情.value));
				
				
				//有图片的获取图片的路径
				if(hasImg && !StringUtil.isNull(uuid)){
					rs.get(i).put("imgs", moodHandler.getMoodImg(DataTableType.心情.value, uuid, picSize));
				}
			}	
		}
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看话题"+topic, "getTopicByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		
		long end = System.currentTimeMillis();
		logger.info("获取话题列表总计耗时：" +(end - start) +"毫秒，总数是："+rs.size());
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public List<MoodBean> getMoodBeans(String sql, Object... params) {
		return  moodMapper.getBeans(sql, params);
	}

	@Override
	public Map<String, Object> updateMoodStick(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request) {
		int mid = JsonUtil.getIntValue(jsonObject, "mid");
		boolean staick = JsonUtil.getBooleanValue(jsonObject, "stick");
		logger.info("MoodServiceImpl-->updateMoodStick():mid=" +mid +"" + ",jsonObject="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		boolean result = false;

		if(mid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));

		MoodBean oldMoodBean = moodMapper.findById(MoodBean.class, mid);
		if(oldMoodBean == null){
			//删除es缓存
			elasticSearchUtil.delete(DataTableType.心情.value, mid);
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该心情不存在.value));
		}
		checkAdmin(user, oldMoodBean.getCreateUserId());

		//
		if(staick){
			//获取排序的最大数
			oldMoodBean.setStick(moodMapper.getMaxStick(user.getId()) + 1);
		}else{
			oldMoodBean.setStick(0);
		}

		//设置es缓存为false
		oldMoodBean.setEsIndex(false);
		oldMoodBean.setModifyUserId(user.getId());
		oldMoodBean.setModifyTime(new Date());
		result = moodMapper.update(oldMoodBean)> 0;
		if(result){
			//删除该心情的缓存
			moodHandler.delete(mid, null, null);
			message.put("isSuccess", result);
			message.put("message", "更新心情置顶状态成功");
			//异步修改心情solr索引
//	        new ThreadUtil().singleTask(new SolrUpdateThread<MoodBean>(MoodSolrHandler.getInstance(), oldMoodBean));

			//删除es缓存
			elasticSearchUtil.delete(DataTableType.心情.value, mid);
			new ThreadUtil().singleTask(new EsIndexAddThread<MoodBean>(oldMoodBean));
			//MoodSolrHandler.getInstance().updateBean(oldMoodBean);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		return message.getMap();
	}
}
