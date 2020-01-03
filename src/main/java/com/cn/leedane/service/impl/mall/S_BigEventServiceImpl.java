package com.cn.leedane.service.impl.mall;

import com.cn.leedane.mapper.mall.S_BigEventMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_BigEventBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_BigEventService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 购物商品大事件的service的实现类
 * @author LeeDane
 * 2017年11月10日 下午12:41:13
 * version 1.0
 */
@Service("S_BigEventService")
public class S_BigEventServiceImpl extends MallRoleCheckService implements S_BigEventService<S_BigEventBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private S_BigEventMapper bigEventMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public ResponseModel save(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_BigEventServiceImpl-->save():jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_BigEventBean bigEventBean = (S_BigEventBean) sqlUtil.getBean(jo, S_BigEventBean.class);

		if(StringUtil.isNull(bigEventBean.getText()) || bigEventBean.getProductId() < 1)
			return new ResponseModel().error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value)).code(EnumUtil.ResponseCode.参数不存在或为空.value);

		String returnMsg = "大事件已经发布成功！";
		bigEventBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		Date createTime = new Date();
		bigEventBean.setCreateTime(createTime);
		bigEventBean.setCreateUserId(user.getId());
		
		boolean result = bigEventMapper.save(bigEventBean) > 0;
		ResponseModel responseModel = new ResponseModel();
		if(result)
			responseModel.ok().message(returnMsg);
		else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value)).code(EnumUtil.ResponseCode.数据库保存失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布商品的大事件:", bigEventBean.getText() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "save()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
	
	@Override
	public ResponseModel paging(long productId, JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("S_BigEventServiceImpl-->paging():jsonObject=" +jo.toString() +", productId=" +productId);
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
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取商品ID为：",productId,"的大事件列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		return new ResponseModel().ok().message(rs);
	}

}
