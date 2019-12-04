package com.cn.leedane.service.impl;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.ChatSquareMapper;
import com.cn.leedane.model.ChatSquareBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.ChatSquareService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 聊天广场service的实现类
 * @author LeeDane
 * 2017年2月10日 下午4:12:06
 * Version 1.0
 */
@Service("chatSquareService")
public class ChatSquareServiceImpl extends AdminRoleCheckService implements ChatSquareService<ChatSquareBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private ChatSquareMapper chatSquareMapper;

	@Override
	public Map<String, Object> getLimit(JSONObject jo, HttpRequestInfoBean request) {
		logger.info("ChatSquareServiceImpl-->getLimit():jsonObject=" +jo.toString());
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		long last = JsonUtil.getLongValue(jo, "last", 0); //当前的索引页
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> result = chatSquareMapper.paging(ConstantsUtil.STATUS_NORMAL, last, pageSize);
		if(CollectionUtil.isNotEmpty(result)){
			for(Map<String, Object> map: result){
				map.putAll(userHandler.getBaseUserInfo(StringUtil.changeObjectToInt(map.get("create_user_id"))));
			}
		}
		message.put("message", result);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public Map<String, Object> addChatSquare(int userId, String msg) {
		logger.info("ChatSquareServiceImpl-->addChatSquare():message=" +msg);
		JSONObject jsonObject = JSONObject.fromObject(msg);
		ChatSquareBean bean = new ChatSquareBean();
		bean.setContent(jsonObject.getString("content"));
		bean.setCreateUserId(userId);
		bean.setCreateTime(new Date());
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		if(jsonObject.has("at_other")){
			bean.setAtOther(jsonObject.getString("at_other"));
		}
		if(jsonObject.has("type")){
			bean.setType(jsonObject.getString("type"));
		}
		chatSquareMapper.save(bean);
		return null;
	}

	@Override
	public Map<String, Object> getActiveUser(Date date, int top) {
		logger.info("ChatSquareServiceImpl-->getActiveUser()");
		ResponseMap message = new ResponseMap();
		message.put("isSuccess", true);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(id) count, create_user_id from t_chat_square WHERE create_time > ?");
		sql.append(" group by create_user_id HAVING COUNT(id) ORDER BY COUNT(id) DESC LIMIT 0,?");
		List<Map<String, Object>> rs = chatSquareMapper.executeSQL(sql.toString(), DateUtil.DateToString(date), top);
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> map: rs){
				int userId = StringUtil.changeObjectToInt(map.get("create_user_id"));
				map.putAll(userHandler.getBaseUserInfo(userId));
			}
		}
		message.put("message", rs);
		return message.getMap();
	}
	
}