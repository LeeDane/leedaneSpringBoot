package com.cn.leedane.service.impl.shop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.shop.S_BigEventMapper;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.shop.S_BigEventBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.shop.S_BigEventService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;

/**
 * 购物商品大事件的service的实现类
 * @author LeeDane
 * 2017年11月10日 下午12:41:13
 * version 1.0
 */
@Service("S_BigEventService")
public class S_BigEventServiceImpl extends AdminRoleCheckService implements S_BigEventService<S_BigEventBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private S_BigEventMapper bigEventMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public Map<String, Object> save(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		
		logger.info("S_BigEventServiceImpl-->save():jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_BigEventBean bigEventBean = (S_BigEventBean) sqlUtil.getBean(jo, S_BigEventBean.class);

		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(bigEventBean.getText()) || bigEventBean.getProductId() < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		String returnMsg = "大事件已经发布成功！";
		bigEventBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		Date createTime = new Date();
		bigEventBean.setCreateTime(createTime);
		bigEventBean.setCreateUserId(user.getId());
		
		boolean result = bigEventMapper.save(bigEventBean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布商品的大事件:", bigEventBean.getText() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "save()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> paging(int productId, JSONObject jo, UserBean user, HttpServletRequest request){
		logger.info("S_BigEventServiceImpl-->paging():jsonObject=" +jo.toString() +", productId=" +productId);
		ResponseMap message = new ResponseMap();
		if(user == null)
			user = OptionUtil.adminUser;
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int currentIndex = JsonUtil.getIntValue(jo, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jo, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		rs = bigEventMapper.getEvents(productId, ConstantsUtil.STATUS_NORMAL, start, pageSize);
			
		if(rs !=null && rs.size() > 0){
			
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取商品ID为：",productId,"的大事件列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("message", rs);
		return message.getMap();
	}

}
