package com.cn.leedane.service.impl;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.EventHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.EventMapper;
import com.cn.leedane.model.EventBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.EventService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 大事件service实现类
 * @author LeeDane
 * 2016年7月19日 下午19:47:53
 * Version 1.0
 */
@Service("eventService")
public class EventServiceImpl extends AdminRoleCheckService implements EventService<EventBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private EventMapper eventMapper;
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private UserService<UserBean> userService;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private EventHandler eventHandler;

	@Override
	public Map<String, Object> add(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("EventServiceImpl-->add():jsonObject=" +jsonObject.toString());
		checkAdmin(user);//必须管理员才能操作

		ResponseMap message = new ResponseMap();

		String content = JsonUtil.getStringValue(jsonObject, "content");
		if(StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message, request))
			return message.getMap();

		SqlUtil sqlUtil = new SqlUtil();
		EventBean eventBean = (EventBean) sqlUtil.getBean(jsonObject, EventBean.class);
		eventBean.setCreateTime(new Date());
		eventBean.setCreateUserId(user.getId());
		eventBean.setStatus(ConstantsUtil.STATUS_NORMAL);

		boolean result = eventMapper.save(eventBean) > 0;
		if(result){
			//清空缓存
			eventHandler.delete();
			message.put("isSuccess", true);
			message.put("message", "添加事件成功");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		this.operateLogService.saveOperateLog(user, request, new Date(), "添加大事件", "add()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(JSONObject jsonObject, HttpRequestInfoBean request, UserBean user) {
		int eid = JsonUtil.getIntValue(jsonObject, "eid");
		logger.info("EventServiceImpl-->update():eid=" +eid + ",jsonObject="+jsonObject.toString());
		checkAdmin(user);//必须管理员才能操作
		ResponseMap message = new ResponseMap();

		String content = JsonUtil.getStringValue(jsonObject, "content");
		if(StringUtil.isNull(content)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		//进行敏感词过滤和emoji过滤
		if(FilterUtil.filter(content, message, request))
			return message.getMap();

		EventBean oldEventBean = eventMapper.findById(EventBean.class, eid);
		if(oldEventBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该事件不存在.value));

		SqlUtil sqlUtil = new SqlUtil();
		EventBean eventBean = (EventBean) sqlUtil.getUpdateBean(jsonObject, oldEventBean);
		eventBean.setModifyUserId(user.getId());
		eventBean.setModifyTime(new Date());

		boolean result = eventMapper.update(eventBean) > 0;

		if(result){
			//清空缓存
			eventHandler.delete();
			message.put("isSuccess", true);
			message.put("message", "修改事件成功");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		this.operateLogService.saveOperateLog(user, request, new Date(), "修改大事件， id="+ eid, "update()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		int eid = JsonUtil.getIntValue(jo, "eid");
		logger.info("EventServiceImpl-->delete():eid=" + eid +",jo="+jo.toString());
		checkAdmin(user);//必须管理员才能操作
		ResponseMap message = new ResponseMap();
		if(eid < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作对象不存在.value));

		boolean result = eventMapper.deleteById(EventBean.class, eid) > 0;
		if(result){

			//清空缓存
			eventHandler.delete();
			message.put("isSuccess", true);
			message.put("message", "删除事件成功");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);

		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		this.operateLogService.saveOperateLog(user, request, new Date(), "删除大事件， id="+ eid, "delete()", StringUtil.changeBooleanToInt(result), EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> events(JSONObject jo,
			UserBean user, HttpRequestInfoBean request){
		logger.info("EventServiceImpl-->events():jo=" +jo.toString());
		int toUserId = JsonUtil.getIntValue(jo, "to_user_id", user.getId()); //
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int pageSize = JsonUtil.getIntValue(jo, "limit", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jo, "page", 0); //当前的索引页
		int total = JsonUtil.getIntValue(jo, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex -1, pageSize, total);
		ResponseMap message = new ResponseMap();
		rs = eventMapper.events(start, pageSize, ConstantsUtil.STATUS_NORMAL);
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> map: rs){
				map.put("account", userHandler.getUserName(StringUtil.changeObjectToInt(map.get("create_user_id"))));
//				map.put("content", JsoupUtil.getInstance().getContentNoTag(StringUtil.changeNotNull(map.get("content"))));
			}

		}

		/*for(int i = 0; i < 8; i++){
			Map<String, Object> item = new HashMap<>();
			item.put("id", 10001+ i);
			item.put("account", "杜甫"+ i);
			item.put("content", "这个是内容，这个是内容，这个是内容，<span>哈哈</span>艰苦，这个是内容，这个是内容，这个是内容，<span>哈哈</span>艰苦，这个是内容，这个是内容，这个是内容，<span>哈哈</span>艰苦，"+ i);
			item.put("source", "##这个是源文件信息<span>哈哈</span>，这个是源文件信息<span>哈哈</span>,这个是源文件信息<span>哈哈</span>,这个是源文件信息<span>哈哈</span>,这个是源文件信息<span>哈哈</span>,这个是源文件信息<span>哈哈</span>"+ i);
			item.put("modifyTime", "2016-10-0"+ i);
			rs.add(item);

		}*/

		message.put("data", rs);
		message.put("count", SqlUtil.getTotalByList(eventMapper.getTotal(DataTableType.大事件.value, " e where create_user_id="+ toUserId)));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"查看用户id为"+toUserId+"个人中心", "getMoodPaging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("msg", "");
		message.put("code", 0);
		return message.getMap();
	}

	@Override
	public Map<String, Object> all(JSONObject jo,
									  UserBean user, HttpRequestInfoBean request){
		logger.info("EventServiceImpl-->all():jo=" +jo.toString());
		ResponseMap message = new ResponseMap();
		message.put("isSuccess", true);
		message.put("message", eventHandler.get());
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}
}
