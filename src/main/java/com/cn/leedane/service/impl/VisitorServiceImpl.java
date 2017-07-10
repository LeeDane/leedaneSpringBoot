package com.cn.leedane.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.handler.FanHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.VisitorMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.VisitorDeleteThread;
import com.cn.leedane.thread.single.VisitorSaveThread;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.RelativeDateFormat;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
/**
 * 访客service的实现类
 * @author LeeDane
 * 2017年5月11日 下午4:38:10
 * version 1.0
 */
@Service("visitorService")
public class VisitorServiceImpl extends AdminRoleCheckService implements VisitorService<VisitorBean>{
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private VisitorMapper visitorMapper;
	
	@Autowired
	private FriendHandler friendHandler;
	
	@Autowired
	private FanHandler fanHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public boolean saveVisitor(final UserBean user, final String froms, final String tableName, final int tableId, final int status){
		if(user == null)
			return false;
		new ThreadUtil().singleTask(new VisitorSaveThread(user, froms, tableName, tableId, status));
		return true;
	}
	

	@Override
	public boolean deleteVisitor(final UserBean user, final String tableName, final int tableId){
		if(user == null)
			return false;
		new ThreadUtil().singleTask(new VisitorDeleteThread(user, tableName, tableId));
		return true;
	}
	
	@Override
	public Map<String, Object> getVisitorsByLimit(int tableId, JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("VisitorServiceImpl-->getVisitorsByLimit():uid=" +tableId +", json="+json.toString());
		ResponseMap message = new ResponseMap();
		
		String tableName = JsonUtil.getStringValue(json, "table_name"); //操作表名
		int pageSize = JsonUtil.getIntValue(json, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(json, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(json, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();

		rs = visitorMapper.visitors(tableName, tableId, start, pageSize, ConstantsUtil.STATUS_NORMAL);
		if(rs !=null && rs.size() > 0){
			int createUserId = 0;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				//判断是否是好友关系
				if(createUserId > 0){
					rs.get(i).put("is_friend", friendHandler.inFriend(user.getId(), createUserId));
					rs.get(i).put("is_fan", fanHandler.inAttention(user.getId(), createUserId));
					rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				}
				rs.get(i).put("create_time", RelativeDateFormat.format(DateUtil.stringToDate(String.valueOf(rs.get(i).get("create_time")))));
			}	
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取表名：",tableName,"，表id为：",tableId,"的访客列表").toString(), "getCommentByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}
	
	@Override
	public int getTodayVisitors(String tableName, int tableId) {
		logger.info("VisitorServiceImpl-->getTodayVisitor():tableName = "+ tableName +", tableId=" +tableId);
		Date today = DateUtil.getTodayStart();
		return visitorMapper.getTodayVisitors(today, tableName, tableId, ConstantsUtil.STATUS_NORMAL);
	}
	
	@Override
	public int getAllVisitors(String tableName, int tableId) {
		logger.info("VisitorServiceImpl-->getAllVisitors():tableName = "+ tableName +", tableId=" +tableId);
		return visitorMapper.getAllVisitors(tableName, tableId, ConstantsUtil.STATUS_NORMAL);
	}
}
